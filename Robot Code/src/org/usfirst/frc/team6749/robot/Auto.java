package org.usfirst.frc.team6749.robot;

import java.util.LinkedList;
import java.util.Queue;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auto {
	
	DriveController dc;
	GPS gps;
	
	Queue<AutoCommand> commandQueue;
	
	public enum AutoState {Idle, Driving, Rotation, Wait};
	
	double autoAcceleration = 5;
	
	double maxAutoSpeed = 0.4; //Max speed when auto driving
	double maxRotateSpeed = 0.7; //Max rotation when auto driving.
	
	AutoState autoState = AutoState.Idle;
	
	public Auto (DriveController driveController, GPS gps) {
		dc = driveController;
		commandQueue = new LinkedList<AutoCommand>();
		this.gps = gps;
	}
	
	//This function gets called to keep the auto actually functioning
	public void AutoPeriodic (RobotPosition robotPosition) {
		//If our queue is empty then we idle
		if(commandQueue.isEmpty()) {
			autoState = AutoState.Idle;
		} else {
			//We have a command we need to complete
			AutoCommand c = commandQueue.peek();
			
			if(c.commandType == AutoCommand.CommandType.Move) {
				autoState = AutoState.Driving;
				MoveCommandPeriodic(c);
			}
			if(c.commandType == AutoCommand.CommandType.Rotate) {
				autoState = AutoState.Rotation;
				RotateCommandPeriodic(c);
			}
		}

		SmartDashboard.putNumber("Command Queue Left", commandQueue.size());
	}

	public void AddCommand (AutoCommand command) {
		commandQueue.add(command);
	}
	
	private void CompletedCommand () {
		commandQueue.poll();
		dc.Stop();
	}
	
	void MoveCommandPeriodic (AutoCommand command) {
		//If we just started do x
		if(command.start_distance == -1) {
			command.start_distance = gps.robotPosition.distanceAbsolute;
		}
		
		double x = (Math.abs(command.move_distance) + command.start_distance) - gps.robotPosition.distanceAbsolute;
		
		if(x >= 0) {
			if (command.move_distance > 0) {
				AutoDrive(command.move_speed * maxAutoSpeed, 0);
			}
			if (command.move_distance <= 0){
				AutoDrive(-command.move_speed * maxAutoSpeed, 0);
			}
		} else {
			//we have moved far enough
			CompletedCommand ();
		}
	}
	
	public void RotateCommandPeriodic (AutoCommand command) {
		
		if(command.start_rotation == -1) {
			command.start_rotation = gps.robotPosition.rotation;
		}
		
		SmartDashboard.putNumber("Result", Math.abs(command.start_rotation + command.rotate_amount) - Math.abs(gps.robotPosition.rotation));
		if(command.rotate_amount > 0) {
			if(Math.abs(command.start_rotation + command.rotate_amount) - Math.abs(gps.robotPosition.rotation) > 0) {
				AutoDrive (0, -command.rotate_speed * maxRotateSpeed);
			} else {
				CompletedCommand();
			}
		}
		if(command.rotate_amount < 0) {
			if(Math.abs(gps.robotPosition.rotation) - Math.abs(command.start_rotation + Math.abs(command.rotate_amount)) > 0) {
				AutoDrive (0, command.rotate_speed * maxRotateSpeed);
			} else {
				CompletedCommand();
			}
		}
	}
	
	void AutoDrive (double speed, double rotation) {
		dc.DriveRelative(-speed, rotation);
	}
	
}