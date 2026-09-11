package org.firstinspires.ftc.teamcode.globals;

import android.util.Size;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.commandbase.subsystems.Drive;
import org.firstinspires.ftc.teamcode.commandbase.subsystems.Intake;
import org.firstinspires.ftc.teamcode.globals.PedroConstants;
import static org.firstinspires.ftc.teamcode.globals.Constants.*;
// import org.firstinspires.ftc.vision.VisionPortal;
// import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
// import org.firstinspires.ftc.vision.opencv.ColorRange;
// import org.firstinspires.ftc.vision.opencv.ImageRegion;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import dev.nullftc.profiler.Profiler;
import dev.nullftc.profiler.entry.BasicProfilerEntryFactory;
import dev.nullftc.profiler.exporter.CSVProfilerExporter;

/**
 * Singleton entry point that owns the robot's subsystems and shared profiling/logging state.
 * OpModes call {@link #init(HardwareMap)} once to construct the subsystems, and may later call
 * {@link #exportProfiler(File, File)} to flush diagnostics after a run.
 */
public class Robot extends com.seattlesolvers.solverslib.command.Robot {
    private static final Robot instance = new Robot();
    public static Robot getInstance() {
        return instance;
    }

    public Profiler profiler;
    public File profilerFile;
    public File logCatFile;


    public Drive drive;
    public Intake intake;
    private DcMotor intakeMotor;
    // public Camera camera;
    // public Launcher launcher;

    /**
     * Constructs the robot's subsystems and profiler/logging infrastructure.
     *
     * @param hwMap hardware map to resolve devices from
     */
    public void init(HardwareMap hwMap) {
        // logging
        File profilerFolder = new File(AppUtil.FIRST_FOLDER, "logs");
        File logcatFolder = new File(AppUtil.FIRST_FOLDER, "logcat");
        if (!profilerFolder.exists()) {
            profilerFolder.mkdirs();
        }
        if (!logcatFolder.exists()) {
            logcatFolder.mkdirs();
        }

        long timestamp = System.currentTimeMillis();
        profilerFile = new File(profilerFolder, "profiler-" + timestamp + ".csv");
        logCatFile = new File(logcatFolder, "logcat_" + timestamp + ".txt");

        profiler = Profiler.builder()
                .factory(new BasicProfilerEntryFactory())
                .exporter(new CSVProfilerExporter(profilerFile))
                .debugLog(false)
                .build();

    
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setPower(0.0);

        Follower follower = PedroConstants.create(hwMap);
        drive = new Drive(follower);
        intake = new Intake(intakeMotor);
        // camera = new Camera(hwMap, drive);
        // launcher = new Launcher();

        CommandScheduler.getInstance().setBulkReading(hwMap, LynxModule.BulkCachingMode.MANUAL);
    }

    /**
     * Calls each subsystems's post-movement initialization, for subsystems that need to
     * re-home or re-read state once the robot has already moved (e.g. after an autonomous
     * handoff). Only called on subsystems that expose such a hook.
     */
    public void initHasMovement() {
        drive.resetHeadingToCurrent();
    }

    /**
     * Runs a single iteration of the robot's main loop.
     * This method handles the command scheduler and clearing bulk caches.
     */
    public void updateLoop() {
        super.run();

        for (com.qualcomm.hardware.lynx.LynxModule hub : com.seattlesolvers.solverslib.command.CommandScheduler.getInstance().allHubs) {
            hub.clearBulkCache();
        }
    }

    /**
     * Runs repeatedly during the OpMode's initialization phase.
     * Useful for clearing bulk caches.
     *
     * @param gamepad1    the driver {@link com.qualcomm.robotcore.hardware.Gamepad}
     */
    public void initializeLoop(com.qualcomm.robotcore.hardware.Gamepad gamepad1) {
        for (com.qualcomm.hardware.lynx.LynxModule hub : com.seattlesolvers.solverslib.command.CommandScheduler.getInstance().allHubs) {
            hub.clearBulkCache();
        }
    }

    /**
     * Asynchronously flushes the profiler and logcat output to the given files.
     *
     * @param profilerFile file to export profiler data to
     * @param logCatFile   file to export the device logcat to
     */
    public void exportProfiler(File profilerFile, File logCatFile) {
        RobotLog.i("Starting async profiler and logcat export to: " + profilerFile.getAbsolutePath());

        Thread exportThread = new Thread(() -> {
            try {
                profiler.export();
                profiler.shutdown();
            } catch (Exception e) {
                RobotLog.e("An error occurred", e.toString());
                RobotLog.e(e.toString(), Arrays.toString(e.getStackTrace()));
            }

            try {
                Process process = Runtime.getRuntime().exec("logcat -d -f " + logCatFile.getAbsolutePath());

                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    RobotLog.i("Logcat export successful to " + logCatFile.getAbsolutePath());
                } else {
                    RobotLog.w("Logcat export failed with exit code: " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                RobotLog.i("Logcat export Error", e.getMessage());
            }
        });

        exportThread.setDaemon(true);
        exportThread.start();
    }
}
