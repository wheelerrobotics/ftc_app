package org.wheelerschool.robotics.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.wheelerschool.robotics.Autonomy.Main;
import org.wheelerschool.robotics.Autonomy.VisionDecoder;
import org.wheelerschool.robotics.Hardware;

@Autonomous
public class FullAuto extends LinearOpMode {
    Hardware hw;
    Main auto;

    private void initHW() {
        hw.lift.moveTo(0, 0.5);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        hw = new Hardware(hardwareMap);
        auto = new Main(hw);
        auto.enable();

        initHW();

        telemetry.addData("State", "READY");
        telemetry.update();
        waitForStart();

        hw.lift.moveTo(1, 1);

        Thread.sleep(5000);

        hw.drive.updateMotors(0, 1, 0);
        Thread.sleep(300);
        hw.drive.updateMotors(0, 0, 0);

        auto.drive.turnAngle((float) -Math.PI/2.f, 0.75f);
        hw.lift.moveTo(0, 0.75f);
        Thread.sleep(500);

        // Drive fwd to narrow FOV of camera for sampling
        hw.drive.updateMotors(0, 0.7f, 0);
        Thread.sleep(400);

        int mineralStep = 0;
        for (; mineralStep<3; mineralStep++) {
            boolean goldDetected = false;
            for (int i=0; i<50; i++) {
                if (auto.decoder.frameDetectedMineral() == VisionDecoder.Mineral.GOLD) {
                    goldDetected = true;
                    break;
                }
            }

            // Skip rotate for next position if gold detected or is last mineral step
            if (goldDetected || mineralStep == 2) {
                break;
            }

            auto.drive.turnAngle((float) -Math.PI/6.f, 0.75f);
            Thread.sleep(500);
        }

        telemetry.addData("Selected Angle", mineralStep);
        telemetry.update();

        auto.drive.turnAngle((float) Math.PI/6.f, 0.75f);
        Thread.sleep(500);

        hw.drive.updateMotors(0, 0.7f, 0);
        Thread.sleep(3000);

        hw.drive.updateMotors(0, -0.7f, 0);
        Thread.sleep(500);
    }
}
