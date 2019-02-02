package org.wheelerschool.robotics.OpModes.Maintenance;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.wheelerschool.robotics.Hardware;

@Autonomous(name = "DRIVE VERIFY")
public class DriveMotorVerify extends LinearOpMode {
    Hardware robot;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Hardware(hardwareMap);
        waitForStart();

        float driveSpeed = 0.25f;

        DcMotor[] motors = new DcMotor[] {
                robot.drive.fRight,
                robot.drive.fLeft,
                robot.drive.bLeft,
                robot.drive.bRight
        };

        for (DcMotor m : motors) {
            m.setPower(driveSpeed);
            Thread.sleep(1000);
            m.setPower(0);
        }
    }
}
