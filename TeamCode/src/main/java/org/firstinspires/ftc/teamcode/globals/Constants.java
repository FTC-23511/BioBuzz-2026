package org.firstinspires.ftc.teamcode.globals;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.math.Pose;

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
}
