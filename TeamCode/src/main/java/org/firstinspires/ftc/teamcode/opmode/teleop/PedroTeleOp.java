package org.firstinspires.ftc.teamcode.opmode.teleop;

import static org.firstinspires.ftc.teamcode.globals.Constants.OpModeType;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.commandbase.commands.IntakeCommand;
import org.firstinspires.ftc.teamcode.commandbase.subsystems.Intake;
import org.firstinspires.ftc.teamcode.globals.Constants;
import org.firstinspires.ftc.teamcode.globals.Robot;


@TeleOp(name = "Pedro TeleOp")
public class PedroTeleOp extends CommandOpMode {

    private final Robot robot = Robot.getInstance();
    private GamepadEx driver;
    private ElapsedTime timer;

    @Override
    public void initialize() {
        super.reset();

        Constants.OP_MODE_TYPE = OpModeType.TELEOP;
        Constants.TESTING_OP_MODE = false;

        robot.init(hardwareMap);

        driver = new GamepadEx(gamepad1);

        // robot.camera.configureExposureBlocking(this, telemetryEx);

//        robot.intake.setState(Intake.IntakeState.STOP);

        // Driver controls
        robot.drive.setDefaultCommand(robot.drive.driveCommand(driver));

        driver.getGamepadButton(GamepadKeys.Button.CROSS).whenPressed(new IntakeCommand(robot.intake, Intake.IntakeState.INTAKE));
        driver.getGamepadButton(GamepadKeys.Button.CROSS).whenReleased(new IntakeCommand(robot.intake, Intake.IntakeState.STOP));
        driver.getGamepadButton(GamepadKeys.Button.CIRCLE).whenPressed(new IntakeCommand(robot.intake, Intake.IntakeState.REVERSE));
        driver.getGamepadButton(GamepadKeys.Button.CIRCLE).whenReleased(new IntakeCommand(robot.intake, Intake.IntakeState.STOP));

        driver.getGamepadButton(GamepadKeys.Button.OPTIONS)
                .whenPressed(new InstantCommand(() -> robot.drive.resetPose(0.0)));
    }

    @Override
    public void initialize_loop() {
        robot.initializeLoop(gamepad1);
    }

    @Override
    public void preRun() {
        robot.initHasMovement();
    }

    @Override
    public void run() {
        if (timer == null) {
            timer = new ElapsedTime();
        }

        timer.reset();

        // Runs the command scheduler
        robot.updateLoop();
    }

    @Override
    public void end() {
        robot.intake.setState(Intake.IntakeState.STOP);
        robot.exportProfiler(robot.profilerFile, robot.logCatFile);
    }
}
