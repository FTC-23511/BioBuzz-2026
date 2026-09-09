package org.firstinspires.ftc.teamcode.commandbase.subsystem;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.acmerobotics.dashboard.config.Config;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.globals.Robot;

@Config
public class Drive extends SubsystemBase {
    private final Robot robot = Robot.getInstance();

    public Drive() {
        super();
    }

    public void driveRobotCentric(GamepadEx gamepadEx) {
        double speed = MIN_DRIVE_SPEED + (1 - MIN_DRIVE_SPEED) * gamepadEx.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER);

        double y = gamepadEx.getLeftY(); // Remember, Y stick value is reversed
        double x = gamepadEx.getLeftX() * 1.1; // Counteract imperfect strafing
        double rx = gamepadEx.getRightX();

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        robot.FL.set(frontLeftPower * speed);
        robot.BL.set(backLeftPower * speed);
        robot.FR.set(frontRightPower * speed);
        robot.BR.set(backRightPower * speed);
    }

    @Override
    public void periodic() {

    }
}
