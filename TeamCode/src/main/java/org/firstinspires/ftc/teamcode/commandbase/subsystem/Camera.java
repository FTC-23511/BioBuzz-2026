package org.firstinspires.ftc.teamcode.commandbase.subsystem;

import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.ALIGN_UPDATE_SECONDS;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.CAMERA_PITCH_OFFSET;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.DESIRED_TAG_ID;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.FILTER_ALPHA;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.StartEndCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Webcam + AprilTag pipeline. Detection/filtering runs on a dedicated background thread (mirroring
 * the polling cadence the original vision thread used) so it never blocks the command scheduler
 * loop.
 *
 * <p>The original OpMode drove the robot to the tag itself using Follower#followPath /
 * #breakFollowing / #setMaxPower. Those methods don't exist on this project's PedroPathing version
 * (com.pedropathing:core:3.0.0-SNAPSHOT), which replaced them with a Path/Curve/Modifier builder
 * API. Auto-align is stubbed out below - {@link #isAligning()} still reports state for
 * telemetry/RGB, and tag offsets are still filtered, but nothing drives the robot yet. Rebuild
 * {@link #processTagDetection} against the new Path API to restore the auto-drive behavior.
 */
@Config
public class Camera extends SubsystemBase {

    private final AprilTagProcessor aprilTag;
    private final VisionPortal visionPortal;
    private final Drive drive;
    private final ElapsedTime pathUpdateTimer = new ElapsedTime();

    private volatile boolean running = true;
    private volatile boolean isAligning = false;
    private volatile boolean isConverged = false;
    private final Thread visionThread;

    private boolean hasFilteredTag = false;
    private double filteredX, filteredY, filteredYaw;

    public Camera(HardwareMap hardwareMap, Drive drive) {
        this.drive = drive;

        aprilTag = new AprilTagProcessor.Builder().build();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(aprilTag)
                .build();

        FtcDashboard.getInstance().startCameraStream(visionPortal, 15);

        pathUpdateTimer.reset();

        visionThread = new Thread(() -> {
            while (running && !Thread.currentThread().isInterrupted()) {
                if (isAligning && !isConverged) {
                    AprilTagDetection targetTag = findTargetTag();
                    if (targetTag != null) {
                        processTagDetection(targetTag);
                    }
                }
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        visionThread.setName("VisionThread");
        visionThread.setPriority(Thread.MAX_PRIORITY);
        visionThread.start();
    }

    private AprilTagDetection findTargetTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        if (currentDetections == null) return null;
        for (AprilTagDetection detection : currentDetections) {
            if (detection.id == DESIRED_TAG_ID && detection.metadata != null) {
                return detection;
            }
        }
        return null;
    }

    private void processTagDetection(AprilTagDetection tag) {
        if (pathUpdateTimer.seconds() < ALIGN_UPDATE_SECONDS) return;

        double pitchRad = Math.toRadians(CAMERA_PITCH_OFFSET);
        double rawX = (tag.ftcPose.y * Math.cos(pitchRad)) - (tag.ftcPose.z * Math.sin(pitchRad));
        double rawY = -tag.ftcPose.x;
        double rawYaw = tag.ftcPose.yaw;

        double distance = tag.ftcPose.range;
        double dynamicAlpha = Math.max(FILTER_ALPHA, 1.0 - (distance / 35.0));
        dynamicAlpha = Math.min(0.8, dynamicAlpha);

        if (!hasFilteredTag) {
            filteredX = rawX; filteredY = rawY; filteredYaw = rawYaw;
            hasFilteredTag = true;
        } else {
            filteredX = dynamicAlpha * rawX + (1.0 - dynamicAlpha) * filteredX;
            filteredY = dynamicAlpha * rawY + (1.0 - dynamicAlpha) * filteredY;
            filteredYaw = dynamicAlpha * rawYaw + (1.0 - dynamicAlpha) * filteredYaw;
        }

        // TODO(pedropathing-3.0.0-SNAPSHOT): rebuild the drive-to-tag path here using the new
        // Path/Curve/Modifier API and set isConverged once the robot actually arrives. For now
        // this only tracks the filtered offsets above.
        pathUpdateTimer.reset();
    }

    public void startAlignment() {
        isAligning = true;
        isConverged = false;
        hasFilteredTag = false;
        pathUpdateTimer.reset();
        drive.getFollower().stop();
    }

    public void stopAlignment() {
        isAligning = false;
        isConverged = false;
        hasFilteredTag = false;
    }

    public StartEndCommand alignCommand() {
        return new StartEndCommand(this::startAlignment, () -> {
            stopAlignment();
            drive.resetHeadingToCurrent();
        }, this, drive);
    }

    public void stop() {
        running = false;
        if (visionThread != null) {
            visionThread.interrupt();
            try {
                visionThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    public boolean isAligning() { return isAligning; }

    public boolean isConverged() { return isConverged; }

    /**
     * Blocks (during OpMode init, before the match starts) until the camera stream is live, then
     * locks exposure/gain for consistent tag detection. Safe to call from
     * {@code CommandOpMode#initialize()}.
     */
    public void configureExposureBlocking(LinearOpMode opMode, Telemetry telemetry) {
        ElapsedTime timer = new ElapsedTime();
        double timeoutSeconds = 3.0;

        telemetry.addData("Camera", "Waiting for stream to start...");
        telemetry.update();

        while (!opMode.isStopRequested() && visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            if (timer.seconds() > timeoutSeconds) {
                telemetry.addData("Camera Error", "Stream timeout! Bypassing camera controls to prevent hang.");
                telemetry.update();
                return;
            }
            opMode.sleep(20);
        }

        if (opMode.isStopRequested()) return;

        ExposureControl ec = visionPortal.getCameraControl(ExposureControl.class);
        if (ec != null && ec.isModeSupported(ExposureControl.Mode.Manual)) {
            ec.setMode(ExposureControl.Mode.Manual);
            ec.setExposure(15, TimeUnit.MILLISECONDS);
        }

        GainControl gc = visionPortal.getCameraControl(GainControl.class);
        if (gc != null) {
            gc.setGain(200);
        }

        telemetry.addData("Camera", "Controls set successfully.");
        telemetry.update();
    }
}
