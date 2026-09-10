package org.firstinspires.ftc.teamcode.globals;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.math.Pose;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

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

        public int getMultiplier() {
            return multiplier;
        }
    }


    public static OpModeType OP_MODE_TYPE;

    public static boolean TESTING_OP_MODE = false;

    public static AllianceColor ALLIANCE_COLOR = AllianceColor.BLUE;


    public static Pose END_POSE = null;
    public static DistanceUnit DISTANCE_UNIT = DistanceUnit.INCH;
    public static AngleUnit ANGLE_UNIT = AngleUnit.RADIANS;
    public static double INTAKE_POWER = 0.9;
    public static double REVERSE_POWER = -0.9;
    public static final int THREADS_DEFAULT = 3;
    public static final String VISION_TAG = "AprilTagProcessorImpl";
}
