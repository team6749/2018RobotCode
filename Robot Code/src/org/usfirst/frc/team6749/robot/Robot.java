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
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	GPS gps;
	
	DriveController driveController;
	
	
	XboxController driveJoystick;
	
	int cameraResolutionX = 225;
	int cameraResolutionY = 165;
	int cameraFPS = 20;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		gps = new GPS();
		driveController = new DriveController(gps);
		
		gps.Init();
		
		//Init Joystick
		driveJoystick = new XboxController(0);
		
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
		SmartDashboard.putNumber("AccelX", gps.GetAccelerometer().getX());
		SmartDashboard.putNumber("AccelY", gps.GetAccelerometer().getY());
		SmartDashboard.putNumber("AccelZ", gps.GetAccelerometer().getZ());
		
		double turn = -driveJoystick.getX(Hand.kLeft);
		double speed = driveJoystick.getTriggerAxis(Hand.kLeft) - driveJoystick.getTriggerAxis(Hand.kRight);
		
		
		if(driveJoystick.getRawButton(5) == true) {
			turn = turn * 0.5;
		}
		
		driveController.DriveRelative(speed, turn);
		
		
		gps.Calculate();
	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {

	}
	
}
