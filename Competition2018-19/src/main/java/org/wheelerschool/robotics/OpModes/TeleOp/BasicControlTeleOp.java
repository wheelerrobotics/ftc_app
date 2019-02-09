package org.wheelerschool.robotics.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.wheelerschool.robotics.Hardware;
import org.wheelerschool.robotics.robotlib.util.joystick.CumulativeControl;
import org.wheelerschool.robotics.robotlib.util.joystick.JoystickButtonUpdated;

import java.util.concurrent.Callable;

@TeleOp(name="Control")
public class BasicControlTeleOp extends OpMode {
    // CONST:
    final double INTAKE_POWER = 1;

    // Hardware:
    Hardware robot;

    // Joystick:
    CumulativeControl intakeAngle;
    JoystickButtonUpdated angleUp;
    JoystickButtonUpdated angleDown;

    // Control
    Integer armExtStopPos;

    public BasicControlTeleOp() {
        super();
        msStuckDetectInit = 8000;
        msStuckDetectInitLoop = 8000;
    }

    private void debugPrint() {
        telemetry.addData("Angle IDX", robot.armAngle.currentIdx);
        telemetry.addData("Lift", robot.lift.dcMotor.getCurrentPosition());
    }

    @Override
    public void init() {
        robot = new Hardware(hardwareMap);
        robot.lift.dcMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intakeAngle = new CumulativeControl(0);

        angleUp = new JoystickButtonUpdated(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return gamepad2.dpad_up;
            }
        });
        angleDown = new JoystickButtonUpdated(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return gamepad2.dpad_down;
            }
        });
    }

    @Override
    public void loop() {
        telemetry.addData("x", gamepad1.left_stick_x);
        telemetry.addData("y", gamepad1.left_stick_y);
        telemetry.addData("rot", gamepad1.right_stick_x);
        telemetry.addData("arm angle", robot.armAngle.dcMotor.getCurrentPosition());
        robot.drive.updateMotors(gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x);
        //robot.drive.calcDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);

        if (angleUp.getValueIgnoreException().newStateTrue) {
            robot.armAngle.moveRel(-1, robot.ARM_EXT_POWER);
        } else if (angleDown.getValueIgnoreException().newStateTrue) {
            robot.armAngle.moveRel(1, robot.ARM_EXT_POWER);
        } else {
            robot.armAngle.manualOverride(gamepad2.left_stick_y);
        }


        float armExtCtl = -gamepad2.right_stick_y;
        if (armExtCtl > 0) {
            armExtStopPos = null;
            robot.armExt.dcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            armExtCtl *= 2. / 3.;
        } else if (robot.armExt.dcMotor.getCurrentPosition() < 100) {
            armExtCtl = 0;
        } else if (armExtCtl == 0 || robot.armExt.dcMotor.getCurrentPosition() < 0) {
            if (robot.armExt.dcMotor.getCurrentPosition() < 0) {
                telemetry.addData("state", "force stop");
            }
            if (armExtStopPos == null) {
                armExtStopPos = robot.armExt.dcMotor.getCurrentPosition();
                if (armExtStopPos < 0) {
                    armExtStopPos = 0;
                }
            }
            robot.armExt.dcMotor.setTargetPosition(armExtStopPos);
            robot.armExt.dcMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armExtCtl = 1/8.f;
        } else if (armExtCtl < 0) {
            armExtStopPos = null;
            robot.armExt.dcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //robot.armExt.setTargetPosition(0);
            //robot.armExt.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armExtCtl *= 1.;
        }
        telemetry.addData("arm", armExtCtl);
        robot.armExt.manualOverride(armExtCtl);


        // Intake Angle:
        float angleCtl = gamepad2.right_trigger - gamepad2.left_trigger;

        intakeAngle.setCps(angleCtl);
        intakeAngle.positionTick(angleCtl != 0, getRuntime());
        robot.intakeAngle.setPosition(intakeAngle.getPosition());

        // Intake Drive:
        double intakeDrive = INTAKE_POWER;
        if (gamepad1.y) {}
        else if (gamepad1.a) {
            intakeDrive = -intakeDrive;
        } else {
            intakeDrive = 0;
        }

        robot.intakeDrive.setPower(intakeDrive);

        // Lift Control:
        float liftGain = 1;
        if (gamepad1.dpad_up) {
            robot.lift.moveTo(1, 1.f);
        } else if (gamepad1.dpad_down) {
            robot.lift.moveTo(0, 1.f);
        } else if (gamepad1.right_bumper) {
            robot.lift.manualOverride(liftGain);
        } else if (gamepad1.left_bumper) {
            robot.lift.manualOverride(-liftGain);
        } else if (!robot.lift.motorActive()){
            robot.lift.stop();
        }
        // robot.lift.manualOverride(liftGain * (gamepad1.right_trigger - gamepad1.left_trigger));

        if (gamepad2.a) {
            robot.drop.setState(true);
        } else {
            robot.drop.setState(false);
        }

        debugPrint();
    }
}
