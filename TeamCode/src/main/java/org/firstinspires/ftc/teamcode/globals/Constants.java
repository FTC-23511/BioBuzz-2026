package org.firstinspires.ftc.teamcode.globals;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/**
 * Tunable constants for the mechanisms and OpModes, editable live through FTC Dashboard.
 * Drivetrain geometry and follower gains are configured separately in
 * {@link PedroConstants}.
 */
@Config
public class Constants {
    public enum OpModeType {
        AUTO,
        TELEOP
    }

    public enum AllianceColor {
        BLUE(1), RED(-1);

        private final int multiplier;

        AllianceColor(int multiplier) {
            this.multiplier = multiplier;
        }

        /**
         * Returns the sign used to mirror alliance-dependent values measured from the center of
         * the field.
         *
         * @return 1 for blue, -1 for red
         */
        public int getMultiplier() {
            return multiplier;
        }
    }


    public static OpModeType OP_MODE_TYPE;

    public static boolean TESTING_OP_MODE = false;

    public static AllianceColor ALLIANCE_COLOR = AllianceColor.BLUE;


    public static Pose END_POSE = null;


    // Drive
    public static boolean FIELD_CENTRIC = true;
    public static double JOYSTICK_DEAD_ZONE = 0.05; // Joystick
    public static double TELEOP_MIN_SPEED = 0.4; // Power, right trigger released
    public static double TELEOP_MAX_SPEED = 1.0; // Power, right trigger pressed

    // Intake
    public static double INTAKE_FORWARD_SPEED = 1.0; // Power
    public static double INTAKE_REVERSE_SPEED = -1.0; // Power

    // Transfer
    public static double TRANSFER_FORWARD_POWER = 1.0; // Power
    public static double TRANSFER_REVERSE_POWER = -1.0; // Power

    // Launcher
    // kF carries most of the output and should be roughly 1 / FLYWHEEL_MAX_VELOCITY, leaving
    // kP to close only the remainder. UNTUNED - these are starting seeds
    public static PIDFCoefficients FLYWHEEL_PIDF_COEFFICIENTS =
            new PIDFCoefficients(0.0004, 0.0, 0.00001, 1.0 / 2800.0);
    public static double FLYWHEEL_MAX_VELOCITY = 2800.0; // Ticks/second, 1:1 goBILDA 5202 free speed
    public static double FLYWHEEL_VEL_TOLERANCE = 60.0; // Ticks/second
    public static double LAUNCHER_NEAR_VELOCITY = 1400.0; // Ticks/second
    public static double LAUNCHER_FAR_VELOCITY = 2300.0; // Ticks/second

    public static double LAUNCH_FEED_TIME = 750.0; // Milliseconds
    // Ceiling on how long a launch waits for the flywheel to reach tolerance. Without it a
    // launch scheduled against a stalled flywheel never ends and holds its requirements forever
    public static double LAUNCH_SPIN_UP_TIMEOUT = 2000.0; // Milliseconds
}
