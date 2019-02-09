package org.wheelerschool.robotics.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.wheelerschool.robotics.Autonomy.VisionDecoder;

@Autonomous
public class CraterAuto extends BaseAuto {

    protected void depositMarker() throws InterruptedException {
        if (goldPosition == VisionDecoder.Position.CENTER) {
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -380);
            auto.drive.turnAngle(-((float)Math.PI)/2 - 0.2f, ROTATE_DRIVE_PWR);
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -1000);
            auto.drive.turnAngle(-((float)Math.PI)/6, ROTATE_DRIVE_PWR);
        }  else {
            if (goldPosition == VisionDecoder.Position.LEFT) {  // LEFT
                auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -200);
                auto.drive.turnAngle(-((float) Math.PI)/2.f - ((float) Math.PI)/6.f, ROTATE_DRIVE_PWR);
                auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -500);
                auto.drive.turnAngle(-((float) Math.PI)/6.f - 0.1f, ROTATE_DRIVE_PWR);

            } else if (goldPosition == VisionDecoder.Position.RIGHT) {  // RIGHT
                auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -500);
                auto.drive.turnAngle(-((float) Math.PI)/2.f + ((float) Math.PI)/6.f - 0.2f, ROTATE_DRIVE_PWR);
                auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -1000);
                auto.drive.turnAngle(-((float) Math.PI)/6.f - 0.1f, ROTATE_DRIVE_PWR);
            }
        }

        auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -280);
        auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 120);
        auto.drive.turnAngle(((float)Math.PI)/2, ROTATE_DRIVE_PWR);
        auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -1120);

        hw.drop.setState(true);
    }

    protected void parkInCrater() {
        auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 1400);
        hw.armAngle.moveTo(-1, 1f);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        CENTER_MINERAL_DISTANCE = 580;
        LEFT_MINERAL_DISTANCE = 770;

        stageDrop();

        mineralOperation();

        depositMarker();

        Thread.sleep(1000);

        parkInCrater();
    }
}
