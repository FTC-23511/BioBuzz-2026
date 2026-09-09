package org.firstinspires.ftc.teamcode.opmode.teleop;

import static org.firstinspires.ftc.teamcode.globals.Constants.OpModeType;

// import com.acmerobotics.dashboard.FtcDashboard;
// import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.commandbase.commands.IntakeCommand;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;
import org.firstinspires.ftc.teamcode.globals.Constants;
import org.firstinspires.ftc.teamcode.globals.Robot;


@TeleOp(name = "Pedro TeleOp")
public class PedroTeleOp extends CommandOpMode {

    private final Robot robot = Robot.getInstance();
    private GamepadEx driver;

    @Override
    public void initialize() {
        Constants.OP_MODE_TYPE = OpModeType.TELEOP;

        super.reset();

        robot.init(hardwareMap);

        driver = new GamepadEx(gamepad1);

        // Telemetry dashboardTelemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        // robot.camera.configureExposureBlocking(this, dashboardTelemetry);

        robot.intake.setState(Intake.IntakeState.STOP);

        // Driver controls
        robot.drive.setDefaultCommand(robot.drive.driveCommand(driver));

        driver.getGamepadButton(GamepadKeys.Button.A).whenPressed(new IntakeCommand(robot.intake, Intake.IntakeState.INTAKE));
        driver.getGamepadButton(GamepadKeys.Button.A).whenReleased(new IntakeCommand(robot.intake, Intake.IntakeState.STOP));
        driver.getGamepadButton(GamepadKeys.Button.B).whenPressed(new IntakeCommand(robot.intake, Intake.IntakeState.REVERSE));
        driver.getGamepadButton(GamepadKeys.Button.B).whenReleased(new IntakeCommand(robot.intake, Intake.IntakeState.STOP));

        new Trigger(() -> driver.getButton(GamepadKeys.Button.OPTIONS) || driver.getButton(GamepadKeys.Button.START))
                .whenActive(new InstantCommand(() -> robot.drive.resetPose(0.0)));
    }

    @Override
    public void preRun() {
        robot.initHasMovement();
        robot.drive.resetHeadingToCurrent();
    }

    @Override
    public void end() {
        robot.intake.setState(Intake.IntakeState.STOP);
    }
}
