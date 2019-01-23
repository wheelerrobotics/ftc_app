package org.wheelerschool.robotics.OpModes.Maintenance;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.wheelerschool.robotics.Hardware;

@Autonomous(name = "RETRACT")
public class Retract extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Hardware r = new Hardware(hardwareMap);

        r.lift.moveTo(0, 0.5);

        waitForStart();
    }
}
