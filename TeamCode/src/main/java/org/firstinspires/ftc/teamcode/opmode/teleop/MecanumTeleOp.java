package org.firstinspires.ftc.teamcode.opmode.teleop;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.util.TelemetryEx;

import org.firstinspires.ftc.teamcode.commandbase.commands.SetIntake;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;
import org.firstinspires.ftc.teamcode.globals.Constants;
import org.firstinspires.ftc.teamcode.globals.Robot;

@Config
@TeleOp(group = "TeleOp")
public class MecanumTeleOp extends CommandOpMode {
    public GamepadEx driver;
    public GamepadEx operator;

    public ElapsedTime timer;

    TelemetryEx telemetryEx = new TelemetryEx(new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry()));

    private final Robot robot = Robot.getInstance();

    @Override
    public void initialize() {
        // Must-have for all opModes
        OP_MODE_TYPE = OpModeType.TELEOP;
        TESTING_OP_MODE = false;

        // Resets the command scheduler
        super.reset();

        // Initialize the robot (which also registers subsystems, configures CommandScheduler, etc.)
        robot.init(hardwareMap);

        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        // Driver controls
        driver.getGamepadButton(GamepadKeys.Button.TRIANGLE).whenPressed(
                new SetIntake(Intake.MotorState.FORWARD)
        );

        driver.getGamepadButton(GamepadKeys.Button.CIRCLE).whenPressed(
                new SetIntake(Intake.MotorState.STOP)
        );

        driver.getGamepadButton(GamepadKeys.Button.SQUARE).whenPressed(
                new SetIntake(Intake.MotorState.REVERSE)
        );


        // Operator controls
        // TODO: add controls here
    }

    @Override
    public void preRun() {
        robot.initHasMovement();
        timer = new ElapsedTime();
    }

    @Override
    public void run() {
        robot.drive.driveRobotCentric(driver);

        telemetryEx.addData("Loop Time", timer.milliseconds());
        timer.reset();

        // DO NOT REMOVE ANY LINES BELOW! Runs the command scheduler and updates telemetry
        super.run();
        telemetryEx.update();
    }

    @Override
    public void end() {
//        Constants.END_POSE = robot.drive.getPose();
    }
}