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
	double x;
	double y;
	
	double rot;
	
	double accelInputX;
	double accelInputY;
	double accelOldX;
	double accelOldY;
	
	void Init () {
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		accelerometer = new BuiltInAccelerometer();
		
		leftEncoder = new FancyEncoder (2, 3, true);
		rightEncoder = new FancyEncoder (0, 1, false);
		
		ResetSensors();
	}
	
	void GetGyroData () {
		rot = gyro.getAngle();
	}
	
	void GetAccelerometerData () {
		accelOldX = (accelerometer.getX()*0.2) + (0.8*accelOldX);
		accelOldY = (accelerometer.getY()*0.2) + (0.8*accelOldY);
	}
	
	void GetEncoderData () {
		SmartDashboard.putNumber("Left Encoder", leftEncoder.GetDistanceMetric());
		SmartDashboard.putNumber("Right Encoder", rightEncoder.GetDistanceMetric());
		
		leftEncoder.ProcessLocation(rot);
		rightEncoder.ProcessLocation(rot);
		
		SmartDashboard.putNumber("Left Encoder X", leftEncoder.GetX());
		SmartDashboard.putNumber("Left Encoder Y", leftEncoder.GetY());
		SmartDashboard.putNumber("Right Encoder X", rightEncoder.GetX());
		SmartDashboard.putNumber("Right Encoder Y", rightEncoder.GetY());
	}
	
	public void Calculate () {
		GetAccelerometerData();
		GetGyroData ();
		GetEncoderData ();
		
		x = (leftEncoder.GetX() + rightEncoder.GetX()) / 2;
		y = (leftEncoder.GetY() + rightEncoder.GetY()) / 2;
		
		SmartDashboard.putNumber("GPS X", x);
		SmartDashboard.putNumber("GPS Y", y);
		SmartDashboard.putNumber("GPS Rot", rot);
	}
	
	public ADXRS450_Gyro GetGyro () {
		return gyro;
	}
	
	public BuiltInAccelerometer GetAccelerometer () {
		return accelerometer;
	}
	
	void Reset () {
		//resets and recalibrates the robot
		ResetSensors ();
	}
	
	void ResetSensors () {
		leftEncoder.reset();
		rightEncoder.reset();
		gyro.calibrate();
		gyro.reset();
	}
	
	
	
}
