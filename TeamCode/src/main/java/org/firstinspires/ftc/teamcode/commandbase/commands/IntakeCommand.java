package org.firstinspires.ftc.teamcode.commandbase.commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.commandbase.subsystem.Intake;

public class IntakeCommand extends CommandBase {
    private final Intake intake;
    private final Intake.IntakeState desiredState;

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
    public boolean isFinished() {
        return true;
    }
}
