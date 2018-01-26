/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6749.robot;

import org.opencv.calib3d.StereoBM;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.*;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	//Sensors
	 ADXRS450_Gyro gyro;
	
	Joystick driveJoystick;
	
	MecanumDrive drive;
	
	int cameraResolutionX = 225;
	int cameraResolutionY = 165;
	int cameraFPS = 20;
	
	SpeedController frontLeft;
	SpeedController frontRight;
	SpeedController backLeft;
	SpeedController backRight;
	
	
	double speedScale = 1f;
	double rotScale = 0.5f;
	
	double currentX;
	double currentY;
	double currentZ;
	
	double forwardThreshold = 0.1f;
	double rotationThreshold = 0.25f;
	double strafeThreshold = 0.25f;
	
	double forwardAcceleration = 0.035f;
	double rotationAcceleration = 0.03f;
	
	double strafeScale = 0.8f;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		
		//Init SpeedControllers
		frontLeft = new Spark(3);
		frontRight = new Spark (0);
		backLeft = new Spark (2);
		backRight = new Spark (1);
		
		//Init mecanum drive train
		drive = new MecanumDrive (frontLeft, backLeft, frontRight, backRight);
		
		//Init Joystick
		driveJoystick = new Joystick(0);
		
		InitCameras();
	}
	
	void InitCameras () {
		
		new Thread(() -> {
            UsbCamera leftCamera = CameraServer.getInstance().startAutomaticCapture(0);
            UsbCamera rightCamera = CameraServer.getInstance().startAutomaticCapture(1);
            leftCamera.setFPS(cameraFPS);
    		rightCamera.setFPS(cameraFPS);
    		leftCamera.setResolution(cameraResolutionX, cameraResolutionY);
    		rightCamera.setResolution(cameraResolutionX, cameraResolutionY);
            
            CvSink leftcvSink = CameraServer.getInstance().getVideo(leftCamera);
            CvSink rightcvSink = CameraServer.getInstance().getVideo(rightCamera);
            
            CvSource processedStream = CameraServer.getInstance().putVideo("ProcessedOutput", cameraResolutionX, cameraResolutionY);
            
            Mat leftCameraMat = new Mat();
            Mat rightCameraMat = new Mat();
            
            Mat processedOutput = new Mat ();
            
            while(!Thread.interrupted()) {
            	rightCamera.setBrightness(leftCamera.getBrightness());
            	
            	leftcvSink.grabFrame(leftCameraMat);
            	rightcvSink.grabFrame(rightCameraMat);

	            
	            if(leftCameraMat.empty() == false && rightCameraMat.empty() == false) {
	            	
	            	
	            	StereoBM debth = StereoBM.create(16, 16);
		            debth.compute(leftCameraMat, rightCameraMat, processedOutput);
		            //Imgproc.cvtColor(leftCameraMat, processedOutput, Imgproc.COLOR_BGR2GRAY);
		            processedStream.putFrame(processedOutput);
	            }
            }
            
            System.out.println("Interrupted.");
            
        }).start(); 
	}

	/**
	 * This function is run once each time the robot enters autonomous mode.
	 */
	@Override
	public void autonomousInit() {
		
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
	}

	/**
	 * This function is called once each time the robot enters teleoperated mode.
	 */
	@Override
	public void teleopInit() {
		
	}

	/**
	 * This function is called periodically during teleoperated mode.
	 */
	@Override
	public void teleopPeriodic() {
		SmartDashboard.putNumber("Gyro", gyro.getAngle());
		ManageDriveTrain();
		
	}

	void ManageDriveTrain () {
		double x = driveJoystick.getY();
		double y = driveJoystick.getX();
		double z = driveJoystick.getZ();
		
		//Check rotation threshold
		if(z > 0 && z < rotationThreshold) {
			currentZ = 0;
		} else if(z < 0 && -z < rotationThreshold) {
			currentZ = 0;
		} else {
			//We are outside of the limits so lets accelerate towards the target
			//currentZ = z;
			if(z > currentZ) {
				//Go positive
				currentZ = (currentZ + rotationAcceleration);
			}
			if(z < currentZ) {
				currentZ = (currentZ - rotationAcceleration);
			}
			//If we are within the amount needed to clip then clip the value
			if(Math.abs(z - currentZ) < rotationAcceleration) {
				currentZ = z;
			}
		}
		
		//Check Strafe threshold
		if(y > 0 && y < strafeThreshold) {
			currentY = 0;
		} else if(y < 0 && -y < strafeThreshold) {
			currentY = 0;
		} else {
			currentY = y * strafeScale;
		}
		
		//Check forward threshold
		if(x > 0 && x < forwardThreshold) {
			currentX = 0;
		} else if(x < 0 && -x < forwardThreshold) {
			currentX = 0;
		} else {
			//We are outside of the limits so lets accelerate towards the target
			//currentZ = z;
			if(x > currentX) {
				//Go positive
				currentX = (currentX + forwardAcceleration);
			}
			if(x < currentX) {
				currentX = (currentX - forwardAcceleration);
			}
			//If we are within the amount needed to clip then clip the value
			if(Math.abs(x - currentX) < forwardAcceleration) {
				currentX = x;
			}
		}
		
		SmartDashboard.putNumber("X", currentX);
		SmartDashboard.putNumber("Strafe", currentY);
		SmartDashboard.putNumber("Rotation", currentZ);
		
		drive.driveCartesian(-currentY * speedScale, -currentX * speedScale, currentZ * rotScale);
	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
	}
	
}
