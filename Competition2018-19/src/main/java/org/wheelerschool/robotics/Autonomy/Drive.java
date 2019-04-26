package org.wheelerschool.robotics.Autonomy;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.wheelerschool.robotics.Hardware;

import java.util.Arrays;

public class Drive {
    float ANGLE_DB = 1.f * (float) Math.PI/180.f;  // Angle deadband (deg)
    float ANGLE_MAX = 30.f * (float) Math.PI/180.f;  // Angle separation for which max rotation is applied (deg)
    float MIN_PWR = 0.3f;
    int ENC_DEADBAND = 10;
    float ENC_SCALE = 10000.f/2775.f;  // 10^4 enc ticks per 2775mm
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
            power = (1-MIN_PWR)*(sep / this.ANGLE_MAX) + Math.copySign(MIN_PWR, sep);
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
            r.drive.updateMotors(0, 0, power);  // TODO: What a roller coaster
            //double[] mV = r.drive.updateMotors(power*0.4f/1.f, 0, power*0.6f/1.f);  // TODO: Cludgy fix for broken mechanum -- remove (DON'T PUSH)
            //Log.d("ANGLE DRIVE", Arrays.toString(mV));

        } while (power != 0 && linearOpMode.opModeIsActive());

        hault();
    }

    public void forwardDistance(float power, int dist) {
        int encoder = Math.round(dist * ENC_SCALE);
        DcMotor[] motors = r.drive.getMotors();
        int[] targets = new int[motors.length];  // defaults to false

        for (int midx=0; midx<motors.length; midx++) {
            DcMotor m = motors[midx];

            targets[midx] = m.getCurrentPosition() + encoder;
            m.setTargetPosition(targets[midx]);
            m.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            m.setPower(power);
        }

        boolean atTarget = false;
        while (!atTarget && linearOpMode.opModeIsActive()) {
            atTarget = true;
            for (int midx = 0; midx < motors.length; midx++) {
                DcMotor m = motors[midx];
                if (Math.abs(m.getCurrentPosition() - targets[midx]) > ENC_DEADBAND) {
                    atTarget = false;
                }
            }
            linearOpMode.telemetry.addData("STATE", "driving...");
            linearOpMode.telemetry.update();
        }

        for (DcMotor m : motors) {
            m.setPower(0);
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}