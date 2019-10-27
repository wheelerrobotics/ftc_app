package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp
public class TestOpMode extends OpMode {
    DcMotor frontLeft;
    DcMotor backLeft;
    DcMotor frontRight;
    DcMotor backRight;

    DistanceSensor distance;

    @Override
    public void init() {
        frontLeft = hardwareMap.dcMotor.get("driveFrontLeft");
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        backLeft = hardwareMap.dcMotor.get("driveBackLeft");
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontRight = hardwareMap.dcMotor.get("driveFrontRight");
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        backRight = hardwareMap.dcMotor.get("driveBackRight");
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        distance = hardwareMap.get(DistanceSensor.class, "distance");
    }

    @Override
    public void loop() {
        double left_motor_power = -gamepad1.left_stick_y;
        double right_motor_power = -gamepad1.right_stick_y;

        frontLeft.setPower(left_motor_power);
        backLeft.setPower(left_motor_power);

        frontRight.setPower(right_motor_power);
        backRight.setPower(right_motor_power);

        telemetry.addData("LEFT ", left_motor_power);
        telemetry.addData("RIGHT", right_motor_power);

        telemetry.addData("FLEFT ", frontLeft.getCurrentPosition());
        telemetry.addData("BLEFT ", backLeft.getCurrentPosition());
        telemetry.addData("FRIGHT", frontRight.getCurrentPosition());
        telemetry.addData("BRIGHT", backRight.getCurrentPosition());

        telemetry.addData("DIST", distance.getDistance(DistanceUnit.CM));
    }
}
