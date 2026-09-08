package org.firstinspires.ftc.teamcode;

import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.OctoQuadLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.globals.PedroConstants;
import org.firstinspires.ftc.teamcode.tuning.pedroPathing.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.tuning.pedroPathing.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.tuning.pedroPathing.procedures.OctoquadTuner;
import org.firstinspires.ftc.teamcode.tuning.pedroPathing.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.tuning.pedroPathing.procedures.Tests;

public class Tuning {
    @Tuner
    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    @Tuner
    public static Procedure octoquadTuner() {
        return new OctoquadTuner();
    }


    @Tuner
    public static Procedure pinpointTuner() {
        return new PinpointTuner();
    }

    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner(
                (hardwareMap) -> new OctoQuadLocalizer(hardwareMap, PedroConstants.localizerConfig),
                (hardwareMap) -> new Mecanum(hardwareMap, PedroConstants.driveConfig));
    }

    @Tuner
    public static Procedure tests() {
        return new Tests(PedroConstants::create);
    }
}
