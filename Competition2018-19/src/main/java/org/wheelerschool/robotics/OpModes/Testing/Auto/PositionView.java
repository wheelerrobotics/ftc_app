package org.wheelerschool.robotics.OpModes.Testing.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.wheelerschool.robotics.Autonomy.Main;
import org.wheelerschool.robotics.Hardware;

import java.util.Arrays;

@Autonomous
@Disabled
public class PositionView extends LinearOpMode {
    Main auto;

    public PositionView() {
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
            auto.locator.readData();
            OpenGLMatrix loc = auto.locator.lastLocation;
            if (loc != null) {
                telemetry.addData("Position", Arrays.toString(loc.getTranslation().getData()));
            } else {
                //telemetry.addData("Position", "None");
            }
        }
    }
}
