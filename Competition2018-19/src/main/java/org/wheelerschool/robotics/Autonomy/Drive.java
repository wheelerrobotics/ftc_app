package org.wheelerschool.robotics.Autonomy;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.wheelerschool.robotics.Hardware;

public class Drive {
    float ANGLE_DB = 1.f * (float) Math.PI/180.f;  // Angle deadband (deg)
    float ANGLE_MAX = 30.f * (float) Math.PI/180.f;  // Angle separation for which max rotation is applied (deg)
    AxesOrder IMU_ORDER = AxesOrder.ZYX;

    LinearOpMode linearOpMode;

    Hardware r;
    public Drive(Hardware r, LinearOpMode linearOpMode) {
        this.linearOpMode = linearOpMode;
        this.r = r;
    }
    public void hault() {
        r.drive.updateMotors(0, 0, 0);
    }

    public float anglePower(Orientation start, float angle) {
        Orientation current = r.imu.getAngularOrientation().toAxesOrder(this.IMU_ORDER);
        float currentChange = current.firstAngle - start.firstAngle;

        float sep = angle - currentChange;

        if (sep < -Math.PI) {
            sep += 2*Math.PI;
        } else if (sep > Math.PI) {
            sep -= 2*Math.PI;
        }

        float power;
        if (Math.abs(sep) > this.ANGLE_MAX) {
            power = Math.copySign(1, sep);
        } else if (Math.abs(sep) < this.ANGLE_DB) {
            power = 0;
        } else {
            power = sep / this.ANGLE_MAX;
        }

        Log.d("auto", String.format("change: %f, delta: %f, power: %f", currentChange, sep, power));
        return power;
    }


    public void turnAngle(float angle, float gain) {
        Orientation start = r.imu.getAngularOrientation().toAxesOrder(this.IMU_ORDER);
        Log.d("auto", "First");
        float power;
        do {
            Log.d("auto", "Here");
            power = anglePower(start, angle) * gain;
            r.drive.updateMotors(0, 0, power);
        } while (power != 0 && linearOpMode.opModeIsActive());

        hault();
    }
}