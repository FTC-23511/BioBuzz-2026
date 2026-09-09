package org.firstinspires.ftc.teamcode.globals;

import android.util.Size;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Drive;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;
// import org.firstinspires.ftc.teamcode.commandbase.subsystem.Camera;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Launcher;
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
    // public Camera camera;
    public Launcher launcher;

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

        // subsystems
        drive = new Drive(hwMap);
        intake = new Intake(hwMap);
        // camera = new Camera(hwMap, drive);
        launcher = new Launcher();

        CommandScheduler.getInstance().setBulkReading(hwMap, LynxModule.BulkCachingMode.MANUAL);
    }

    public void initHasMovement() {

    }

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
