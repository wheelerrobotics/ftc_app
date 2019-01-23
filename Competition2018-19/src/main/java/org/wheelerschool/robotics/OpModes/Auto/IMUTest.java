package org.wheelerschool.robotics.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.wheelerschool.robotics.Autonomy.Main;
import org.wheelerschool.robotics.Hardware;

@Autonomous
@Disabled
public class IMUTest extends LinearOpMode {
    Hardware hw;
    Main auto;

    @Override
    public void runOpMode() throws InterruptedException {
        hw = new Hardware(hardwareMap);
        auto = new Main(hw);
        //auto.enable();

        waitForStart();

        auto.drive.turnAngle((float) -Math.PI/2.f, 0.5f);
    }
}
