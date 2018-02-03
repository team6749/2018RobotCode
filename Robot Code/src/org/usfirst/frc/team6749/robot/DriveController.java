package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveController {
	
	GPS gps; 
	
	double speedScale = 1f;
	double rotScale = 0.8f;
	
	double currentForward;
	double currentY;
	double currentRot;
	
	double forwardThreshold = 0.1f;
	double rotationThreshold = 0.3f;
	
	double forwardAcceleration = 0.05f;
	double rotationAcceleration = 0.03f;
	
	SpeedController rightFront;
	SpeedController rightBack;
	SpeedController leftFront;
	SpeedController leftBack;

	DriveController (GPS gps) {
		this.gps = gps;
		//Init SpeedControllers
		rightFront = new Spark(3);
		rightBack = new Spark (0);
		leftFront = new Spark (2);
		leftBack = new Spark (1);
	}
	
	void DriveRelative (double forwardInput, double rotationInput) {
		
		//Check rotation threshold
		if(rotationInput > 0 && rotationInput < rotationThreshold) {
			currentRot = 0;
		} else if(rotationInput < 0 && -rotationInput < rotationThreshold) {
			currentRot = 0;
		} else {
			//We are outside of the limits so lets accelerate towards the target
			//currentRot = z;
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
		
		StandardDrive(-currentForward * speedScale, currentRot * rotScale);
	}
	
	
	void StandardDrive (double speed, double rotation) {
		speed = ClampN11(speed);
		rotation  = ClampN11(rotation);
		
		double rightOutput = speed + rotation;
		double leftOutput = speed - rotation;
		
		leftOutput *= .5;
		rightOutput  *= .5;
		
		SmartDashboard.putNumber("Left Output", leftOutput);
		SmartDashboard.putNumber("Right Output", rightOutput);
		
		leftFront.set(leftOutput);
		leftBack.set(leftOutput);
		rightFront.set(rightOutput);
		rightBack.set(rightOutput);
	}
	
	double ClampN11 (double d) {
		if(d < -1) {
			d = -1;
		}
		if(d > 1) {
			d = 1;
		}
		return d;
	}
	
}
