package org.wheelerschool.robotics;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.bosch.NaiveAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.wheelerschool.robotics.robotlib.motion.MechanumDrive;
import org.wheelerschool.robotics.robotlib.motion.MechanumDrive4x;
import org.wheelerschool.robotics.robotlib.motion.MotorGroup;
import org.wheelerschool.robotics.robotlib.motion.PositionalMotor;
import org.wheelerschool.robotics.robotlib.motion.ServoTwoPos;
import org.wheelerschool.robotics.robotlib.motion.SyncedServo;

public class Hardware {
    public HardwareMap hw;

    // Drive:
    //public MechanumDrive drive;
    public MechanumDrive4x drive;

    // Arm:
    public DcMotor armAngle;
    public PositionalMotor armExt;

    // End-fixture:
    public SyncedServo intakeAngle;
    public MotorGroup intakeDrive;

    // Lift:
    public PositionalMotor lift;

    // Drop:
    public ServoTwoPos drop;

    // Robot IMU:
    public BNO055IMU imu;

    private void MotorConfig(HardwareMap hw) {
        // Drive:
        /*
        drive = new MechanumDrive(
                new DcMotor[] {
                        hw.dcMotor.get("drive++"),  // Front right
                        hw.dcMotor.get("drive-+"),  // Front left
                        hw.dcMotor.get("drive--"),  // Back left
                        hw.dcMotor.get("drive+-"),  // Back right
                },
                new float[][] {
                        {+1, +1, 0, +1},
                        {-1, +1, (float) Math.PI, -1},
                        {-1, -1, (float) Math.PI, +1},
                        {+1, -1, 0, -1},
                }
                );
        */


        DcMotor fl = hw.dcMotor.get("drive-+");  // Front left
        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fl.setDirection(DcMotorSimple.Direction.REVERSE);

        DcMotor bl= hw.dcMotor.get("drive--");  // Back left
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);

        DcMotor fr = hw.dcMotor.get("drive++");
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);

        DcMotor br = hw.dcMotor.get("drive+-");
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setDirection(DcMotorSimple.Direction.FORWARD);

        drive = new MechanumDrive4x(
                fl,  // Front left
                fr,  // Front right
                bl,  // Back left
                br  // Back right
        );


        // Arm:
        armAngle = hw.dcMotor.get("armAngle");
        armExt = new PositionalMotor(hw.dcMotor.get("armExt"), new int[]{0});
        armExt.dcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armExt.dcMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        // Intake:
        //  Angle:
        Servo angleL = hw.servo.get("angleL");
        angleL.setDirection(Servo.Direction.REVERSE);
        Servo angleR = hw.servo.get("angleR");
        intakeAngle = new SyncedServo(new Servo[]{angleL, angleR});

        //  Drive:
        intakeDrive = new MotorGroup();
        CRServo intakeL = hw.crservo.get("intakeL");
        intakeL.setDirection(DcMotorSimple.Direction.REVERSE);
        CRServo intakeR = hw.crservo.get("intakeR");
        intakeDrive.add(intakeL);
        intakeDrive.add(intakeR);

        // Lift:
        lift = new PositionalMotor(hw.dcMotor.get("lift"), new int[]{0, 6290});
        lift.dcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Drop:
        drop = new ServoTwoPos(hw.servo.get("drop"), 0, 180);
    }

    private void SensorConfig(HardwareMap hw) {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hw.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }


    public Hardware(HardwareMap hw) {
        this.hw = hw;
        MotorConfig(hw);
        SensorConfig(hw);
    }
}
