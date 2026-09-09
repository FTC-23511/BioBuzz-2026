package org.firstinspires.ftc.teamcode.commandbase.commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;
import org.firstinspires.ftc.teamcode.globals.Robot;

public class IntakeCommand extends CommandBase {
    private final Intake intake;
    private final Intake.IntakeState desiredState;

    public IntakeCommand() {
        this(Robot.getInstance().intake, Intake.IntakeState.INTAKE);
    }

    public IntakeCommand(Intake intake, Intake.IntakeState desiredState) {
        this.intake = intake;
        this.desiredState = desiredState;
        addRequirements(intake);
    }

    @Override
    public void initialize() {
        intake.setState(desiredState);
    }

    @Override
    public void execute() {
        intake.setState(desiredState);
    }

    @Override
    public void end(boolean interrupted) {
        if (desiredState != Intake.IntakeState.STOP) {
            intake.setState(Intake.IntakeState.STOP);
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
