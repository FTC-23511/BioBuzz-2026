package org.firstinspires.ftc.teamcode.opmode.teleop;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.util.TelemetryEx;

import org.firstinspires.ftc.teamcode.commandbase.commands.Launch;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Launcher;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Transfer;
import org.firstinspires.ftc.teamcode.globals.Constants;
import org.firstinspires.ftc.teamcode.globals.MathFunctions;
import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * The competition TeleOp. The driver takes the drivetrain and the intake, the operator takes
 * the launcher and the feeder.
 *
 * <p>Driver: left stick translates, right stick x turns, the right trigger scales speed
 * between TELEOP_MIN_SPEED and TELEOP_MAX_SPEED, OPTIONS zeroes the heading, CROSS toggles the
 * intake and holding TRIANGLE spits.
 *
 * <p>Operator: the bumpers launch at the near and far presets, CIRCLE stops the flywheel, and
 * holding SQUARE or DPAD_DOWN drives the feeder by hand to clear a jam.
 *
 * <p>Driving is field-centric, so the heading reset on OPTIONS matters. Once the heading is
 * wrong the stick directions stop matching the field.
 */
@Config
@TeleOp(name = "Starter Bot TeleOp", group = "AAATeleOp")
public class StarterBotTeleOp extends CommandOpMode {
    public GamepadEx driver;
    public GamepadEx operator;

    public ElapsedTime timer;

    TelemetryEx telemetryEx = new TelemetryEx(new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry()));

    private final Robot robot = Robot.getInstance();

    // What the intake was doing before TRIANGLE was held, so releasing it restores that
    private Intake.MotorState intakeStateBeforeReverse = Intake.MotorState.STOP;

    @Override
    public void initialize() {
        // Must have for all opModes
        OP_MODE_TYPE = OpModeType.TELEOP;
        TESTING_OP_MODE = false;

        // Resets the command scheduler
        super.reset();

        // Initialize the robot (which also registers subsystems, configures CommandScheduler, etc.)
        robot.init(hardwareMap);

        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        // Driver controls
        driver.getGamepadButton(GamepadKeys.Button.OPTIONS).whenPressed(
                new InstantCommand(() -> robot.drive.resetHeading())
        );

        driver.getGamepadButton(GamepadKeys.Button.CROSS).whenPressed(
                new InstantCommand(() -> robot.intake.toggleIntake(), robot.intake)
        );

        driver.getGamepadButton(GamepadKeys.Button.TRIANGLE).whenPressed(
                new InstantCommand(() -> {
                    intakeStateBeforeReverse = Intake.motorState;
                    robot.intake.setIntake(Intake.MotorState.REVERSE);
                }, robot.intake)
        );

        driver.getGamepadButton(GamepadKeys.Button.TRIANGLE).whenReleased(
                new InstantCommand(() -> robot.intake.setIntake(intakeStateBeforeReverse), robot.intake)
        );

        // Operator controls
        operator.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).whenPressed(
                new Launch(Launcher.FlywheelState.NEAR)
        );

        operator.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).whenPressed(
                new Launch(Launcher.FlywheelState.FAR)
        );

        operator.getGamepadButton(GamepadKeys.Button.CIRCLE).whenPressed(
                new InstantCommand(() -> robot.launcher.setFlywheel(Launcher.FlywheelState.OFF), robot.launcher)
        );

        // Manual feeder overrides. These require the transfer subsystem so pressing one
        // interrupts an in-flight Launch instead of fighting it for the servo
        operator.getGamepadButton(GamepadKeys.Button.SQUARE).whenPressed(
                new InstantCommand(() -> robot.transfer.setTransfer(Transfer.TransferState.FORWARD), robot.transfer)
        );

        operator.getGamepadButton(GamepadKeys.Button.SQUARE).whenReleased(
                new InstantCommand(() -> robot.transfer.setTransfer(Transfer.TransferState.STOP), robot.transfer)
        );

        operator.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenPressed(
                new InstantCommand(() -> robot.transfer.setTransfer(Transfer.TransferState.REVERSE), robot.transfer)
        );

        operator.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenReleased(
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
        robot.profiler.start("Full Loop");

        // Only drive when no command has claimed the drivetrain
        if (CommandScheduler.getInstance().isAvailable(robot.drive)) {
            double speedMultiplier = TELEOP_MIN_SPEED
                    + (TELEOP_MAX_SPEED - TELEOP_MIN_SPEED) * driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER);

            // The Pedro frame is x forward, y left, heading counter-clockwise-positive.
            // getLeftY() is already up-positive, but both x axes read right-positive on the
            // gamepad and so are negated
            robot.drive.drive(
                    MathFunctions.deadZone(driver.getLeftY(), JOYSTICK_DEAD_ZONE),
                    -MathFunctions.deadZone(driver.getLeftX(), JOYSTICK_DEAD_ZONE),
                    -MathFunctions.deadZone(driver.getRightX(), JOYSTICK_DEAD_ZONE),
                    speedMultiplier
            );
        }

        telemetryEx.addData("Loop Time", timer.milliseconds());
        timer.reset();

        telemetryEx.addData("Robot Pose", robot.drive.getPose());
        telemetryEx.addData("Field Centric", FIELD_CENTRIC);

        telemetryEx.addData("Intake State", Intake.motorState);
        telemetryEx.addData("Transfer State", Transfer.transferState);

        telemetryEx.addData("Flywheel State", Launcher.flywheelState);
        telemetryEx.addData("Flywheel Target", robot.launcher.getFlywheelTarget());
        telemetryEx.addData("Flywheel Velocity", robot.launcher.getFlywheelVelocity());
        telemetryEx.addData("Ready To Launch", robot.launcher.readyToLaunch());

        // DO NOT REMOVE ANY LINES BELOW! Runs the command scheduler and updates telemetry
        super.run();
        telemetryEx.update();

        robot.profiler.end("Full Loop");
    }

    @Override
    public void end() {
        Constants.END_POSE = robot.drive.getPose();
        telemetryEx.update();
        robot.exportProfiler(robot.profilerFile, robot.logCatFile);
    }
}
