package org.firstinspires.ftc.teamcode.globals;

/**
 * Generic math helpers shared across the subsystems and OpModes. Distances are in inches and
 * angles in radians. See {@link #toRobotCentric(double, double, double)} for the field-centric
 * drive conversion and {@link #deadZone(double, double)} for joystick conditioning.
 */
public class MathFunctions {
    private MathFunctions() {
    }

    /**
     * Zeroes an input whose magnitude falls below a threshold, so a released joystick that
     * rests slightly off centre does not creep the mechanism it controls.
     *
     * @param value     the raw input, normally a joystick axis
     * @param threshold the magnitude below which the input is treated as zero
     * @return the input unchanged, or zero if it is within the threshold
     */
    public static double deadZone(double value, double threshold) {
        if (Math.abs(value) < threshold) {
            return 0;
        }

        return value;
    }

    /**
     * Rotates a field-relative translation command into the robot's own frame, which is what
     * makes field-centric driving work: the driver keeps pushing the stick in one field
     * direction while the robot turns underneath it.
     *
     * <p>Both frames follow Pedro's convention of x forward, y left and heading
     * counter-clockwise-positive.
     *
     * @param forward the field-relative forward component, away from the driver
     * @param lateral the field-relative lateral component, to the driver's left
     * @param heading the robot's current heading in radians, counter-clockwise from the field x axis
     * @return a two element array holding the robot-relative forward and lateral components
     */
    public static double[] toRobotCentric(double forward, double lateral, double heading) {
        double cos = Math.cos(heading);
        double sin = Math.sin(heading);

        return new double[]{
                forward * cos + lateral * sin,
                -forward * sin + lateral * cos
        };
    }
}
