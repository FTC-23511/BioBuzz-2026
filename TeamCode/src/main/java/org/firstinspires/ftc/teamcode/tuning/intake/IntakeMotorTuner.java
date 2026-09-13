package org.firstinspires.ftc.teamcode.tuning.intake;

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

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;
import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * Checks the intake motor direction and finds the roller speeds that pick artifacts up
 * cleanly without jamming them.
 *
 * <p>Leave USE_RAW_POWER off to drive the intake through the subsystem with TRIANGLE, CIRCLE
 * and SQUARE, which is how it behaves in a match. Turn it on to sweep INTAKE_MOTOR_POWER
 * directly from Dashboard, then copy the values that worked into INTAKE_FORWARD_SPEED and
 * INTAKE_REVERSE_SPEED.
 *
 * <p>If the rollers spin the wrong way, flip the inverted flag on intakeMotor in Robot rather
 * than sign-flipping the constants.
 */
@Config
@TeleOp(name = "IntakeMotorTuner", group = "Intake")
public class IntakeMotorTuner extends CommandOpMode {
    public GamepadEx driver;

    public ElapsedTime timer;

    public static boolean USE_RAW_POWER = false;
    public static double INTAKE_MOTOR_POWER = 0.0; // Power

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
        driver.getGamepadButton(GamepadKeys.Button.TRIANGLE).whenPressed(
                new InstantCommand(() -> robot.intake.setIntake(Intake.MotorState.FORWARD), robot.intake)
        );

        driver.getGamepadButton(GamepadKeys.Button.CIRCLE).whenPressed(
                new InstantCommand(() -> robot.intake.setIntake(Intake.MotorState.STOP), robot.intake)
        );

        driver.getGamepadButton(GamepadKeys.Button.SQUARE).whenPressed(
                new InstantCommand(() -> robot.intake.setIntake(Intake.MotorState.REVERSE), robot.intake)
        );
    }

    @Override
    public void preRun() {
        robot.initHasMovement();
        timer = new ElapsedTime();
    }

    @Override
    public void run() {
        if (USE_RAW_POWER) {
            // Bypasses the state machine, so the reported state will not match what is spinning
            robot.intakeMotor.set(INTAKE_MOTOR_POWER);
        }

        telemetryEx.addData("Loop Time", timer.milliseconds());
        timer.reset();

        telemetryEx.addData("Using Raw Power", USE_RAW_POWER);
        telemetryEx.addData("Intake State", Intake.motorState);
        telemetryEx.addData("Intake Power", robot.intakeMotor.get());
        telemetryEx.addData("Intake Current (mA)", robot.intakeMotor.getCurrent(CurrentUnit.MILLIAMPS));

        // DO NOT REMOVE ANY LINES BELOW! Runs the command scheduler and updates telemetry
        super.run();
        telemetryEx.update();
    }

    @Override
    public void end() {
        robot.exportProfiler(robot.profilerFile, robot.logCatFile);
    }
}
