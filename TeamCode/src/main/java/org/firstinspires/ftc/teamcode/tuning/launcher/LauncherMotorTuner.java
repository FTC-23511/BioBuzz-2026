package org.firstinspires.ftc.teamcode.tuning.launcher;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.util.TelemetryEx;

import org.firstinspires.ftc.teamcode.commandbase.subsystem.Launcher;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Transfer;
import org.firstinspires.ftc.teamcode.globals.Constants;
import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * Tunes the flywheel velocity loop and finds the velocity each shot distance needs.
 *
 * <p>Set TARGET_VELOCITY in Dashboard and watch the measured velocity graph against it. Tune
 * FLYWHEEL_PIDF_COEFFICIENTS in the same session, since the launcher re-reads them every loop.
 * Raise kF first until the flywheel settles near the target on its own, then add kP to close
 * the remaining gap.
 *
 * <p>Hold SQUARE to feed an artifact once the flywheel is at speed, then measure where it
 * lands to fill in LAUNCHER_NEAR_VELOCITY and LAUNCHER_FAR_VELOCITY.
 */
@Config
@TeleOp(name = "LauncherMotorTuner", group = "Launcher")
public class LauncherMotorTuner extends CommandOpMode {
    public GamepadEx driver;

    public ElapsedTime timer;

    public static double TARGET_VELOCITY = 0.0; // Ticks/second

    TelemetryEx telemetryEx = new TelemetryEx(new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry()));

    private final Robot robot = Robot.getInstance();

    @Override
    public void initialize() {
        // Must have for all opModes
        OP_MODE_TYPE = OpModeType.TELEOP;
        TESTING_OP_MODE = true;

        // Resets the command scheduler
        super.reset();

        // Initialize the robot (which also registers subsystems, configures CommandScheduler, etc.)
        robot.init(hardwareMap);

        driver = new GamepadEx(gamepad1);

        // Driver controls
        driver.getGamepadButton(GamepadKeys.Button.CROSS).whenPressed(
                new InstantCommand(() -> TARGET_VELOCITY = Constants.LAUNCHER_NEAR_VELOCITY)
        );

        driver.getGamepadButton(GamepadKeys.Button.TRIANGLE).whenPressed(
                new InstantCommand(() -> TARGET_VELOCITY = Constants.LAUNCHER_FAR_VELOCITY)
        );

        driver.getGamepadButton(GamepadKeys.Button.CIRCLE).whenPressed(
                new InstantCommand(() -> TARGET_VELOCITY = 0.0)
        );

        driver.getGamepadButton(GamepadKeys.Button.SQUARE).whenPressed(
                new InstantCommand(() -> robot.transfer.setTransfer(Transfer.TransferState.FORWARD), robot.transfer)
        );

        driver.getGamepadButton(GamepadKeys.Button.SQUARE).whenReleased(
                new InstantCommand(() -> robot.transfer.setTransfer(Transfer.TransferState.STOP), robot.transfer)
        );
    }

    @Override
    public void preRun() {
        robot.initHasMovement();
        timer = new ElapsedTime();
    }

    @Override
    public void run() {
        robot.launcher.setFlywheelVelocity(TARGET_VELOCITY);

        telemetryEx.addData("Loop Time", timer.milliseconds());
        timer.reset();

        telemetryEx.addData("Flywheel Target", robot.launcher.getFlywheelTarget());
        telemetryEx.addData("Flywheel Velocity", robot.launcher.getFlywheelVelocity());
        telemetryEx.addData("Flywheel Error", robot.launcher.getFlywheelTarget() - robot.launcher.getFlywheelVelocity());
        telemetryEx.addData("Ready To Launch", robot.launcher.readyToLaunch());
        telemetryEx.addData("Launch Motor Power", robot.launchMotors.get());

        telemetryEx.addData("Transfer State", Transfer.transferState);
        telemetryEx.addData("Flywheel State", Launcher.flywheelState);

        // DO NOT REMOVE ANY LINES BELOW! Runs the command scheduler and updates telemetry
        super.run();
        telemetryEx.update();
    }

    @Override
    public void end() {
        robot.exportProfiler(robot.profilerFile, robot.logCatFile);
    }
}
