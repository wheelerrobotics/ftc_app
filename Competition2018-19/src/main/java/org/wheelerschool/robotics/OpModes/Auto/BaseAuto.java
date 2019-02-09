package org.wheelerschool.robotics.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.wheelerschool.robotics.Autonomy.Main;
import org.wheelerschool.robotics.Autonomy.VisionDecoder;
import org.wheelerschool.robotics.Hardware;

public abstract class BaseAuto extends LinearOpMode {
    float STRAIGHT_DRIVE_PWR = 1.f;
    float ROTATE_DRIVE_PWR = 1.f;

    public int CENTER_MINERAL_DISTANCE;
    public int LEFT_MINERAL_DISTANCE;

    Hardware hw;
    Main auto;

    VisionDecoder.Position goldPosition = null;

    private void initHW() {
        hw.armExt.moveTo(0, 0.5);
        hw.armAngle.moveTo(0, 0.5);
        hw.lift.moveTo(0, 0.5);
        hw.drop.setState(false);
    }

    protected void stageDrop() throws InterruptedException {
        VisionDecoder.Position goldCam = null;
        for (int i=0; i<50; i++) {
            goldCam = auto.decoder.frameDetectedDualMineral(true);

            if (goldCam != VisionDecoder.Position.UNKNOWN) break;
        }

        switch (goldCam) {  // Position in field
            case LEFT:
                telemetry.addData("Position", "LEFT");
                goldPosition = VisionDecoder.Position.LEFT;
                break;
            case RIGHT:
                telemetry.addData("Position", "CENTER");
                goldPosition = VisionDecoder.Position.CENTER;
                break;
            case NONE:
                telemetry.addData("Position", "RIGHT");
                goldPosition = VisionDecoder.Position.RIGHT;
                break;
            case UNKNOWN:
                telemetry.addData("Position", "ERR");
                goldPosition = VisionDecoder.Position.CENTER;  // DEFAULT
                break;
        }
        telemetry.update();

        hw.lift.moveTo(1, 1);

        Thread.sleep(3000);

        auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 100);  // UNHOOK

        auto.drive.turnAngle((float) -Math.PI/2.f, ROTATE_DRIVE_PWR);
        hw.lift.moveTo(0, 0.75f);
    }

    protected void mineralOperation() throws InterruptedException {

        if (goldPosition == VisionDecoder.Position.CENTER) {
            //auto.drive.turnAngle(0.0611f, ROTATE_DRIVE_PWR);  // Small correction angle
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, CENTER_MINERAL_DISTANCE+80);
        } else {
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 80);
            int moveSign;
            if (goldPosition == VisionDecoder.Position.LEFT) {
                moveSign = 1;
            } else if (goldPosition == VisionDecoder.Position.RIGHT) {
                moveSign = -1;
            } else {
                throw new InterruptedException("Position logic failure!");
            }

            float mineralAlignAngle;
            int mineralDriveDistance;
            if (moveSign > 0) {  // LEFT
                mineralAlignAngle = ((float) Math.PI/4.f)-0.2f;  // MEASURED ANGLE
                mineralDriveDistance = LEFT_MINERAL_DISTANCE;
            } else {  // RIGHT
                mineralAlignAngle = -0.4286f;  // MEASURED ANGLE
                mineralDriveDistance = 720;
            }
            auto.drive.turnAngle(mineralAlignAngle, ROTATE_DRIVE_PWR);
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, mineralDriveDistance);

            //  MINERAL HAS NOW BEEN PUSHED
        }
    }


    @Override
    public void runOpMode() throws InterruptedException {
        hw = new Hardware(hardwareMap);
        auto = new Main(hw, this);
        auto.enable();

        initHW();

        auto.waitForStart();
    }
}
