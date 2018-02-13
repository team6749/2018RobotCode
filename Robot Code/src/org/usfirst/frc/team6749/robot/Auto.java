package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auto {
	
	DriveController dc;
	
	public enum AutoState {Idle, DriveToLocation, WaypointDrive, CommandDrive};
	public enum AutoSubstate {Orientation, Drive, FinalRotation};
	
	double positionalAccuracy = 0.1; //Within 4 cm
	double rotationalAccuracy = 2; //Within 3 degrees
	
	double autoMoveSpeed = 0.7; //Max speed when auto driving
	double autoRotateSpeed = 0.4; //Max rotation when auto driving.
	
	double autoMoveSpeedSlow = 0.3; //Max speed when auto driving
	double autoRotateSpeedSlow = 0.4; //Max rotation when auto driving.
	
	double slowSpeedTrigger = 0.5;
	double slowRotateTrigger = 25;
	
	AutoState autoState = AutoState.Idle;
	AutoSubstate autoSubstate = AutoSubstate.Orientation;
	RobotPosition targetPosition;
	
	public Auto (DriveController driveController) {
		dc = driveController;
	}
	
	//This class gets called to keep the auto actually functioning
	public void AutoPeriodic (RobotPosition robotPosition) {
		if(autoState == AutoState.DriveToLocation) {
			DriveToLocationPeriodic (robotPosition, targetPosition);
		}
		if(autoState == AutoState.WaypointDrive) {
			WaypointDrivePeriodic (robotPosition);
		}
	}
	
	public void AutoDriveToLocation (RobotPosition targetPosition) {
		autoState = AutoState.DriveToLocation;
		autoSubstate = AutoSubstate.Orientation;
		this.targetPosition = targetPosition;
	}
	
	private void DriveToLocationPeriodic (RobotPosition robotPosition, RobotPosition target) {
		//We are trying to drive to a position
		//We need to turn to face the location then drive there.
		//So we need to calculate the degrees to look at that location
		//TODO
		if(autoSubstate == AutoSubstate.Orientation) {
			//We are trying to rotate to the looking direction
			
			double offset = GetRotationToPoint(robotPosition, target);
			
			if(Math.abs(offset) > rotationalAccuracy) {
				//We need to rotate towards our goal
				AutoDrive (0, GetDriveRotationAmount(offset));
			} else {
				//We have reached our goal now proced to move the robot while doing this too
				autoSubstate = AutoSubstate.Drive;
			}
			SmartDashboard.putNumber("Offset", offset);
			SmartDashboard.putNumber("Robot Cyclic rot", robotPosition.GetRotationCyclic());
		}
		
		//Second stage of an auto move, drive to the point
		if(autoSubstate == AutoSubstate.Drive) {
			
			double speed = 0;
			double rot = 0;
			
			//Calculate rotation error again to look at the point again
			double offset = GetRotationToPoint(robotPosition, target);
			if(Math.abs(offset) > rotationalAccuracy) {
				//We need to rotate towards our goal
				rot = GetDriveRotationAmount(offset);
			} else {
				//We have reached our goal now proced to move the robot while doing this too
				autoSubstate = AutoSubstate.Drive;
			}
			
			double dist = Math.sqrt(Math.pow((robotPosition.x - target.x), 2) + Math.pow((robotPosition.y - target.y), 2));
			
			if(dist > positionalAccuracy) {
				//We need to keep moving forward
				SmartDashboard.putNumber("DistanceToTarget", dist);
				
				if(dist > slowSpeedTrigger) {
					speed = autoMoveSpeed;
				} else {
					speed = autoMoveSpeedSlow;
				}
				
				speed = Helpers.ClampN11(speed);
			} else {
				//We arrived at the destination
				autoSubstate = AutoSubstate.FinalRotation;
			}
			
			SmartDashboard.putNumber("speeda", speed);
			SmartDashboard.putNumber("rota", rot);
			AutoDrive (speed, rot);
		}
	}
	
	
	double GetRotationToPoint (RobotPosition robotPosition, RobotPosition target) {
		double point_rot = Helpers.CalculateRotationBetweenPoints(robotPosition.ToPosition(), target.ToPosition());
		point_rot = Helpers.Convert180to360(point_rot);
		double offset = robotPosition.GetRotationCyclic() - point_rot;
		return offset;
	}
	
	double GetDriveRotationAmount (double offset) {
		double amt = 0;
		if(Math.abs(offset) > slowRotateTrigger) {
			amt = autoRotateSpeed * Helpers.PositiveOrNegitive(offset);
		} else {
			amt = autoRotateSpeedSlow * Helpers.PositiveOrNegitive(offset);
		}
		
		amt = Helpers.ClampN11(amt);
		SmartDashboard.putNumber("Test", amt);
		return -amt;
	}
	
	private void WaypointDrivePeriodic (RobotPosition robotPosition) {
		
	}
	
	void AutoDrive (double speed, double rotation) {
		dc.StandardDrive(speed * autoMoveSpeed, rotation * autoRotateSpeed);
	}
}