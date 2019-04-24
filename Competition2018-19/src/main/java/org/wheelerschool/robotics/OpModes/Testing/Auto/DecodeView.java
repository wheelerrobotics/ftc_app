package org.wheelerschool.robotics.OpModes.Testing.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.wheelerschool.robotics.Autonomy.Main;
import org.wheelerschool.robotics.Autonomy.VisionDecoder;
import org.wheelerschool.robotics.Hardware;

@Autonomous
public class DecodeView extends LinearOpMode {
    Main auto;

    public DecodeView() {
        super();
        msStuckDetectInit = 10000;
        msStuckDetectInitLoop = 10000;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hw = new Hardware(hardwareMap);
        auto = new Main(hw, this);

        waitForStart();

        auto.enable();

        while (opModeIsActive()) {
            if (false) {
                VisionDecoder.Position gp = auto.decoder.frameCompare3();

                switch (gp) {
                    case LEFT:
                        telemetry.addData("pos", "left");
                        break;
                    case RIGHT:
                        telemetry.addData("pos", "right");
                        break;
                    case CENTER:
                        telemetry.addData("pos", "center");
                        break;
                }
            }

            if (false) {
                VisionDecoder.Mineral m = auto.decoder.frameDetectedMineral();

                switch (m) {
                    case GOLD:
                        telemetry.addData("min", "gold");
                        break;
                    case SILVER:
                        telemetry.addData("min", "silver");
                        break;
                }
            }

            if (true) {
                VisionDecoder.Position gp = auto.decoder.frameDetectedDualMineral(true);

                switch (gp) {
                    case LEFT:
                        telemetry.addData("pos", "left");
                        break;
                    case RIGHT:
                        telemetry.addData("pos", "middle");
                        break;
                    case NONE:
                        telemetry.addData("pos", "not seen (right)");
                        break;
                    case UNKNOWN:
                        //telemetry.addData("pos", "err");
                        break;
                }
                telemetry.update();
            }
        }
    }
}
