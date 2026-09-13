package org.firstinspires.ftc.teamcode.commandbase.commands;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.commandbase.subsystem.Launcher;
import org.firstinspires.ftc.teamcode.commandbase.subsystem.Transfer;
import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * Spins the flywheel up to a preset, waits for it to reach that speed, then runs the feeder
 * for a fixed window to push artifacts into it. Requires the launcher and the transfer, so a
 * manual feeder override interrupts it rather than fighting it for the servo.
 *
 * <p>Because the feeder is continuous rotation with no artifact sensing, the feed window is
 * timed rather than counted, and LAUNCH_FEED_TIME is the knob for how many artifacts go per
 * press. The flywheel is left running when this ends, so repeated shots do not each pay the
 * spin-up cost.
 */
public class Launch extends CommandBase {
    private final Robot robot;
    private final Launcher.FlywheelState state;

    private final ElapsedTime feedTimer = new ElapsedTime();
    private final ElapsedTime spinUpTimer = new ElapsedTime();
    private boolean feeding;

    public Launch(Launcher.FlywheelState state) {
        robot = Robot.getInstance();
        this.state = state;
        addRequirements(robot.launcher, robot.transfer);
    }

    @Override
    public void initialize() {
        feeding = false;
        spinUpTimer.reset();
        robot.launcher.setFlywheel(state);
    }

    @Override
    public void execute() {
        // Latch on the first moment the flywheel is ready. Firing an artifact drags the wheel
        // back below tolerance, so re-checking readiness here would chop the feed window short
        if (!feeding && robot.launcher.readyToLaunch()) {
            feeding = true;
            feedTimer.reset();
            robot.transfer.setTransfer(Transfer.TransferState.FORWARD);
        }
    }

    @Override
    public void end(boolean interrupted) {
        robot.transfer.setTransfer(Transfer.TransferState.STOP);
    }

    @Override
    public boolean isFinished() {
        if (feeding) {
            return feedTimer.milliseconds() >= LAUNCH_FEED_TIME;
        }

        return spinUpTimer.milliseconds() >= LAUNCH_SPIN_UP_TIMEOUT;
    }
}
