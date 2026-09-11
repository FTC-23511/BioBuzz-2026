package org.firstinspires.ftc.teamcode.commandbase.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import static org.firstinspires.ftc.teamcode.globals.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.globals.Constants.REVERSE_POWER;

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

    public Intake() {
        robot = Robot.getInstance();
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

    public void setState(IntakeState state) {
        intakeState = state;
    }
}
