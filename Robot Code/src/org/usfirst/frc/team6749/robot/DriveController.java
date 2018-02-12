package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveController {

	DriveCompensation dc;
	GPS gps;
	
	public boolean enableRotationCompensation = true;
	
	double speedScale = 1f;
	double rotScale = 0.6f;
	
	double currentForward;
	double currentY;
	double currentRot;
	
	double forwardThreshold = 0.05f;
	double rotationThreshold = 0.1f;
	
	double forwardAcceleration = 0.06f;
	double rotationAcceleration = 0.03f;
	
	SpeedController rightFront;
	SpeedController rightBack;
	SpeedController leftFront;
	SpeedController leftBack;
	

	DriveController (GPS gps) {
		this.gps = gps;
		dc = new DriveCompensation ();
		//Init SpeedControllers
		rightFront = new Spark(0);
		rightBack = new Spark (1);
		leftFront = new Spark (2);
		leftBack = new Spark (3);
	}
	
	void DriveRelative (double forwardInput, double rotationInput) {
		
		//Check rotation threshold
		if(rotationInput > 0 && rotationInput < rotationThreshold) {
			currentRot = 0;
		} else if(rotationInput < 0 && -rotationInput < rotationThreshold) {
			currentRot = 0;
		} else {
			//We are outside of the limits so lets accelerate towards the target
			if(rotationInput > currentRot) {
				//Go positive
				currentRot = (currentRot + rotationAcceleration);
			}
			if(rotationInput < currentRot) {
				currentRot = (currentRot - rotationAcceleration);
			}
			//If we are within the amount needed to clip then clip the value
			if(Math.abs(rotationInput - currentRot) < rotationAcceleration) {
				currentRot = rotationInput;
			} 
		}
		
		//Check forward threshold
		if(forwardInput > 0 && forwardInput < forwardThreshold) {
			currentForward = 0;
		} else if(forwardInput < 0 && -forwardInput < forwardThreshold) {
			currentForward = 0;
		} else {
			//We are outside of the limits so lets accelerate towards the target
			//currentRot = z;
			if(forwardInput > currentForward) {
				//Go positive
				currentForward = (currentForward + forwardAcceleration);
			}
			if(forwardInput < currentForward) {
				currentForward = (currentForward - forwardAcceleration);
			}
			//If we are within the amount needed to clip then clip the value
			if(Math.abs(forwardInput - currentForward) < forwardAcceleration) {
				currentForward = forwardInput;
			}
		}
		
		StandardDrive(-currentForward * speedScale, -currentRot * rotScale);
	}
	
	void StandardDrive (double speed, double rotation) {
		speed = Helpers.ClampN11(speed);
		rotation  = Helpers.ClampN11(rotation);
		
		double rightOutput = 0;
		double leftOutput = 0;
		
		SmartDashboard.putNumber("speedlol", speed);
		SmartDashboard.putNumber("rotationlol", rotation);
		
		//Check my prefrences to see if I sould even compensate
		enableRotationCompensation = Preferences.getInstance().getBoolean("EnableDriveAssist", false);
		
		//Process movement if you are not turning to stay straight
		if(Math.abs(rotation) < rotationThreshold && Math.abs(speed) > forwardThreshold && enableRotationCompensation) {
			//Start the move
			SmartDashboard.putBoolean("Compensating", true);
			dc.StartMove(gps.robotPosition);
			
			rotation = dc.ProcessRotation(rotation, gps.robotPosition);
		}
		
		if(Math.abs(speed) < forwardThreshold || Math.abs(rotation) > rotationThreshold) {
			dc.EndMove();
			SmartDashboard.putBoolean("Compensating", false);
		}
		
		
		
		rightOutput = rotation - speed;
		leftOutput = rotation + speed;
		
		leftFront.set(leftOutput);
		leftBack.set(leftOutput);
		rightFront.set(rightOutput);
		rightBack.set(rightOutput);
	}
	
	
	
}
