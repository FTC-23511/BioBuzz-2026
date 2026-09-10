package org.firstinspires.ftc.teamcode.globals;

import com.acmerobotics.dashboard.config.Config;

/**
 * Tunable TeleOp constants for driver control, heading lock, and vision-assisted alignment,
 * editable live through FTC Dashboard.
 */
@Config
public class TeleOpConstants {
    public static double DRIVE_SPEED_LIMIT = 0.75;
    public static double TURN_SPEED_LIMIT = 0.675;
    public static double DEADZONE = 0.05;
    public static double STICK_ALPHA = 0.4;
    public static double DRIVE_MAX_ACCEL = 3.5;
    public static double DRIVE_JERK_MULTIPLIER = 12.0;

    public static double HEADING_LOCK_KP = 0.55;
    public static double HEADING_LOCK_KD = 0.09;
    public static double HEADING_LOCK_MAX_POWER = 0.65;
    public static double HEADING_LOCK_TOLERANCE_RAD = Math.toRadians(1.0);
    public static double HEADING_LOCK_RAMP_SEC = 0.65;
    public static double HEADING_LOCK_SETTLE_VELOCITY_THRESHOLD = 0.12;

    public static int DESIRED_TAG_ID = 586;
    public static double DESIRED_DISTANCE = 19.5;
    public static double ALIGN_UPDATE_SECONDS = 0.1;
    public static double ALIGN_MAX_POWER = 0.6;
    public static double CAMERA_FORWARD_OFFSET = 4.0;
    public static double CAMERA_LEFT_OFFSET = -1.5;
    public static double CAMERA_YAW_OFFSET = -3.0;
    public static double CAMERA_PITCH_OFFSET = 2.5;
    public static double FILTER_ALPHA = 0.4;
    public static int REQUIRED_STABLE_FRAMES = 1;
    public static double MAX_TRANSLATION_JUMP = 5.0;
    public static double MAX_YAW_JUMP_DEG = 10.0;
    public static double PATH_UPDATE_XY_INCHES = 0.5;
    public static double PATH_UPDATE_HEADING_DEG = 1.0;

    public static double SERVO_HOME = 0.0;
    public static double SERVO_MAX = 1.0;
    public static double ANIMATION_DURATION_SEC = 6.0;
    public static double ANIMATION_STEP_SEC = 0.25;

    public static double DASHBOARD_TAG_X = 0.0;
    public static double DASHBOARD_TAG_Y = 40.0;
}
