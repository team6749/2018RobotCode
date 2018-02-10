package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FancyEncoder {

	public double encoderCalibrationValue = 49.03376264046825;
	double encoderCalibrationMultiplier = 0.975;
	public double encoderStepsPerRotation = 0.002777777777777;
	
	Encoder myEncoder;
	
	EncoderAbsolutePostion myPos;
	
	double lastRecordedValue;
	double lastRotation;
	
	public FancyEncoder (int primaryPWM, int secondaryPWM, boolean invert) {
		myPos = new EncoderAbsolutePostion (0, 0);
		myEncoder = new Encoder(primaryPWM, secondaryPWM, invert);
		myEncoder.setDistancePerPulse(encoderStepsPerRotation);
		myEncoder.setSamplesToAverage(5);
	}
	
	double GetDistanceMetric () {
		return myEncoder.getDistance() * encoderCalibrationValue / 100 * encoderCalibrationMultiplier;
	}
	
	void reset () {
		myEncoder.reset();
	}
	
	void ProcessLocation (double rotation) {
		double distance = GetDistanceMetric ();
		double delta = distance - lastRecordedValue;
		
		if(Math.abs(delta) < 0.01 && Math.abs(rotation - lastRotation) < 5) {
			//Only do one every x meters or rotation is greater than 5*
			return;
		}
		
		double x = Math.cos(Math.toRadians(rotation)) * delta;
		double y = Math.sin(Math.toRadians(rotation)) * delta;
		
		myPos.x += x;
		myPos.y += y;
		
		lastRecordedValue = distance;
		lastRotation = rotation;
	}
	
	double GetX () {
		return myPos.x;
	}
	double GetY () {
		return myPos.y;
	}
	
}

class EncoderAbsolutePostion {
	public double x, y;
	
	public EncoderAbsolutePostion (double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	void Reset () {
		x = 0;
		y = 0;
	}
	
}