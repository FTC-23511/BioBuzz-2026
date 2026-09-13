package org.firstinspires.ftc.teamcode.globals;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.hardware.motors.CRServoEx;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Drive;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Camera;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Launcher;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Transfer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import dev.nullftc.profiler.Profiler;
import dev.nullftc.profiler.entry.BasicProfilerEntryFactory;
import dev.nullftc.profiler.exporter.CSVProfilerExporter;

public class Robot extends com.seattlesolvers.solverslib.command.Robot {
    private static final Robot instance = new Robot();
    public static Robot getInstance() {
        return instance;
    }

    public Profiler profiler;
    public File profilerFile;
    public File logCatFile;

    // Hardware
    public MotorEx intakeMotor;

    // Both flywheel motors drive the same shaft, so they must turn opposite ways. Only
    // leftLaunchMotor is inverted, since MotorGroup commands the same power to every member
    // and lets the SDK apply the direction of each motor
    public MotorEx leftLaunchMotor;
    public MotorEx rightLaunchMotor;
    public MotorGroup launchMotors;
    // Reads the encoder on the leftLaunchMotor port, already direction-corrected by the SDK
    public Motor.Encoder launchEncoder;

    public CRServoEx transferServo;

    // Subsystems
    public Drive drive;
    public Intake intake;
    public Transfer transfer;
    public Camera camera;
    public Launcher launcher;

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

        // hardware
        // The four drive motors are owned by Pedro's Mecanum drivetrain (see PedroConstants),
        // so they are deliberately not wrapped again here.
        intakeMotor = new MotorEx(hwMap, "intakeMotor")
                .setCachingTolerance(0.01);
        intakeMotor.setRunMode(Motor.RunMode.RawPower)
                .setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)
                .setInverted(false);

        leftLaunchMotor = new MotorEx(hwMap, "leftLaunchMotor")
                .setCachingTolerance(0.01);
        leftLaunchMotor.setInverted(true);

        rightLaunchMotor = new MotorEx(hwMap, "rightLaunchMotor")
                .setCachingTolerance(0.01);
        rightLaunchMotor.setInverted(false);

        launchMotors = new MotorGroup(leftLaunchMotor, rightLaunchMotor);
        launchMotors.setRunMode(Motor.RunMode.RawPower);
        // FLOAT so a spun-up flywheel coasts down instead of fighting its own inertia.
        launchMotors.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);

        launchEncoder = leftLaunchMotor.encoder;

        transferServo = new CRServoEx(hwMap, "transferServo");
        transferServo.setRunMode(CRServoEx.RunMode.RawPower)
                .setCachingTolerance(0.01);
        transferServo.setInverted(false);

        // subsystems
        drive = new Drive(hwMap);
        intake = new Intake();
        transfer = new Transfer();
        launcher = new Launcher();

        CommandScheduler.getInstance().setBulkReading(hwMap, LynxModule.BulkCachingMode.MANUAL);
    }

    /**
     * Moves the mechanisms into their starting positions. This is kept out of
     * {@link #init(HardwareMap)} so nothing moves until the OpMode is actually started,
     * rather than the moment it is initialized on the field.
     */
    public void initHasMovement() {
        drive.init();
        intake.init();
        transfer.init();
        launcher.init();
    }

    /**
     * Writes the profiler data and logcat to files on the Robot Controller, on a background
     * thread so the OpMode is not stalled.
     *
     * @param profilerFile the file to write profiler results to
     * @param logCatFile   the file to write the logcat dump to
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
