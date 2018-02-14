package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GPS {

	ADXRS450_Gyro gyro;
	BuiltInAccelerometer accelerometer;
	
	FancyEncoder leftEncoder;
	FancyEncoder rightEncoder;
	
	//Global positions
	RobotPosition robotPosition;
	
	double accelInputX;
	double accelInputY;
	double accelOldX;
	double accelOldY;
	
	public GPS () {
		gyro = new ADXRS450_Gyro();
		accelerometer = new BuiltInAccelerometer();
		robotPosition = new RobotPosition (0, 0, 0);
		leftEncoder = new FancyEncoder (2, 3, true);
		rightEncoder = new FancyEncoder (0, 1, false);
		gyro.calibrate();
		Reset(0, 0, 0);
	}
	
	void GetGyroData () {
		robotPosition.rotation = gyro.getAngle();
	}
	
	void GetAccelerometerData () {
		accelOldX = (accelerometer.getX()*0.2) + (0.8*accelOldX);
		accelOldY = (accelerometer.getY()*0.2) + (0.8*accelOldY);
		
		SmartDashboard.putNumber("Accelerometer X", accelOldX);
		SmartDashboard.putNumber("Accelerometer Y", accelOldY);
	}
	
	void GetEncoderData () {
		SmartDashboard.putNumber("Left Encoder", leftEncoder.GetDistanceMetric());
		SmartDashboard.putNumber("Right Encoder", rightEncoder.GetDistanceMetric());
		
		leftEncoder.ProcessLocation(robotPosition.rotation);
		rightEncoder.ProcessLocation(robotPosition.rotation);
		
		SmartDashboard.putNumber("Left Encoder X", leftEncoder.GetX());
		SmartDashboard.putNumber("Left Encoder Y", leftEncoder.GetY());
		SmartDashboard.putNumber("Right Encoder X", rightEncoder.GetX());
		SmartDashboard.putNumber("Right Encoder Y", rightEncoder.GetY());
	}
	
	public void Calculate () {
		GetAccelerometerData();
		GetGyroData ();
		GetEncoderData ();
		
		robotPosition.x = (leftEncoder.GetX() + rightEncoder.GetX()) / 2;
		robotPosition.y = (leftEncoder.GetY() + rightEncoder.GetY()) / 2; 
		robotPosition.distance = (leftEncoder.GetDistanceMetric() + rightEncoder.GetDistanceMetric()) / 2;
		
		SmartDashboard.putNumber("GPS X", robotPosition.x);
		SmartDashboard.putNumber("GPS Y", robotPosition.y);
		SmartDashboard.putNumber("GPS Rot", robotPosition.rotation);
		SmartDashboard.putNumber("GPS Dist", robotPosition.distance);
	}
	
	public ADXRS450_Gyro GetGyro () {
		return gyro;
	}
	
	public BuiltInAccelerometer GetAccelerometer () {
		return accelerometer;
	}
	
	public double GetAccelMagnatude () {
		double avg = (accelOldX + accelOldY) / 2d;
		return Math.abs(avg);
	}
	
	void Reset (double x, double y, double rot) {
		//resets and recalibrates the robot
		leftEncoder.reset();
		rightEncoder.reset();
		gyro.reset();
		robotPosition.Reset();
		robotPosition.x = x;
		robotPosition.y = y;
		robotPosition.rotation = rot;
	}
}

class RobotPosition {
	public double x, y, rotation;
	public double distance;
	
	public RobotPosition (double x, double y, double rotation) {
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.distance = 0;
	}
	
	public void AddPosition (double x, double y, double rotation) {
		this.x += x;
		this.y += y;
		this.rotation += rotation;
	}
	
	public double GetRotationCyclic () {
		return rotation % 360;
	}
	
	void Reset () {
		x = 0;
		y = 0;
		rotation = 0;
		distance = 0;
	}
	
	public Position ToPosition () {
		return new Position(x, y);
	}
}