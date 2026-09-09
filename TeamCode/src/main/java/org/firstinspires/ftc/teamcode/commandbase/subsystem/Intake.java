package org.firstinspires.ftc.teamcode.commandbase.subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;

/**
 * Intake roller with three states: stopped, intaking, and reversed.
 */
@Config
public class Intake extends SubsystemBase {

    public enum IntakeState {
        INTAKE,
        REVERSE,
        STOP
    }

    public static double INTAKE_POWER = 0.9;
    public static double REVERSE_POWER = -0.9;

    private IntakeState intakeState = IntakeState.STOP;
    private final DcMotor intakeMotor;

    public Intake(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setPower(0.0);
    }

    @Override
    public void periodic() {
        switch (intakeState) {
            case INTAKE:
                intakeMotor.setPower(INTAKE_POWER);
                break;
            case REVERSE:
                intakeMotor.setPower(REVERSE_POWER);
                break;
            case STOP:
                intakeMotor.setPower(0.0);
                break;
            default:
                intakeMotor.setPower(0.0);
                break;
        }
    }

    public IntakeState getState() {
        return intakeState;
    }

    public void setState(IntakeState state) {
        intakeState = state;
    }

    public void setPower(double power) {
        intakeMotor.setPower(power);
    }

    public InstantCommand intakeCommand() {
        return new InstantCommand(() -> setState(IntakeState.INTAKE), this);
    }

    public InstantCommand reverseCommand() {
        return new InstantCommand(() -> setState(IntakeState.REVERSE), this);
    }

    public InstantCommand stopCommand() {
        return new InstantCommand(() -> setState(IntakeState.STOP), this);
    }
}
