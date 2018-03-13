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
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
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
	Auto auto;
	
	AutoRecord autoRecord;
	AutoReplay autoReplay;
	
	XboxController driveJoystick;
	Joystick controllerJoystick;
	
	int cameraResolutionX = 240;
	int cameraResolutionY = 135;
	int cameraFPS = 20;
	
	SendableChooser<Integer> recordingOptions;
	SendableChooser<Integer> autoSelection;
	SendableChooser<Integer> myTeam;
	
	Spark ratchet;
	Solenoid elevatorArms;
	Solenoid elevatorArmsUp;  //MEO
	Solenoid elevatorArmsDn;  //MEO
	Solenoid grabber;
	Solenoid grabberIn;  // MEO
	Solenoid grabberOut;  // MEO
	Compressor compressor;
	
	Talon pusherMotorLeft;
	Talon pusherMotorRight;
	
	boolean eleArmState;
	boolean grabArmState;
	
	@Override
	public void robotInit() {
		
		gps = new GPS();
		driveController = new DriveController(gps);
		auto = new Auto(driveController, gps);
		autoRecord = new AutoRecord();
		autoReplay = new AutoReplay();
		
		
		//Init Joystick
		driveJoystick = new XboxController(0);
		controllerJoystick = new Joystick(0);
		
		autoSelection = new SendableChooser<Integer>();
		autoSelection.addDefault("0, 0, 0", 0);
		
		autoSelection.addObject("Replay Test", 1);
		autoSelection.addObject("Replay Left", 2);
		autoSelection.addObject("Replay Middle", 3);
		autoSelection.addObject("Replay Right", 4);
		
		SmartDashboard.putData("Auto Selection", autoSelection);

		recordingOptions = new SendableChooser<Integer>();
		recordingOptions.addDefault("Record Test", 0);
		recordingOptions.addObject("Record Left", 1);
		recordingOptions.addObject("Record Middle", 2);
		recordingOptions.addObject("Record Right", 3);
		SmartDashboard.putData("Recording Selection", recordingOptions);
		
		myTeam = new SendableChooser<Integer>();
		myTeam.addDefault("Red", 0);
		myTeam.addDefault("Blue", 1);
		SmartDashboard.putData("Team Selection", myTeam);
		
		ratchet = new Spark(4);
//		elevatorArmsDn = new Solenoid(0);  //MEO
		elevatorArms = new DoubleSolenoid(0, 1); //MEO
//		elevatorArms = new Solenoid(1); MEO
//		grabber = new Solenoid(2);  MEO
		grabber = new DoubleSolenoid(2, 3);  //MEO
//		grabberIn = new Solenoid(2);  //MEO
//		grabberOut = new Solenoid(3);  //MEO
		compressor = new Compressor(0);
		pusherMotorLeft = new Talon(5);
		pusherMotorRight = new Talon(6);
		
		//Enable the compressor
		compressor.start();
		
		InitCameras();
	}
	
	void InitCameras () {
		
		new Thread(() -> {
			UsbCamera front = CameraServer.getInstance().startAutomaticCapture(1);
			front.setFPS(cameraFPS);
			front.setBrightness(40);
			front.setResolution(cameraResolutionX, cameraResolutionY);
			
			UsbCamera top = CameraServer.getInstance().startAutomaticCapture(0);
			top.setFPS(cameraFPS);
			top.setBrightness(40);
			top.setResolution(cameraResolutionX, cameraResolutionY);
		}).start();
		
		/*
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
        */
	}

	/**
	 * This function is run once each time the robot enters autonomous mode.
	 */
	@Override
	public void autonomousInit() {
		//Reset the gps
		int team = (int) myTeam.getSelected();
		int autoMode = (int) autoSelection.getSelected();
		
		if(autoMode == 0) {
			gps.Reset(0, 0, 0);
			//Test Test mode
		}
		if(autoMode == 1) {
			//Test Mode
			gps.Reset(0, 0, 0);
			//We are going to Replay
			autoReplay.StartReplay(Preferences.getInstance().getString("test_recording", ""));
		}
		if(autoMode == 2) {
			//Left
			gps.Reset(1.676, RobotData.height, 0);
			autoReplay.StartReplay(Preferences.getInstance().getString("left_recording", ""));
		}
		if(autoMode == 3) {
			//Middle
			gps.Reset(4.7752, RobotData.height, 0);
			autoReplay.StartReplay(Preferences.getInstance().getString("middle_recording", ""));
		}
		if(autoMode == 4) {
			gps.Reset(6.5786, RobotData.height, 0);
			autoReplay.StartReplay(Preferences.getInstance().getString("right_recording", ""));
			//Right
		}
	}
	
	
	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		if(autoReplay.isReplaying) {
			if(autoReplay.ReplayPeriodic() != null) {
				DoMovement(autoReplay.ReplayPeriodic());
			}
		}
		
		
		gps.Calculate();
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
		DoMovement(GetUserInput ());
		
		gps.Calculate();
	}
	
	double[] GetUserInput () {
		double[] out = new double[5];
		
		double turn = -driveJoystick.getX(Hand.kLeft);
		double speed = driveJoystick.getTriggerAxis(Hand.kLeft) - driveJoystick.getTriggerAxis(Hand.kRight);
		
		if(Math.abs(turn) < 0.07) {
			//We have not rotated a lot so dont set the rotation
			turn = 0;
		}
		
		if(driveJoystick.getRawButton(5) == true) {
			turn = turn * 0.5;
		}
		
		
		if(controllerJoystick.getRawButtonPressed(2) == true) {
			//use the elevator with the Y button
			eleArmState = !eleArmState;
		}
		if(controllerJoystick.getRawButtonPressed(1) == true) {
			//use the grab arm with the X button
			grabArmState = !grabArmState;
		}
		
		double pusherArms = 0;
		if(driveJoystick.getRawButton(4)) {
			//Pull in with the a button
			pusherArms = -1;
		}
		if(driveJoystick.getRawButton(3)) {
			//Push out with the b button
			pusherArms = 1;
		}
		
		out[0] = speed;
		out[1] = turn;
		out[2] = pusherArms;
		out[3] = (eleArmState) ? 1 : 0;
		out[4] = (grabArmState) ? 1 : 0;
		
		return out;
	}
	
	void DoMovement (double[] inputs) {
		driveController.DriveRelative(inputs[0], inputs[1]);
		
		
		boolean elevatorState = false;
		if(inputs[3] == 1) {
			elevatorState = true;
		} else {
			elevatorState = false;
		}
		
		boolean armState = false;
		if(inputs[4] == 1) {
			armState = true;
		} else {
			armState = false;
		}
		
		grabber.set(armState);
		grabberIn.set(armState);  //MEO
		grabberOut.set(!armState);  //MEO
		elevatorArms.set(elevatorState);
		elevatorArmsUp.set(elevatorState);  //MEO
		elevatorArmsDn.set(!elevatorState);  //MEO
		
		SmartDashboard.putNumber("Input 0", inputs[0]);
		SmartDashboard.putNumber("Input 1", inputs[1]);
		SmartDashboard.putNumber("Input 2", inputs[2]);
		SmartDashboard.putNumber("Input 3", inputs[3]);
		SmartDashboard.putNumber("Input 4", inputs[4]);
		
		pusherMotorLeft.set(-inputs[2]);
		pusherMotorRight.set(inputs[2]);
	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		DoMovement(GetUserInput ());
		
		if(autoRecord.recording) {
			autoRecord.RecordingPeriodic(GetUserInput ());
			
			if(driveJoystick.getRawButton(2) == true) {
				autoRecord.StopAndStoreRecording();
				SmartDashboard.putBoolean("RecordingEnded", true);
			}
		}
	}
	
	@Override
	public void testInit() {
		int recordMode = (int) recordingOptions.getSelected();
		
		switch (recordMode) {
		case 0:
			autoRecord.StartRecording("test_recording");
			break;
		case 1:
			autoRecord.StartRecording("left_recording");
			break;
		case 2:
			autoRecord.StartRecording("middle_recording");
			break;
		case 3:
			autoRecord.StartRecording("right_recording");
			break;
		}
	}
	
}
