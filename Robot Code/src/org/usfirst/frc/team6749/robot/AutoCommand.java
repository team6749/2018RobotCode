package org.usfirst.frc.team6749.robot;

public class AutoCommand {
	
	public AutoCommand (CommandType command, GPS gps) {
		this.commandType = command;
		this.gps = gps;
	}
	
	public void InitMove (double distance, double speed) {
		move_distance = distance;
		move_speed = speed;
	}
	
	public void InitRotate (double rotate_amount, double speed) {
		this.rotate_amount = rotate_amount;
		rotate_speed = speed;
	}
	
	//Position of the robot when the command is started
	RobotPosition startPosition;
	GPS gps;
	
	public enum CommandType {Move, Rotate};
	public CommandType commandType;
	
	//If it is a move command we want to move x distance
	double start_distance = -1;
	double move_distance;
	double move_speed;
	
	//If it is a rotate command we want to get to rotation x
	double rotate_amount;
	double rotate_speed;
	double start_rotation = -1;
}
