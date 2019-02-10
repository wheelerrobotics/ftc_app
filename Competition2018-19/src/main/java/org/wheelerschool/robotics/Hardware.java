package org.wheelerschool.robotics;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.bosch.NaiveAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
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
    // Hardware characteristics:
    public double INTAKE_UP_POWER = 0.75f;
    public double INTAKE_DOWN_POWER = 0.4f;

    public HardwareMap hw;

    // Drive:
    //public MechanumDrive drive;
    public MechanumDrive4x drive;

    // Arm:
    public PositionalMotor armAngle;
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

    private DcMotor setupMotor(DcMotor m, DcMotorSimple.Direction d) {
        m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m.setDirection(d);
        m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        return m;
    }
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

        drive = new MechanumDrive4x(
                setupMotor(hw.dcMotor.get("drive-+"), DcMotorSimple.Direction.REVERSE),  // Front left
                setupMotor(hw.dcMotor.get("drive++"), DcMotorSimple.Direction.FORWARD),  // Front right
                setupMotor(hw.dcMotor.get("drive--"), DcMotorSimple.Direction.REVERSE),  // Back left
                setupMotor(hw.dcMotor.get("drive+-"), DcMotorSimple.Direction.FORWARD)  // Back right
        );


        // Arm:
        armAngle = new PositionalMotor(hw.dcMotor.get("armAngle"), new int[]{-150, 980, 1576});
        armAngle.dcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armAngle.dcMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        armExt = new PositionalMotor(hw.dcMotor.get("armExt"), new int[]{0});
        armExt.dcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armExt.dcMotor.setDirection(DcMotorSimple.Direction.REVERSE);

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
        lift = new PositionalMotor(hw.dcMotor.get("lift"), new int[]{0, 6125});
        lift.dcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Drop:
        drop = new ServoTwoPos(hw.servo.get("drop"), 0, 0.5);
        drop.s.setDirection(Servo.Direction.REVERSE);

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
