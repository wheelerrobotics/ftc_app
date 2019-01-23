package org.wheelerschool.robotics.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.wheelerschool.robotics.Autonomy.Main;
import org.wheelerschool.robotics.Autonomy.VisionDecoder;
import org.wheelerschool.robotics.Hardware;

@Autonomous
@Disabled
public class DecodeView extends OpMode {
    Main auto;

    public DecodeView() {
        super();
        msStuckDetectInit = 10000;
        msStuckDetectInitLoop = 10000;
    }
    
    @Override
    public void init() {
        Hardware hw = new Hardware(hardwareMap);
        auto = new Main(hw);
    }

    @Override
    public void start() {
        auto.enable();
    }


    @Override
    public void loop() {
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

        if (true) {
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
    }
}
