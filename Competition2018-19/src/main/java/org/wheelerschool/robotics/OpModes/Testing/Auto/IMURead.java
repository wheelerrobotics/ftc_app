package org.wheelerschool.robotics.OpModes.Testing.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.wheelerschool.robotics.Autonomy.Main;
import org.wheelerschool.robotics.Hardware;

@Autonomous
@Disabled
public class IMURead extends LinearOpMode {
    Hardware hw;
    Main auto;
    @Override
    public void runOpMode() {
        hw = new Hardware(hardwareMap);
        auto = new Main(hw, this);
        auto.enable();

        telemetry.addData("State", "READY");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            Orientation o = hw.imu.getAngularOrientation();
            telemetry.addData("IMU", o.toString());
            telemetry.update();
        }
    }
}
