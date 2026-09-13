package org.firstinspires.ftc.teamcode.tuning.transfer;

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

import org.firstinspires.ftc.teamcode.commandbase.subsystem.Transfer;
import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * Checks the feeder direction and finds the roller power that carries artifacts into the
 * flywheels without stalling the Axon or forcing two artifacts in at once.
 *
 * <p>Leave USE_RAW_POWER off to drive the feeder through the subsystem with TRIANGLE, CIRCLE
 * and SQUARE. Turn it on to sweep TRANSFER_SERVO_POWER directly from Dashboard, then copy the
 * values that worked into TRANSFER_FORWARD_POWER and TRANSFER_REVERSE_POWER.
 *
 * <p>An Axon Max+ in continuous mode still creeps at a power of zero if its trim is off. If
 * the roller does not sit still when stopped, trim the servo rather than biasing the constants.
 */
@Config
@TeleOp(name = "TransferServoTuner", group = "Transfer")
public class TransferServoTuner extends CommandOpMode {
    public GamepadEx driver;

    public ElapsedTime timer;

    public static boolean USE_RAW_POWER = false;
    public static double TRANSFER_SERVO_POWER = 0.0; // Power

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
                new InstantCommand(() -> robot.transfer.setTransfer(Transfer.TransferState.FORWARD), robot.transfer)
        );

        driver.getGamepadButton(GamepadKeys.Button.CIRCLE).whenPressed(
                new InstantCommand(() -> robot.transfer.setTransfer(Transfer.TransferState.STOP), robot.transfer)
        );

        driver.getGamepadButton(GamepadKeys.Button.SQUARE).whenPressed(
                new InstantCommand(() -> robot.transfer.setTransfer(Transfer.TransferState.REVERSE), robot.transfer)
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
            robot.transferServo.set(TRANSFER_SERVO_POWER);
        }

        telemetryEx.addData("Loop Time", timer.milliseconds());
        timer.reset();

        telemetryEx.addData("Using Raw Power", USE_RAW_POWER);
        telemetryEx.addData("Transfer State", Transfer.transferState);
        telemetryEx.addData("Transfer Power", robot.transferServo.get());

        // DO NOT REMOVE ANY LINES BELOW! Runs the command scheduler and updates telemetry
        super.run();
        telemetryEx.update();
    }

    @Override
    public void end() {
        robot.exportProfiler(robot.profilerFile, robot.logCatFile);
    }
}
