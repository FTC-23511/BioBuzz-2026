package org.firstinspires.ftc.teamcode.commandbase.commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.commandbase.subsystems.Intake;

/**
 * Command to change the {@link Intake} subsystems's state.
 * This command sets the desired state and finishes immediately.
 */
public class IntakeCommand extends CommandBase {
    private final Intake intake;
    private final Intake.IntakeState desiredState;

    /**
     * Constructs an {@link IntakeCommand}.
     *
     * @param intake       the {@link Intake} subsystems to control
     * @param desiredState the {@link Intake.IntakeState} to apply
     */
    public IntakeCommand(Intake intake, Intake.IntakeState desiredState) {
        this.intake = intake;
        this.desiredState = desiredState;
        addRequirements(intake);
    }

    /**
     * Sets the intake to the desired state.
     */
    @Override
    public void initialize() {
        intake.setState(desiredState);
    }

    /**
     * Returns true to indicate the command is finished.
     *
     * @return true
     */
    @Override
    public boolean isFinished() {
        return true;
    }
}
