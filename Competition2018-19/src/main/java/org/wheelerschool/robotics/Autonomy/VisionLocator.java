/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
Edits by The Wheeler School FTC Robotics Team
 */
package org.wheelerschool.robotics.Autonomy;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.wheelerschool.robotics.robotlib.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

/**
 * =================================================================================================
 * NOTE: Make sure to set 'VUFORIA_KEY' in 'org.wheelerschool.robotics.robotlib.config.Config' to your Vuforia key
 * =================================================================================================
 */

/**
 * Library to find the robot locations on the field using "VuforiaLocalizer"
 *
 * @see org.firstinspires.ftc.robotcontroller.external.samples.ConceptVuforiaNavigation
 * @see VuforiaLocalizer
 * @see VuforiaTrackableDefaultListener
 * see  ftc_app/doc/tutorial/FTC_FieldCoordinateSystemDefinition.pdf
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 *
 * @author luciengaitskell
 * @version 1.0
 */


public class VisionLocator {
    // DEFAULT VARIABLES:
     static float MM_PER_INCH = 25.4f;
    private static float MM_FTC_FIELD_WIDTH = (12*6) * MM_PER_INCH;
    private static float MM_TARGET_HEIGHT = (6) * MM_PER_INCH;

    // Variables:
    public static final String TAG = "Vuforia Location";

    //  Vuforia:
    WebcamName webcamName;
    VuforiaLocalizer vuforia;
    VuforiaTrackables visionDataset = null;
    Map<String, VuforiaTrackable> trackables = null;


    public OpenGLMatrix lastLocation = null;
    public VectorF lastLocationXYZ = null;
    public Orientation lastRotationXYZ = null;
    public Map<String, Boolean> lastTrackableData = null;

    public VisionLocator(HardwareMap hw, OpenGLMatrix cameraOffset) {
        /**
         * Create instance of VisionLocator.
         */

        // Vuforia Setup:
        int cameraMonitorViewId = hw.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hw.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        webcamName = hw.get(WebcamName.class, "Webcam 1");
        parameters.vuforiaLicenseKey = Config.VUFORIA_KEY;
        parameters.cameraName = webcamName;
        //parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Load trackables:
        this.visionDataset = this.vuforia.loadTrackablesFromAsset("RoverRuckus");

        // List of targets with names:
        List<String> targets = new ArrayList<>(); // Wheels Legos Tools Gears
        targets.add("Blue-Rover");
        targets.add("Red-Footprint");
        targets.add("Front-Craters");
        targets.add("Back-Space");


        // Iterate through trackables and set names and other data:
        this.trackables = new HashMap<>();
        for (int ii=0; ii < targets.size(); ii++) {
            String name = targets.get(ii);
            VuforiaTrackable trackableObject = this.setUpTrackable(ii, name);
            trackableObject.setLocation(getTargetLocation(name));

            this.trackables.put(name, trackableObject);
            ((VuforiaTrackableDefaultListener) trackableObject.getListener())
                    .setCameraLocationOnRobot(parameters.cameraName, cameraOffset);
        }
    }

    /**
     * Start tracking vision targets
     */
    public void enable() {
        // Start tracking the data sets we care about
        this.visionDataset.activate();
    }

    public void readData() {
        /**
         * Read the robot location data and set the object variables with the read data.
         */
        OpenGLMatrix lastLocation = null;
        OpenGLMatrix robotLocationTransform = null;
        Map<String, Boolean> trackableData = new HashMap<>();

        for (Map.Entry<String, VuforiaTrackable> trackableEntry : this.trackables.entrySet()) {
            VuforiaTrackable trackable = trackableEntry.getValue();

            trackableData.put(trackable.getName(), ((VuforiaTrackableDefaultListener) trackable
                                                    .getListener()).isVisible());

            robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable
                                                   .getListener()).getUpdatedRobotLocation();
            if (robotLocationTransform != null) {
                lastLocation = robotLocationTransform;
            }
        }

        this.lastTrackableData = trackableData;
        this.lastLocation = lastLocation;

        this.lastLocationXYZ = (lastLocation == null ? null : lastLocation.getTranslation());
        this.lastRotationXYZ = (lastLocation == null ? null : Orientation.getOrientation(lastLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS));
    }

    /**
     * Get a trackable by name and set the name
     */
    private VuforiaTrackable setUpTrackable(int trackableNumb, String trackableName){
        VuforiaTrackable trackable = this.visionDataset.get(trackableNumb);
        trackable.setName(trackableName);
        return trackable;
    }


    private OpenGLMatrix getTargetLocation(String targetName) {
        /**
         * Get the target location based on the name.
         */

        // Set default target location value:
        OpenGLMatrix target = null;

        if (targetName.equals("Blue-Rover")) {
            /**
             * To place the BlueRover target in the middle of the blue perimeter wall:
             * - First we rotate it 90 around the field's X axis to flip it upright.
             * - Then, we translate it along the Y axis to the blue perimeter wall.
             */
            target = OpenGLMatrix
                    .translation(0, MM_FTC_FIELD_WIDTH, MM_TARGET_HEIGHT)
                    .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES,
                            90, 0, 0));
        }

        else if (targetName.equals("Red-Footprint")) {
            /**
             * To place the RedFootprint target in the middle of the red perimeter wall:
             * - First we rotate it 90 around the field's X axis to flip it upright.
             * - Second, we rotate it 180 around the field's Z axis so the image is flat against the red perimeter wall
             *   and facing inwards to the center of the field.
             * - Then, we translate it along the negative Y axis to the red perimeter wall.
             */
            target = OpenGLMatrix
                    .translation(0, -MM_FTC_FIELD_WIDTH, MM_TARGET_HEIGHT)
                    .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES,
                            90, 0, 180));
        }

        else if (targetName.equals("Front-Craters")) {
            /**
             * To place the FrontCraters target in the middle of the front perimeter wall:
             * - First we rotate it 90 around the field's X axis to flip it upright.
             * - Second, we rotate it 90 around the field's Z axis so the image is flat against the front wall
             *   and facing inwards to the center of the field.
             * - Then, we translate it along the negative X axis to the front perimeter wall.
             */
            target = OpenGLMatrix
                    .translation(-MM_FTC_FIELD_WIDTH, 0, MM_TARGET_HEIGHT)
                    .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES,
                            90, 0 , 90));
        }

        else if (targetName.equals("Back-Space")) {
            /**
             * To place the BackSpace target in the middle of the back perimeter wall:
             * - First we rotate it 90 around the field's X axis to flip it upright.
             * - Second, we rotate it -90 around the field's Z axis so the image is flat against the back wall
             *   and facing inwards to the center of the field.
             * - Then, we translate it along the X axis to the back perimeter wall.
             */
            target = OpenGLMatrix
                    .translation(MM_FTC_FIELD_WIDTH, 0, MM_TARGET_HEIGHT)
                    .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90));
        }

        return target;
    }

    /**
     * A simple utility that extracts positioning information from a transformation matrix
     * and formats it in a form palatable to a human being.
     */
    String format(OpenGLMatrix transformationMatrix) {
        return transformationMatrix.formatAsTransform();
    }
}
