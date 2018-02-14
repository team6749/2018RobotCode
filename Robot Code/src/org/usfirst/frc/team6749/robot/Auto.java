package org.usfirst.frc.team6749.robot;

import java.util.LinkedList;
import java.util.Queue;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auto {
	
	DriveController dc;
	Queue<AutoCommand> commandQueue;
	
	public enum AutoState {Idle, Driving, Rotation, Wait};
	
	double autoMoveSpeed = 0.7; //Max speed when auto driving
	double autoRotateSpeed = 0.4; //Max rotation when auto driving.
	
	AutoState autoState = AutoState.Idle;
	
	public Auto (DriveController driveController) {
		dc = driveController;
		commandQueue = new LinkedList<AutoCommand>();
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
				MoveCommandPeriodic(c);
			}
			if(c.commandType == AutoCommand.CommandType.Rotate) {
				RotateCommandPeriodic(c);
			}
		}
		
	}

	public void AddInstruction (AutoCommand command) {
		commandQueue.add(command);
	}
	
	public void MoveCommandPeriodic (AutoCommand command) {
		
	}
	
	public void RotateCommandPeriodic (AutoCommand command) {
		
	}
	
	void AutoDrive (double speed, double rotation) {
		dc.StandardDrive(speed * autoMoveSpeed, rotation * autoRotateSpeed);
	}
	
}