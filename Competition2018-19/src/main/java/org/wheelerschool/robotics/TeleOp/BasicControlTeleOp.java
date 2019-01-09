package org.wheelerschool.robotics.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.wheelerschool.robotics.Hardware;
import org.wheelerschool.robotics.robotlib.util.joystick.CumulativeControl;
import org.wheelerschool.robotics.robotlib.util.joystick.JoystickButtonUpdated;

@TeleOp(name="Control")
public class BasicControlTeleOp extends OpMode {
    // CONST:
    final double INTAKE_POWER = 1;

    // Hardware:
    Hardware robot;

    // Joystick:
    CumulativeControl intakeAngle;

    @Override
    public void init() {
        robot = new Hardware(hardwareMap);

        intakeAngle = new CumulativeControl(0);
    }

    @Override
    public void loop() {
        robot.drive.updateMotors(-gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        //robot.drive.calcDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);

        robot.armAngle.setPower(gamepad2.left_stick_y);
        robot.armExt.setPower(gamepad2.right_stick_y);


        // Intake Angle:
        float angleCtl = gamepad2.right_trigger - gamepad2.left_trigger;

        intakeAngle.setCps(angleCtl);
        intakeAngle.positionTick(angleCtl != 0, getRuntime());
        robot.intakeAngle.setPosition(intakeAngle.getPosition());

        // Intake Drive:
        double intakeDrive = INTAKE_POWER;
        if (gamepad2.y) {}
        else if (gamepad2.a) {
            intakeDrive = -intakeDrive;
        } else {
            intakeDrive = 0;
        }

        robot.intakeDrive.setPower(intakeDrive);

        float liftPower = 1;
        if (gamepad1.dpad_up) {
            robot.lift.setPower(liftPower);
        } else if (gamepad1.dpad_down) {
                robot.lift.setPower(-liftPower);
        } else {
            robot.lift.setPower(0);
        }

        if (gamepad2.a) {
            robot.drop.setState(true);
        } else {
            robot.drop.setState(false);
        }
    }
}
