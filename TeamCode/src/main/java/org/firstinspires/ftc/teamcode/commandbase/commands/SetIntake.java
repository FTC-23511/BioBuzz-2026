package org.firstinspires.ftc.teamcode.commandbase.commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;
import org.firstinspires.ftc.teamcode.globals.Robot;

public class SetIntake extends CommandBase {
    private final Robot robot;
    private final Intake.MotorState motorState;

    public SetIntake(Intake.MotorState motorState) {
        robot = Robot.getInstance();
        this.motorState = motorState;
        addRequirements(robot.intake);
    }

    @Override
    public void initialize() {
        robot.intake.setIntake(motorState);
    }

    @Override
    public void execute() {

    }

    @Override
    public void end(boolean interrupted) {

    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
