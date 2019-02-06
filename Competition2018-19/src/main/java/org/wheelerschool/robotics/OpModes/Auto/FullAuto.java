package org.wheelerschool.robotics.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.wheelerschool.robotics.Autonomy.Main;
import org.wheelerschool.robotics.Autonomy.VisionDecoder;
import org.wheelerschool.robotics.Hardware;

@Autonomous
public class FullAuto extends LinearOpMode {
    float STRAIGHT_DRIVE_PWR = 1.f;
    Hardware hw;
    Main auto;

    private void initHW() {
        hw.lift.moveTo(0, 0.5);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        hw = new Hardware(hardwareMap);
        auto = new Main(hw, this);
        auto.enable();

        initHW();

        telemetry.addData("State", "READY");
        telemetry.update();
        waitForStart();

        VisionDecoder.Position goldCam = null;
        for (int i=0; i<50; i++) {
            goldCam = auto.decoder.frameDetectedDualMineral(true);

            if (goldCam != VisionDecoder.Position.NONE) break;
        }

        VisionDecoder.Position goldPosition = null;

        switch (goldCam) {  // Position in field
            case LEFT:
                telemetry.addData("Position", "LEFT");
                goldPosition = VisionDecoder.Position.LEFT;
                break;
            case RIGHT:
                telemetry.addData("Position", "CENTER");
                goldPosition = VisionDecoder.Position.CENTER;
                break;
            case UNKNOWN:
                telemetry.addData("Position", "RIGHT");
                goldPosition = VisionDecoder.Position.RIGHT;
                break;
            case NONE:
                telemetry.addData("Position", "ERR");
                goldPosition = VisionDecoder.Position.CENTER;  // DEFAULT
                break;
        }
        telemetry.update();

        hw.lift.moveTo(1, 1);

        Thread.sleep(3000);

        auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 100);  // UNHOOK

        auto.drive.turnAngle((float) -Math.PI/2.f, 0.75f);
        hw.lift.moveTo(0, 0.75f);
        Thread.sleep(500);

        if (goldPosition == VisionDecoder.Position.CENTER) {
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 680);
            auto.drive.turnAngle((float) Math.PI, 0.75f);
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
                mineralAlignAngle = ((float) Math.PI/4.f)-0.1f;  // MEASURED ANGLE
                mineralDriveDistance = 1015;
            } else {  // RIGHT
                mineralAlignAngle = -0.4286f;  // MEASURED ANGLE
                mineralDriveDistance = 750;
            }
            auto.drive.turnAngle(mineralAlignAngle, 0.75f);
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, mineralDriveDistance);

            if (moveSign > 0) {  // LEFT
                auto.drive.turnAngle(((float) Math.PI) / 2.f + 0.314f, 0.75f);
                auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -450);
            } else {  // RIGHT
                auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -250);
                auto.drive.turnAngle(-((float) Math.PI) / 4.f, 0.75f);
                auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -1350);
                auto.drive.turnAngle(-3.f*((float) Math.PI) / 4.f, 0.75f);

                // SLAM WALL ALIGN
                hw.drive.updateMotors(0.5f,0,0);
                Thread.sleep(2000);
                hw.drive.updateMotors(-0.5f,0,0);
                Thread.sleep(500);
                hw.drive.updateMotors(0f,0,0);

                auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -1300);
            }
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 1600);
        }


    }
}
