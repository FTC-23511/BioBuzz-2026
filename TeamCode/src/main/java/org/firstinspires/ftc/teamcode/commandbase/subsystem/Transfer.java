package org.firstinspires.ftc.teamcode.commandbase.subsystem;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.acmerobotics.dashboard.config.Config;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * Runs the feeder between the intake and the launcher, a single continuous-rotation Axon Max+
 * driving the transfer roller. Running it forward carries artifacts up into the flywheels.
 *
 * <p>{@link #setTransfer(TransferState)} does not check whether the flywheel is up to speed.
 * That interlock lives in {@link org.firstinspires.ftc.teamcode.commandbase.commands.Launch},
 * so the driver keeps a manual override for clearing jams.
 */
@Config
public class Transfer extends SubsystemBase {
    private final Robot robot = Robot.getInstance();

    public enum TransferState {
        REVERSE,
        STOP,
        FORWARD
    }

    public static TransferState transferState = TransferState.STOP;

    /**
     * Stops the feeder and clears its state at the start of an OpMode. Safe to call from a
     * testing OpMode, since stopping never moves anything.
     */
    public void init() {
        transferState = TransferState.STOP;
        robot.transferServo.set(0);
    }

    /**
     * Sets the direction the feeder roller spins. Re-commanding the state the feeder is
     * already in does nothing, so this is cheap to call from a held button.
     *
     * @param transferState the roller direction to command
     */
    public void setTransfer(TransferState transferState) {
        if (Transfer.transferState == transferState) {
            return;
        }

        switch (transferState) {
            case STOP:
                robot.transferServo.set(0);
                break;
            case FORWARD:
                robot.transferServo.set(TRANSFER_FORWARD_POWER);
                break;
            case REVERSE:
                robot.transferServo.set(TRANSFER_REVERSE_POWER);
                break;
        }

        Transfer.transferState = transferState;
    }

    /**
     * Reads which direction the feeder is currently commanded to spin.
     *
     * @return the current roller state
     */
    public TransferState getTransferState() {
        return transferState;
    }
}
