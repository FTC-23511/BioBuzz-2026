package org.firstinspires.ftc.teamcode.commandbase.subsystem;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.acmerobotics.dashboard.config.Config;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.globals.Robot;

@Config
public class Intake extends SubsystemBase {
    private final Robot robot = Robot.getInstance();

    public enum MotorState {
        FORWARD,
        TRANSFER,
        REVERSE,
        STOP
    }

    public static MotorState motorState = MotorState.STOP;

    public Intake() {
        super();
    }

    public void init() {
        setIntake(MotorState.STOP);
    }

    public void setIntake(MotorState motorState) {
        if (Intake.motorState == motorState) {
            return;
        }

        switch (motorState) {
            case STOP:
                robot.intakeMotor.set(0);
                break;
            case TRANSFER:
                robot.intakeMotor.set(INTAKE_TRANSFER_SPEED);
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

    public void toggleIntakeMotor() {
        setIntake(motorState.equals(MotorState.FORWARD) ? MotorState.STOP : MotorState.FORWARD);
    }

    @Override
    public void periodic() {

    }
}
