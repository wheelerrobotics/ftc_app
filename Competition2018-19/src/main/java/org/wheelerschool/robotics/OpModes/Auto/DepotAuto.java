package org.wheelerschool.robotics.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.wheelerschool.robotics.Autonomy.VisionDecoder;

@Autonomous
public class DepotAuto extends BaseAuto {

    protected void depositMarker() throws InterruptedException {
        if (goldPosition == VisionDecoder.Position.CENTER) {
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -160);
            auto.drive.turnAngle(((float) Math.PI) / 4.f, ROTATE_DRIVE_PWR);
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 800);  // WALL SLAM
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -100);
            auto.drive.turnAngle(((float) Math.PI) / 2.f, ROTATE_DRIVE_PWR);
        } else if (goldPosition == VisionDecoder.Position.LEFT) {  // LEFT
            auto.drive.turnAngle(((float) Math.PI) / 2.f + 0.214f, 0.75f);
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -450);
        } else if (goldPosition == VisionDecoder.Position.RIGHT) {  // RIGHT
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -250);
            auto.drive.turnAngle(-((float) Math.PI) / 4.f, 0.75f);

            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -900);
            auto.drive.turnAngle(-((float) Math.PI) / 4.f, 0.75f);
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -600);  // WALL SLAM
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 120);
            auto.drive.turnAngle(-((float) Math.PI) / 2.f, 0.75f);
            auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, -1100);
        }

        hw.drop.setState(true);
        auto.drive.turnAngle(-0.1f, ROTATE_DRIVE_PWR); // ensure miss of other minerals
    }

    protected void parkInCrater() {
        auto.drive.forwardDistance(STRAIGHT_DRIVE_PWR, 1600);
        hw.armAngle.moveTo(-1, 1f);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        CENTER_MINERAL_DISTANCE = 940;
        LEFT_MINERAL_DISTANCE = 1015;

        stageDrop();

        mineralOperation();

        depositMarker();

        Thread.sleep(1000);

        parkInCrater();
    }
}
