package org.firstinspires.ftc.teamcode.commandbase.subsystem;

import static org.firstinspires.ftc.teamcode.globals.Constants.*;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.globals.MathFunctions;
import org.firstinspires.ftc.teamcode.globals.PedroConstants;
import org.firstinspires.ftc.teamcode.globals.Robot;

/**
 * Drives the four mecanum motors and tracks the robot's pose, both through the Pedro
 * {@link Follower} built by {@link PedroConstants#create(HardwareMap)}. Pedro owns those motors
 * directly, so they are not wrapped anywhere else in the command base.
 *
 * <p>Pedro's frame is x forward, y left and heading counter-clockwise-positive, and every
 * method here uses it. Drive the robot with {@link #drive(double, double, double, double)},
 * which respects the {@code FIELD_CENTRIC} constant, and realign field-centric driving with
 * {@link #resetHeading()}.
 */
@Config
public class Drive extends SubsystemBase {
    private final Robot robot = Robot.getInstance();

    public final Follower follower;

    public Drive(HardwareMap hwMap) {
        follower = PedroConstants.create(hwMap);
    }

    /**
     * Seeds the pose estimate at the start of an OpMode. A TeleOp run straight after an Auto
     * inherits where the Auto finished, so field-centric driving still lines up with the field.
     */
    public void init() {
        if (END_POSE != null) {
            setPose(END_POSE);
        } else {
            setPose(new Pose(0, 0, 0));
        }

        // Seed the localizer before anything calls manual(), the same order the Pedro tuning
        // OpModes use. The scheduler does not run periodic() until after the OpMode starts
        follower.update();
    }

    /**
     * Drives from raw driver input, converting to the robot frame first when
     * {@code FIELD_CENTRIC} is set.
     *
     * @param forward         the forward stick, where +1 is away from the driver
     * @param lateral         the strafe stick, where +1 is to the driver's left
     * @param turn            the turn stick, where +1 is counter-clockwise
     * @param speedMultiplier the fraction of full output to apply to all three axes
     */
    public void drive(double forward, double lateral, double turn, double speedMultiplier) {
        double scaledForward = forward * speedMultiplier;
        double scaledLateral = lateral * speedMultiplier;
        double scaledTurn = turn * speedMultiplier;

        if (FIELD_CENTRIC) {
            driveFieldCentric(scaledForward, scaledLateral, scaledTurn);
        } else {
            driveRobotCentric(scaledForward, scaledLateral, scaledTurn);
        }
    }

    /**
     * Drives a field-relative command, rotating it into the robot frame by the current heading
     * so the robot moves where the driver points regardless of which way it faces.
     *
     * @param forward the field-relative forward component
     * @param lateral the field-relative lateral component
     * @param turn    the counter-clockwise-positive turn power
     */
    public void driveFieldCentric(double forward, double lateral, double turn) {
        double[] robotRelative = MathFunctions.toRobotCentric(forward, lateral, getPose().heading());

        driveRobotCentric(robotRelative[0], robotRelative[1], turn);
    }

    /**
     * Drives a command that is already in the robot's own frame.
     *
     * @param forward the robot-relative forward component
     * @param lateral the robot-relative lateral component
     * @param turn    the counter-clockwise-positive turn power
     */
    public void driveRobotCentric(double forward, double lateral, double turn) {
        follower.manual(forward, lateral, turn);
    }

    /**
     * Cuts drive power while leaving the follower's pose estimate untouched.
     */
    public void stop() {
        follower.manual(0, 0, 0);
    }

    /**
     * Reads the robot's current field position and heading.
     *
     * @return the current pose, in inches and radians
     */
    public Pose getPose() {
        return follower.pose();
    }

    /**
     * Overwrites the follower's pose estimate.
     *
     * @param pose the pose to seed, in inches and radians
     */
    public void setPose(Pose pose) {
        follower.setPose(pose);
    }

    /**
     * Zeroes the heading in place while keeping the current translation. The driver uses this
     * when field-centric drive has drifted out of alignment with the field.
     */
    public void resetHeading() {
        Pose pose = getPose();

        setPose(new Pose(pose.x(), pose.y(), 0));
    }

    private void update() {
        robot.profiler.start("Drive Update");
        follower.update();
        robot.profiler.end("Drive Update");
    }

    @Override
    public void periodic() {
        update();
    }
}
