package org.wheelerschool.robotics.OpModes.Maintenance;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.wheelerschool.robotics.Autonomy.Drive;
import org.wheelerschool.robotics.Hardware;

@TeleOp
public class DriveMotorExample extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hw = new Hardware(hardwareMap);
        Drive aut = new Drive(hw, this);
        int sign = 1;

        waitForStart();

        while(opModeIsActive()) {
            float ang = Math.copySign((float) Math.PI/2.f, sign);
            telemetry.addData("ang", ang);

            aut.turnAngle(ang, 1.f);
            Thread.sleep(2000);
            sign *= -1;
            telemetry.update();
        }
    }
}
