package org.firstinspires.ftc.teamcode.commandbase.subsystem;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDFController;

import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * Holds the two-motor flywheel at a target surface speed with a velocity PIDF loop. Both
 * motors drive the same shaft and are commanded together as one motor group, while the loop
 * closes on the encoder plugged into the leftLaunchMotor port.
 *
 * <p>Pick a shot with {@link #setFlywheel(FlywheelState)}, or set a raw target with
 * {@link #setFlywheelVelocity(double)} when tuning. Always gate feeding on
 * {@link #readyToLaunch()}, since feeding a flywheel that has not spun up throws the artifact
 * short.
 */
@Config
public class Launcher extends SubsystemBase {
    private final Robot robot = Robot.getInstance();
    private final PIDFController flywheelController = new PIDFController(FLYWHEEL_PIDF_COEFFICIENTS);

    public enum FlywheelState {
        OFF,
        NEAR,
        FAR
    }

    public static FlywheelState flywheelState = FlywheelState.OFF;

    // False when the flywheel is commanded off, so the loop does not fight a coasting wheel
    private boolean activeControl = false;

    public Launcher() {
        flywheelController.setTolerance(FLYWHEEL_VEL_TOLERANCE);
    }

    /**
     * Stops the flywheel and clears its state at the start of an OpMode. Safe to call from a
     * testing OpMode, since stopping never moves anything.
     */
    public void init() {
        setFlywheel(FlywheelState.OFF);
    }

    /**
     * Spins the flywheel up to one of the preset shot distances, whose velocities are tunable
     * in the Constants class.
     *
     * @param state the preset to command
     */
    public void setFlywheel(FlywheelState state) {
        switch (state) {
            case NEAR:
                setFlywheelVelocity(LAUNCHER_NEAR_VELOCITY);
                break;
            case FAR:
                setFlywheelVelocity(LAUNCHER_FAR_VELOCITY);
                break;
            case OFF:
            default:
                setFlywheelVelocity(0);
                break;
        }

        flywheelState = state;
    }

    /**
     * Sets the flywheel target directly, bypassing the presets. The launcher tuning OpMode
     * uses this to sweep velocities and find what each shot distance actually needs.
     *
     * @param targetVelocity the target surface speed in ticks per second, clipped to the
     *                       achievable range of the flywheel
     */
    public void setFlywheelVelocity(double targetVelocity) {
        flywheelController.setSetPoint(Range.clip(targetVelocity, 0, FLYWHEEL_MAX_VELOCITY));
        activeControl = flywheelController.getSetPoint() > 0;

        if (!activeControl) {
            // Drop the accumulated integral so the next spin-up does not inherit it
            flywheelController.reset();
        }
    }

    /**
     * Reads the measured speed of the flywheel from the launch encoder.
     *
     * @return the current surface speed in ticks per second
     */
    public double getFlywheelVelocity() {
        return robot.launchEncoder.getCorrectedVelocity();
    }

    /**
     * Reads the speed the velocity loop is currently driving towards.
     *
     * @return the target surface speed in ticks per second
     */
    public double getFlywheelTarget() {
        return flywheelController.getSetPoint();
    }

    /**
     * Reports whether the velocity loop is driving the flywheel or letting it coast.
     *
     * @return true when a non-zero target is commanded
     */
    public boolean getActiveControl() {
        return activeControl;
    }

    /**
     * Reports whether the flywheel is spun up and holding its target, which is to say whether
     * feeding an artifact right now would actually launch it to the commanded distance.
     *
     * @return true when the flywheel is driven and within tolerance of its target
     */
    public boolean readyToLaunch() {
        return activeControl && flywheelController.atSetPoint();
    }

    private void update() {
        robot.profiler.start("Launcher Update");

        // Re-read every loop so Dashboard edits take effect without restarting the OpMode
        flywheelController.setCoefficients(FLYWHEEL_PIDF_COEFFICIENTS);
        flywheelController.setTolerance(FLYWHEEL_VEL_TOLERANCE);

        if (activeControl) {
            // The flywheel only ever spins one way, so the output is clipped at 0. Letting the
            // loop command reverse power would brake the wheel against its own inertia and
            // stall the shot instead of settling it
            robot.launchMotors.set(Range.clip(flywheelController.calculate(getFlywheelVelocity()), 0, 1));
        } else {
            robot.launchMotors.set(0);
        }

        robot.profiler.end("Launcher Update");
    }

    @Override
    public void periodic() {
        update();
    }
}
