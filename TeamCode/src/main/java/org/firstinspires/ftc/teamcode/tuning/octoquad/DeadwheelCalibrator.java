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

/*
 * This OpMode helps calibrate the ticks/mm and directions for your deadwheels
 */
@TeleOp
@Disabled
public class DeadwheelCalibrator extends LinearOpMode
{
    static final int FEET_TO_PUSH = 8;
    static final double FT_TO_MM = 12 * 25.4;

    @Override
    public void runOpMode()
    {
        OctoQuad oq = hardwareMap.get(OctoQuad.class, "octoquad");

        oq.resetEverything();

        while (!isStopRequested() && !bumperPress())
        {
            sleep(20);
        }

        oq.resetAllPositions();
        while (!isStopRequested() && !bumperPress())
        {
            sleep(20);
        }

        OctoQuad.EncoderDataBlock block = oq.readAllEncoderData();

        int x = findAbsMax(block);
        int xCount = block.positions[x];

        while (!isStopRequested() && !bumperPress())
        {
            sleep(20);
        }

        oq.resetAllPositions();
        while (!isStopRequested() && !bumperPress())
        {
            sleep(20);
        }

        block = oq.readAllEncoderData();
        int y = findAbsMax(block);
        int yCount = block.positions[y];

        boolean xNeedsReversed = xCount < 0;
        boolean yNeedsReversed = yCount < 0;

        while (!isStopRequested())
        {
            sleep(100);
        }
    }

    int findAbsMax(OctoQuad.EncoderDataBlock blk)
    {
        int absMax = 0;
        int port = 0;

        for (int i = 0; i < blk.positions.length; i++)
        {
            if (Math.abs(blk.positions[i]) > absMax)
            {
                absMax = Math.abs(blk.positions[i]);
                port = i;
            }
        }

        return port;
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
