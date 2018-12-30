package org.wheelerschool.robotics.Autonomy;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Drive {
    Main a;
    public Drive(Main a) {
        this.a = a;
    }
    public void hault() {
        a.r.drive.updateMotors(0, 0, 0);
    }

    public float anglePower(Orientation start, float angle) {
        Orientation current = a.r.imu.getAngularOrientation().toAxesOrder(a.IMU_ORDER);
        float sep = start.firstAngle - current.firstAngle;

        float power;
        if (sep > a.ANGLE_MAX) {
            power = 1;
        } else if (Math.abs(sep) > a.ANGLE_DB) {
            power = 0;
        } else {
            power = sep / a.ANGLE_MAX;
        }

        return power;
    }


    public void turnAngle(float angle, float gain) {
        Orientation start = a.r.imu.getAngularOrientation().toAxesOrder(a.IMU_ORDER);
        float power;
        do {
            power = anglePower(start, angle) * gain;
            a.r.drive.updateMotors(0, 0, power);
        } while (power != 0);

        hault();
    }
}