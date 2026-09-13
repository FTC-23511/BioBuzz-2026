package org.firstinspires.ftc.teamcode.globals;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MathFunctionsTest {
    private static final double DELTA = 1e-9;

    @Test
    public void deadZoneZeroesInputInsideThreshold() {
        assertEquals(0.0, MathFunctions.deadZone(0.04, 0.05), DELTA);
        assertEquals(0.0, MathFunctions.deadZone(-0.04, 0.05), DELTA);
        assertEquals(0.0, MathFunctions.deadZone(0.0, 0.05), DELTA);
    }

    @Test
    public void deadZonePassesInputOutsideThreshold() {
        assertEquals(0.06, MathFunctions.deadZone(0.06, 0.05), DELTA);
        assertEquals(-0.06, MathFunctions.deadZone(-0.06, 0.05), DELTA);
        assertEquals(1.0, MathFunctions.deadZone(1.0, 0.05), DELTA);
    }

    @Test
    public void deadZoneKeepsInputExactlyOnThreshold() {
        // The comparison is strict, so a value sitting on the threshold survives
        assertEquals(0.05, MathFunctions.deadZone(0.05, 0.05), DELTA);
    }

    @Test
    public void toRobotCentricIsIdentityAtZeroHeading() {
        assertArrayEquals(
                new double[]{0.7, -0.2},
                MathFunctions.toRobotCentric(0.7, -0.2, 0),
                DELTA
        );
    }

    @Test
    public void toRobotCentricRotatesQuarterTurn() {
        // Facing field +y, a field-forward command has to become a strafe to the robot's right
        assertArrayEquals(
                new double[]{0.0, -1.0},
                MathFunctions.toRobotCentric(1, 0, Math.PI / 2),
                DELTA
        );

        // A field-left command becomes forward from the robot's point of view
        assertArrayEquals(
                new double[]{1.0, 0.0},
                MathFunctions.toRobotCentric(0, 1, Math.PI / 2),
                DELTA
        );
    }

    @Test
    public void toRobotCentricReversesAtHalfTurn() {
        assertArrayEquals(
                new double[]{-1.0, -0.5},
                MathFunctions.toRobotCentric(1, 0.5, Math.PI),
                DELTA
        );
    }

    @Test
    public void toRobotCentricPreservesMagnitude() {
        double forward = 0.6;
        double lateral = -0.8;

        for (double heading = -Math.PI; heading <= Math.PI; heading += Math.PI / 8) {
            double[] robotRelative = MathFunctions.toRobotCentric(forward, lateral, heading);

            assertEquals(
                    Math.hypot(forward, lateral),
                    Math.hypot(robotRelative[0], robotRelative[1]),
                    DELTA
            );
        }
    }
}
