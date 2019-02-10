package org.wheelerschool.robotics.OpModes.Testing.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.wheelerschool.robotics.Autonomy.Main;
import org.wheelerschool.robotics.Autonomy.VisionDecoder;
import org.wheelerschool.robotics.Hardware;
import org.wheelerschool.robotics.OpModes.Auto.BaseAuto;

@Autonomous
public class HangDecode extends BaseAuto {
    @Override
    public void runOpMode() throws InterruptedException {
        hw = new Hardware(hardwareMap);
        auto = new Main(hw, this);
        auto.enable();

        initHW();

        long lastGoodRead = System.currentTimeMillis();
        String goldPositionName = "NONE";
        while (!this.opModeIsActive() && !this.isStopRequested()) {
            this.telemetry.addData("STATUS", "waiting for start...");
            VisionDecoder.Position goldCam = auto.decoder.frameDetectedLowestDualMineral();
            switch (goldCam) {  // Position in field
                case LEFT:
                    goldPositionName = "LEFT";
                    lastGoodRead = System.currentTimeMillis();
                    break;
                case RIGHT:
                    goldPositionName = "CENTER";
                    lastGoodRead = System.currentTimeMillis();
                    break;
                case NONE:
                    goldPositionName = "RIGHT";
                    lastGoodRead = System.currentTimeMillis();
                    break;
                case UNKNOWN:
                    break;
            }
            telemetry.addData("Position", goldPositionName);
            telemetry.addData("Since Good Read", System.currentTimeMillis()-lastGoodRead);
            this.telemetry.update();
        }
    }
}
