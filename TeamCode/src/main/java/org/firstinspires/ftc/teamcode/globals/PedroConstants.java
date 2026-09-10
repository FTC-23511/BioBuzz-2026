package org.firstinspires.ftc.teamcode.globals;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.algorithm.Foresight;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.OctoQuadConfig;
import com.pedropathing.revhub.localizers.OctoQuadLocalizer;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Drivetrain geometry, localizer, and Foresight braking configuration used to build the
 * PedroPathing {@link Follower} via {@link #create(HardwareMap)}. Tunable mechanism and
 * OpMode constants live separately in {@link Constants} and {@link TeleOpConstants}.
 */
public class PedroConstants {
    public static final MecanumConfig DRIVE_CONFIG = new MecanumConfig(
            c -> {
                c.frontLeftName.set("FL");
                c.backLeftName.set("BL");
                c.frontRightName.set("FR");
                c.backRightName.set("BR");

                c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
                c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);

                c.manualBrakeMode.set(true);
            }
    );

    public static final OctoQuadConfig LOCALIZER_CONFIG = new OctoQuadConfig(c -> {
        c.name.set("octoquad");
        c.ticksPerUnit.set(19.89436789);
        c.encoderResolutionUnit.set(DistanceUnit.MM);
        c.headingScalar.set(1.0168);
        c.xPodDirection.set(OctoQuad.EncoderDirection.REVERSE);
        c.yPodDirection.set(OctoQuad.EncoderDirection.FORWARD);
        c.i2cRecoveryMode.set(OctoQuad.I2cRecoveryMode.MODE_1_PERIPH_RST_ON_FRAME_ERR);
        c.offsetUnits.set(DistanceUnit.INCH);
        //c.xPodOffset.set(-3.95);
        //c.yPodOffset.set(-5.67);
        c.xPodOffset.set(0.0);
        c.yPodOffset.set(0.0);
    });

    public static final ForesightConfig FORESIGHT_CONFIG = new ForesightConfig(
            c -> {
                Controller largeTranslationalForward = Controller.proportional(.3);
                Controller smallTranslationalForward = Controller.proportional(.1);
                Controller smallTranslationalLateral = Controller.proportional(.1);
                Controller largeTranslationalLateral = Controller.proportional(.3);

                c.forwardTranslational.set(Controller.piecewise(smallTranslationalForward).put(2.5, largeTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(smallTranslationalLateral).put(2.5, largeTranslationalLateral));

                c.brake.set(Controller.proportionalFeedforward(0.005)); // 0.009

                c.maxBrakingPower.set(0.3);

                Controller largeHeading = Controller.proportional(7.2);

                c.headingFeedback.set(largeHeading);
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.0532, 0.0069));

                c.linearBrakeCoefficients.set(Matrix.diag(0.0633, 0.0633));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.00146, 0.00146));

                c.maxAchievableStrafeVelocity.set(150.0);
                c.maxAchievableForwardVelocity.set(150.0);

                c.naturalForwardDeceleration.set(64.33);
                c.naturalStrafeDeceleration.set(48.43);

                c.coast.set(Controller.proportionalFeedforward(0.0105).plus(Controller.staticFeedforward(0.015)));

                c.cosineScale.set(false);

                c.headingDriveRatio.set(0.0);
            }
    );

    /**
     * Builds the PedroPathing follower for the drivetrain.
     *
     * @param h hardware map to resolve the drive motors and localizer from
     * @return a follower configured with {@link #DRIVE_CONFIG}, {@link #LOCALIZER_CONFIG}, and {@link #FORESIGHT_CONFIG}
     */
    public static Follower create(HardwareMap h) {
        return new Follower(new OctoQuadLocalizer(h, LOCALIZER_CONFIG), new Mecanum(h, DRIVE_CONFIG), new Foresight(FORESIGHT_CONFIG));
    }
}
