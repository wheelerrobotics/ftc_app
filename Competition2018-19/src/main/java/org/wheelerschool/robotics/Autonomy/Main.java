package org.wheelerschool.robotics.Autonomy;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.wheelerschool.robotics.Hardware;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

public class Main {
    float ANGLE_DB = 1;  // Angle deadband (deg)
    float ANGLE_MAX = 30;  // Angle separation for which max rotation is applied (deg)
    AxesOrder IMU_ORDER = AxesOrder.XYZ;

    Hardware r;

    VisionLocator locator;


    /* --- Setup Functions --- */
    /** Create vision objects */
    private void setupVision() {
        final int CAMERA_FORWARD_DISPLACEMENT  = 110;   // eg: Camera is 110 mm in front of robot center
        final int CAMERA_VERTICAL_DISPLACEMENT = 200;   // eg: Camera is 200 mm above ground
        final int CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line
        OpenGLMatrix cameraOffset = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES,
                        90, 0, 0));

        this.locator = new VisionLocator(this.r.hw, cameraOffset);
    }


    /* --- Public functions --- */
    /** Constructor */
    public Main(Hardware robot) {
        this.r = robot;

        setupVision();
    }

    /** Enable autonomy systems */
    public void enable() {
        this.locator.enable();
    }
}
