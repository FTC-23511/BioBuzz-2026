package org.firstinspires.ftc.teamcode.commandbase.subsystems;

import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.DEADZONE;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.DRIVE_SPEED_LIMIT;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.STICK_ALPHA;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.globals.PedroConstants;
//
@Config
public class Drive extends SubsystemBase {

    private static final double MAX_RATE = 3.5;
    private final double maxJerk = MAX_RATE * 12.0;

    private double filterX = 0.0;
    private double filterY = 0.0;
    private double filterRx = 0.0;

    private double vX = 0.0, vY = 0.0, vRx = 0.0;
    private double aX = 0.0, aY = 0.0, aRx = 0.0;

    private final Follower follower;
    private final HeadingLock headingLock;
    private final ElapsedTime timer = new ElapsedTime();

    public Drive(HardwareMap hardwareMap) {
        this.follower = PedroConstants.create(hardwareMap);
        this.headingLock = new HeadingLock(follower);
        timer.reset();
    }

    @Override
    public void periodic() {
        follower.update();
    }

    public Follower getFollower() {
        return follower;
    }

    public double getTargetHeading() {
        return headingLock.getTargetHeading();
    }

    public void resetHeadingToCurrent() {
        headingLock.resetHeading(follower.pose().heading());
    }

    public void resetPose(double heading) {
        Pose current = follower.pose();
        follower.setPose(new Pose(current.x(), current.y(), heading));
        headingLock.resetHeading(heading);
    }

    public RunCommand driveCommand(GamepadEx driver) {
        return new RunCommand(() -> handleManualDrive(driver), this);
    }

    private void handleManualDrive(GamepadEx driver) {
        double dt = timer.seconds();
        if (dt > 0.1) dt = 0.01;
        timer.reset();

        double rawY = driver.getLeftY();
        double rawX = -driver.getLeftX();
        double rawRx = -driver.getRightX();

        double outY, outX, outRx;

        if (driver.getButton(GamepadKeys.Button.LEFT_STICK_BUTTON)) {
            double targetAX = (rawX - vX) / (dt > 0 ? dt : 0.01);
            double targetAY = (rawY - vY) / (dt > 0 ? dt : 0.01);
            double targetARx = (rawRx - vRx) / (dt > 0 ? dt : 0.01);

            double jLimit = maxJerk * dt;
            aX += Range.clip(targetAX - aX, -jLimit, jLimit);
            aY += Range.clip(targetAY - aY, -jLimit, jLimit);
            aRx += Range.clip(targetARx - aRx, -jLimit, jLimit);

            aX = Range.clip(aX, -MAX_RATE, MAX_RATE);
            aY = Range.clip(aY, -MAX_RATE, MAX_RATE);
            aRx = Range.clip(aRx, -MAX_RATE, MAX_RATE);

            vX += aX * dt;
            vY += aY * dt;
            vRx += aRx * dt;

            outX = vX; outY = vY; outRx = vRx;
            filterX = outX; filterY = outY; filterRx = outRx;
        } else {
            filterY = STICK_ALPHA * rawY + (1.0 - STICK_ALPHA) * filterY;
            filterX = STICK_ALPHA * rawX + (1.0 - STICK_ALPHA) * filterX;
            filterRx = STICK_ALPHA * rawRx + (1.0 - STICK_ALPHA) * filterRx;

            outY = filterY;
            outX = filterX;
            outRx = filterRx;

            vX = outX; vY = outY; vRx = outRx;
            aX = 0; aY = 0; aRx = 0;
        }

        double magnitude = Math.hypot(outX, outY);
        double scaledY = 0.0, scaledX = 0.0;

        if (magnitude > DEADZONE) {
            double scalar = Math.pow((magnitude - DEADZONE) / (1.0 - DEADZONE), 3.0) * DRIVE_SPEED_LIMIT;
            scaledY = (outY / magnitude) * scalar;
            scaledX = (outX / magnitude) * scalar;
        }

        double finalRx = headingLock.calculateRotationPower(outRx, magnitude);

        // Robot-centric: this PedroPathing version's Follower.manual() takes raw
        // forward/strafe/turn powers with no field-centric conversion built in.
        follower.manual(scaledY, scaledX, finalRx);
    }
}
