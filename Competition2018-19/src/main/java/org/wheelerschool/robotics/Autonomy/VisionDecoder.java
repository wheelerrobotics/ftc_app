package org.wheelerschool.robotics.Autonomy;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

import static org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus.*;

/**
 * Decode vision input for RoverRuckus game play.
 * Wrapper for TensorFlow lite built-in libraries.
 */
public class VisionDecoder {
    final int FRAME_WIDTH = 625; // Approximate from testing

    /**
     * Position of gold mineral.
     */
    public enum Position {
        LEFT,
        CENTER,
        RIGHT,
        NONE,
        UNKNOWN
    }

    public enum Mineral {
        GOLD,
        SILVER,
        NONE
    }

    private TFObjectDetector tfod;
    private OpMode opMode;

    /**
     * Constructor
     * @param locator: Existing VisionLocator object
     */
    public VisionDecoder(HardwareMap hw, VisionLocator locator, OpMode opMode) {
        // USE: locator.vuforia
        int tfodMonitorViewId = hw.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hw.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, locator.vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);

        this.opMode = opMode;
    }

    /**
     * Activate tracking
     */
    public void enable() {
        tfod.activate();
    }

    /**
     * Run TF object detection frame, with order of three minerals in frame
     *
     * @return Position: Position of gold mineral.
     */
    public Position frameCompare3() {
        // Read recognized objects:
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

        // Ensure only 3 objects were detected:
        if (updatedRecognitions != null && updatedRecognitions.size() == 3) {

            // Default positions of minerals:
            int goldMineralX = -1;
            int silverMineral1X = -1;
            int silverMineral2X = -1;

            // Run through objects, and handle if gold or silver (#1 and #2):
            for (Recognition recognition : updatedRecognitions) {
                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                    goldMineralX = (int) recognition.getLeft();
                } else if (silverMineral1X == -1) {
                    silverMineral1X = (int) recognition.getLeft();
                } else {
                    silverMineral2X = (int) recognition.getLeft();
                }
            }

            // Test that all mineral positions were set:
            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                // Handle/return cases with X value ordering:
                if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                    return Position.LEFT;
                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                    return Position.RIGHT;
                } else {
                    return Position.CENTER;
                }
            }
        }

        return Position.UNKNOWN;  // Default, if did not complete all prior steps
    }

    public Mineral frameDetectedMineral() {
        // Read recognized objects:
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

        // Ensure only 3 objects were detected:
        if (updatedRecognitions != null && updatedRecognitions.size()>0) {
            Recognition bestReco = null;  // Best recognition
            float bestConf = 0;  // Best recognition confidence

            for (Recognition r : updatedRecognitions) {
                float c = r.getConfidence();
                if (c > bestConf) {
                    bestReco = r;
                    bestConf = c;
                }
            }

            if (bestReco.getLabel().equals(LABEL_GOLD_MINERAL)) {
                return Mineral.GOLD;
            } else if (bestReco.getLabel().equals(LABEL_SILVER_MINERAL)) {
                return Mineral.SILVER;
            }
        }
        return Mineral.NONE;
    }

    public Mineral frameDetectsGold() {
        // Read recognized objects:
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

        float CONF_THRESH = 0.3f;
        // Ensure only 3 objects were detected:
        if (updatedRecognitions != null && updatedRecognitions.size()>0) {
            for (Recognition r : updatedRecognitions) {
                float c = r.getConfidence();
                if (r.getLabel().equals(LABEL_GOLD_MINERAL) && c > CONF_THRESH) {
                    return Mineral.GOLD;
                }
            }
        }
        return Mineral.NONE;
    }

    public Position frameDetectedDualMineral() {
        return frameDetectedDualMineral(true);
    }
    public Position frameDetectedDualMineral(boolean hanging) {
        // Read recognized objects:
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

        float CONF_THRESH = 0.3f;

        // Filter objects below threshold:
        if (updatedRecognitions != null) {
            opMode.telemetry.addData("Recogs", updatedRecognitions.size());
            for (int idx = updatedRecognitions.size() - 1; idx >= 0; idx--) {
                Recognition r = updatedRecognitions.get(idx);
                if (r.getConfidence() < CONF_THRESH) {
                    updatedRecognitions.remove(idx);
                } else if (hanging) {  // Conditions specific to hanging situation
                    if (r.getBottom() < 260) { // This is roughly half line the line of the crater, to remove/ignore all objects behind it
                        updatedRecognitions.remove(idx);
                    }
                }
            }

            opMode.telemetry.addData("Filtered", updatedRecognitions.size());

            // Ensure only 2 objects were detected:
            Recognition left = null;
            Recognition right = null;
            if (updatedRecognitions != null && updatedRecognitions.size() == 2) {
                for (Recognition r : updatedRecognitions) {
                    if (left == null) {
                        left = r;
                    } else {
                        if (left.getLeft() > r.getLeft()) {
                            right = left;
                            left = r;
                        } else {
                            right = r;
                        }
                    }
                }

                if (left.getLabel().equals(LABEL_GOLD_MINERAL)) {
                    opMode.telemetry.addData("detected", "LEFT");
                    return Position.LEFT;
                } else if (right.getLabel().equals(LABEL_GOLD_MINERAL)) {
                    opMode.telemetry.addData("detected", "RIGHT");
                    return Position.RIGHT;
                } else {
                    opMode.telemetry.addData("detected", "NEITHER");
                    return Position.NONE;
                }
            }
        }

        opMode.telemetry.addData("detected", "ERR");
        return Position.UNKNOWN;
    }

    public Position frameDetectedLowestDualMineral() {
        // Read recognized objects:
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

        float CONF_THRESH = 0.3f;

        // Filter objects below threshold:
        if (updatedRecognitions != null) {
            opMode.telemetry.addData("Recogs", updatedRecognitions.size());

            for (int idx = updatedRecognitions.size() - 1; idx >= 0; idx--) {
                Recognition r = updatedRecognitions.get(idx);
                if (r.getConfidence() < CONF_THRESH) {
                    updatedRecognitions.remove(idx);
                }
            }

            opMode.telemetry.addData("Filtered", updatedRecognitions.size());

            if (updatedRecognitions.size()>=2) {
                Recognition[] allLowestRecogs = new Recognition[2];

                for (int lowestSaveIdx = 0; lowestSaveIdx < allLowestRecogs.length; lowestSaveIdx++) {
                    int lowestIdx = 0;
                    allLowestRecogs[lowestSaveIdx] = updatedRecognitions.get(lowestIdx);
                    for (int idx = 1; idx < updatedRecognitions.size(); idx++) {  //
                        // Save recognitions for easy use:
                        Recognition newR = updatedRecognitions.get(idx);
                        Recognition lowestR = allLowestRecogs[lowestSaveIdx];

                        if (lowestR == null || newR.getBottom() > lowestR.getBottom()) {  // Save if lower than last (GREATER Y VALUE)
                            allLowestRecogs[lowestSaveIdx] = updatedRecognitions.get(idx);
                            lowestIdx = idx;
                        }
                    }
                    updatedRecognitions.remove(lowestIdx);
                }


                // Ensure only 2 objects were detected:
                Recognition left = null;
                Recognition right = null;
                for (Recognition r : allLowestRecogs) {
                    if (left == null) {
                        left = r;
                    } else {
                        if (left.getLeft() > r.getLeft()) {
                            right = left;
                            left = r;
                        } else {
                            right = r;
                        }
                    }
                }

                if (left.getLabel().equals(LABEL_GOLD_MINERAL)) {
                    opMode.telemetry.addData("detected", "LEFT");
                    return Position.LEFT;
                } else if (right.getLabel().equals(LABEL_GOLD_MINERAL)) {
                    opMode.telemetry.addData("detected", "RIGHT");
                    return Position.RIGHT;
                } else {
                    opMode.telemetry.addData("detected", "NEITHER");
                    return Position.NONE;
                }
            }
        }

        opMode.telemetry.addData("detected", "ERR");
        return Position.UNKNOWN;
    }

    /**
     * Deactivate tracking
     */
    public void disable() {
        tfod.shutdown();
    }
}
