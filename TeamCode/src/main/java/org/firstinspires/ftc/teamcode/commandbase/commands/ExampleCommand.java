package org.firstinspires.ftc.teamcode.commandbase.commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * A template for creating new commands.
 * This class should be copied and modified to implement specific robot behaviors.
 */
public class ExampleCommand extends CommandBase {
    private final Robot robot;

    /**
     * Constructs an {@link ExampleCommand} and sets up requirements.
     */
    public ExampleCommand() {
        robot = Robot.getInstance();
        addRequirements(); // TODO: replace with requirements of the command
    }

    /**
     * Called once when the command is scheduled.
     */
    @Override
    public void initialize() {

    }

    /**
     * Called repeatedly while the command is scheduled.
     */
    @Override
    public void execute() {

    }

    /**
     * Called once when the command ends or is interrupted.
     *
     * @param interrupted whether the command was interrupted
     */
    @Override
    public void end(boolean interrupted) {

    }

    /**
     * Returns true when the command should finish.
     *
     * @return true if finished, false otherwise
     */
    @Override
    public boolean isFinished() {
        return true; // TODO: replace with end condition of the command
    }
}
