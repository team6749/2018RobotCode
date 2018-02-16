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
	
	double GetDistanceMetricAbsolute () {
		return myPos.distanceTraveledAbsolute;
	}
	
	void reset () {
		myEncoder.reset();
		myPos.Reset();
	}
	
	public void ProcessLocation (double rotation, GPS gps) {
		double distance = GetDistanceMetric ();
		double delta = distance - lastRecordedValue;
		
		if(Math.abs(delta) < 0.02 && Math.abs(rotation - lastRotation) < 5) {
			//Only do one every x meters or rotation is greater than 5*
			return;
		}
		
		double x = Math.cos(Math.toRadians(rotation)) * delta;
		double y = Math.sin(Math.toRadians(rotation)) * delta;
		
		myPos.AddPosition(x, y);
		myPos.AddDistance(delta);
		
		lastRecordedValue = distance;
		lastRotation = rotation;
	}
	
	public double GetDistanceAbsolute () {
		return myPos.distanceTraveledAbsolute;
	}
	
	double GetX () {
		return myPos.x;
	}
	double GetY () {
		return myPos.y;
	}
}

class EncoderAbsolutePostion {
	public double x, y, distanceTraveled, distanceTraveledAbsolute;
	
	public EncoderAbsolutePostion (double x, double y) {
		this.x = x;
		this.y = y;
		this.distanceTraveled = 0;
		this.distanceTraveledAbsolute = 0;
	}
	
	public void AddPosition (double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	public void AddDistance (double distance) {
		this.distanceTraveled += distance;
		this.distanceTraveledAbsolute += Math.abs(distance);
	}
	
	
	void Reset () {
		x = 0;
		y = 0;
		distanceTraveled = 0;
		distanceTraveledAbsolute = 0;
	}
	
}