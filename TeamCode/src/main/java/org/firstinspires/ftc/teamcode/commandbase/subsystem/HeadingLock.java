package org.firstinspires.ftc.teamcode.commandbase.subsystem;

import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.DEADZONE;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.HEADING_LOCK_KD;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.HEADING_LOCK_KP;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.HEADING_LOCK_MAX_POWER;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.HEADING_LOCK_RAMP_SEC;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.HEADING_LOCK_TOLERANCE_RAD;
import static org.firstinspires.ftc.teamcode.globals.TeleOpConstants.TURN_SPEED_LIMIT;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * PID-based heading hold used by {@link Drive} between manual turns: coasts while the robot is
 * still decelerating from a stick input, then rigidly locks onto the last heading.
 */
public class HeadingLock {

    private final Follower follower;
    private final ElapsedTime timer = new ElapsedTime();
    private final ElapsedTime rampTimer = new ElapsedTime();

    private double targetHeading = 0.0;
    private double lastError = 0.0;
    private double lastHeading = 0.0;
    private double filteredVelocity = 0.0;

    private boolean isCoastingAfterTurn = false;
    private boolean isLockInitialized = false;

    private static final double SETTLE_VELOCITY_THRESHOLD = 0.12;

    public HeadingLock(Follower follower) {
        this.follower = follower;
        timer.reset();
        rampTimer.reset();
    }

    public double calculateRotationPower(double rawRx, double translationMagnitude) {
        double currentHeading = follower.pose().heading();
        double dt = timer.seconds();
        timer.reset();

        double rawVelocity = (dt > 0) ? Math.abs(currentHeading - lastHeading) / dt : 0.0;
        lastHeading = currentHeading;
        filteredVelocity = (0.15 * rawVelocity) + (0.85 * filteredVelocity);

        double rx;
        double absRx = Math.abs(rawRx);

        if (absRx > DEADZONE) {
            rx = rawRx * TURN_SPEED_LIMIT;
            targetHeading = currentHeading;
            lastError = 0.0;
            isCoastingAfterTurn = true;
            isLockInitialized = false;
            rampTimer.reset();
        } else {
            if (isCoastingAfterTurn && filteredVelocity > SETTLE_VELOCITY_THRESHOLD) {
                rx = 0.0;
                targetHeading = currentHeading;
                lastError = 0.0;
                rampTimer.reset();
            } else {
                isCoastingAfterTurn = false;

                double headingError = targetHeading - currentHeading;
                headingError = Math.atan2(Math.sin(headingError), Math.cos(headingError));

                if (!isLockInitialized) {
                    lastError = headingError;
                    isLockInitialized = true;
                    rampTimer.reset();
                }

                if (Math.abs(headingError) < HEADING_LOCK_TOLERANCE_RAD) {
                    rx = 0.0;
                } else {
                    double rampScalar = Range.clip(rampTimer.seconds() / HEADING_LOCK_RAMP_SEC, 0.0, 1.0);
                    double derivative = (dt > 0) ? (headingError - lastError) / dt : 0.0;

                    double rawPidOutput = (headingError * HEADING_LOCK_KP) + (derivative * HEADING_LOCK_KD);
                    rx = rawPidOutput * rampScalar;

                    rx = Range.clip(rx, -HEADING_LOCK_MAX_POWER, HEADING_LOCK_MAX_POWER);
                }
                lastError = headingError;
            }
        }
        return rx;
    }

    public void resetHeading(double newHeading) {
        targetHeading = newHeading;
        lastHeading = newHeading;
        filteredVelocity = 0.0;
        lastError = 0.0;
        isCoastingAfterTurn = false;
        isLockInitialized = false;
        rampTimer.reset();
        timer.reset();
    }

    public double getTargetHeading() {
        return targetHeading;
    }
}
