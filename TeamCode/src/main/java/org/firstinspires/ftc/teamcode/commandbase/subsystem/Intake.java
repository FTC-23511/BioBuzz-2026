package org.firstinspires.ftc.teamcode.commandbase.subsystem;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.acmerobotics.dashboard.config.Config;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * Runs the static ground intake, a single motor spinning the front roller bank that pulls
 * artifacts off the floor and hands them to the {@link Transfer}.
 *
 * <p>Nothing senses the artifacts, so this is open loop throughout: drive it with
 * {@link #setIntake(MotorState)} or {@link #toggleIntake()} and read back
 * {@link #getMotorState()}.
 */
@Config
public class Intake extends SubsystemBase {
    private final Robot robot = Robot.getInstance();

    public enum MotorState {
        REVERSE,
        STOP,
        FORWARD
    }

    public static MotorState motorState = MotorState.STOP;

    /**
     * Stops the intake and clears its state at the start of an OpMode. Safe to call from a
     * testing OpMode, since stopping never moves anything.
     */
    public void init() {
        motorState = MotorState.STOP;
        robot.intakeMotor.set(0);
    }

    /**
     * Sets the direction the intake rollers spin. Re-commanding the state the intake is
     * already in does nothing, so this is cheap to call from a held button.
     *
     * @param motorState the roller direction to command
     */
    public void setIntake(MotorState motorState) {
        if (Intake.motorState == motorState) {
            return;
        }

        switch (motorState) {
            case STOP:
                robot.intakeMotor.set(0);
                break;
            case FORWARD:
                robot.intakeMotor.set(INTAKE_FORWARD_SPEED);
                break;
            case REVERSE:
                robot.intakeMotor.set(INTAKE_REVERSE_SPEED);
                break;
        }

        Intake.motorState = motorState;
    }

    /**
     * Flips the intake between running forward and stopped, so one driver button covers both.
     * Reversing is not part of the cycle, since spitting is a held action rather than a mode.
     */
    public void toggleIntake() {
        if (motorState.equals(MotorState.FORWARD)) {
            setIntake(MotorState.STOP);
        } else {
            setIntake(MotorState.FORWARD);
        }
    }

    /**
     * Reads which direction the intake is currently commanded to spin.
     *
     * @return the current roller state
     */
    public MotorState getMotorState() {
        return motorState;
    }
}
