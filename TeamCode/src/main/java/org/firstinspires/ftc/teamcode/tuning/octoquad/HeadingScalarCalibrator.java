/*
 * Copyright (c) 2025 DigitalChickenLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.tuning.octoquad;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


/**
 * This OpMode helps calibrate the heading scalar for the IMU on the
 * OctoQuad FTC Edition MK2
 */
@TeleOp
@Disabled
public class HeadingScalarCalibrator extends LinearOpMode
{
    OctoQuad oq;

    int wraps = 0;
    double integratedHeading;
    float lastNormalizedHeading = 0;

    static final int SETTLE_MS = 10000;
    static final int NUM_ROTATIONS = 5;

    @Override
    public void runOpMode()
    {
        oq = hardwareMap.get(OctoQuad.class, "octoquad");

        while (!isStopRequested() && !bumperPress())
        {
            sleep(20);
        }

        oq.setLocalizerImuHeadingScalar(1.0f);
        oq.resetLocalizerAndCalibrateIMU();

        while (!isStopRequested())
        {
            sleep(20);
            if (oq.getLocalizerStatus() == OctoQuad.LocalizerStatus.RUNNING)
            {
                break;
            }
        }

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < SETTLE_MS && !isStopRequested())
        {
            sleep(20);
        }

        while (!isStopRequested() && !bumperPress())
        {
            sleep(20);
        }

        oq.setLocalizerHeading(0.0f);

        while (!isStopRequested() && !bumperPress())
        {
            OctoQuad.LocalizerDataBlock data = oq.readLocalizerData();

            if (data.crcOk)
            {
                if (data.heading_rad - lastNormalizedHeading > Math.PI / 2)
                {
                    wraps--;
                }
                else if (data.heading_rad - lastNormalizedHeading < -Math.PI / 2)
                {
                    wraps++;
                }

                integratedHeading = wraps*(2*Math.PI) + data.heading_rad;
                lastNormalizedHeading = data.heading_rad;

                sleep(20);
            }
        }

        double expectedRad = Math.PI*2*NUM_ROTATIONS;
        double actualRad = integratedHeading;

        double scaleFactor = expectedRad / actualRad;

        if (scaleFactor < 0.9 || scaleFactor > 1.1)
        {
            while (!isStopRequested())
            {
                sleep(20);
            }
        }
        else
        {
            while (!isStopRequested())
            {
                sleep(20);

                if (bumperPress())
                {
                    oq.setLocalizerImuHeadingScalar((float) scaleFactor);
                    oq.saveParametersToFlash();

                    while (!isStopRequested())
                    {
                        sleep(20);
                    }

                    return;
                }
            }
        }
    }

    boolean lastBumper;

    boolean bumperPress()
    {
        boolean bmp = gamepad1.right_bumper;
        boolean trig = bmp && !lastBumper;

        lastBumper = bmp;
        return trig;
    }
}
