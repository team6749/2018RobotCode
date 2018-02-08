package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GPS {
	
	public enum EncoderLocation {Left, Right};

	ADXRS450_Gyro gyro;
	BuiltInAccelerometer accelerometer;
	
	Encoder leftEncoder;
	Encoder rightEncoder;
	
	//Global positions
	double x;
	double y;
	
	double xG;
	double yG;
	
	double rot;
	
	double accelInputX;
	double accelInputY;
	double accelOldX;
	double accelOldY;
	
	void Init () {
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		accelerometer = new BuiltInAccelerometer();
		
		leftEncoder = new Encoder (2, 3);
		rightEncoder = new Encoder (0, 1);
		leftEncoder.setDistancePerPulse(0.0027777);
		leftEncoder.setSamplesToAverage(8);
		rightEncoder.setDistancePerPulse(0.002777);
		rightEncoder.setSamplesToAverage(8);
		ResetEncoders ();
	}
	
	void GetGyroData () {
		rot = Math.round((gyro.getAngle() * 10)) / 10f;
	}
	
	void GetAccelerometerData () {
		accelOldX = (accelerometer.getX()*0.2) + (0.8*accelOldX);
		accelOldY = (accelerometer.getY()*0.2) + (0.8*accelOldY);
	}
	
	void GetEncoderData () {
		SmartDashboard.putNumber("Left Encoder", leftEncoder.getDistance());
		SmartDashboard.putNumber("Right Encoder", rightEncoder.getDistance());
	}
	
	public void Calculate () {
		GetAccelerometerData();
		GetGyroData ();
		GetEncoderData ();
		
		xG += accelOldX;
		yG += accelOldY;
		
		x = xG;
		y = yG;
		
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
	
	public Encoder GetEncoder(EncoderLocation location) {
		if(location == EncoderLocation.Left) {
			return leftEncoder;
		}
		if(location == EncoderLocation.Right) {
			return rightEncoder;
		}
		return null;
	}
	
	void ResetEncoders () {
		leftEncoder.reset();
		rightEncoder.reset();
	}
	
	
	
}
