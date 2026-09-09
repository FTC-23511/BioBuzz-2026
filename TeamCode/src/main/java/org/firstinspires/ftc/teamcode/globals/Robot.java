package org.firstinspires.ftc.teamcode.globals;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Drive;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Vision;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Launcher;

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

    // Logging
    public Profiler profiler;
    public File profilerFile;
    public File logCatFile;

    // Subsystems
    public Drive drive;
    public Intake intake;
    public Vision vision;
    public Launcher launcher;

    // Intake
    public MotorEx intakeMotor;

    // Drive
    public MotorEx FL;
    public MotorEx FR;
    public MotorEx BL;
    public MotorEx BR;

    public void init(HardwareMap hwMap) {
        // logging
        File profilerFolder = new File(AppUtil.FIRST_FOLDER, "logs");
        File logcatFolder = new File(AppUtil.FIRST_FOLDER, "logcat");
        if (!profilerFolder.exists()) profilerFolder.mkdirs();
        if (!logcatFolder.exists()) logcatFolder.mkdirs();

        long timestamp = System.currentTimeMillis();
        profilerFile = new File(profilerFolder, "profiler-" + timestamp + ".csv");
        logCatFile = new File(logcatFolder, "logcat_" + timestamp + ".txt");

        profiler = Profiler.builder()
                .factory(new BasicProfilerEntryFactory())
                .exporter(new CSVProfilerExporter(profilerFile))
                .debugLog(false)
                .build();

        // hardware
        intakeMotor = new MotorEx(hwMap, "intakeMotor").setCachingTolerance(0.001);

        FL = new MotorEx(hwMap, "FL")
                .setCachingTolerance(0.001);
        FR = new MotorEx(hwMap, "FR")
                .setCachingTolerance(0.001);
        BL = new MotorEx(hwMap, "BL")
                .setCachingTolerance(0.001);
        BR = new MotorEx(hwMap, "BR")
                .setCachingTolerance(0.001);

        FL.setInverted(true);
        BL.setInverted(true);

        FL.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        // subsystems
        drive = new Drive();
        intake = new Intake();
        intake.init();
        launcher = new Launcher();

        CommandScheduler.getInstance().setBulkReading(hwMap, LynxModule.BulkCachingMode.MANUAL);
    }

    /**
     * Moves the mechanisms into their starting positions. This is kept out of
     * {@link #init(HardwareMap)} so nothing moves until the OpMode is actually started.
     */
    public void initHasMovement() {

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
