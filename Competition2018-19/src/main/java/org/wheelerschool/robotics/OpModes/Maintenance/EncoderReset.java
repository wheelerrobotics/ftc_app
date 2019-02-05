package org.wheelerschool.robotics.OpModes.Maintenance;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.wheelerschool.robotics.Hardware;

@Autonomous(name = "RESET")
public class EncoderReset extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Hardware r = new Hardware(hardwareMap);

        waitForStart();

        r.armExt.dcMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.armAngle.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.lift.dcMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        for (DcMotor dc : r.drive.getMotors()) {
            dc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }
}
