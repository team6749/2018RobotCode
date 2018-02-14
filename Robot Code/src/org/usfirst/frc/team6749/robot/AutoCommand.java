package org.usfirst.frc.team6749.robot;

public class AutoCommand {
	
	public AutoCommand (CommandType command) {
		this.commandType = command;
	}
	
	public void InitMove (double distance, double speed) {
		move_distance = distance;
		move_speed = speed;
	}
	
	public void InitRotate (double desired_facing_direction, double speed) {
		rotate_desired_rotation = desired_facing_direction;
		rotate_speed = speed;
	}
	
	//Position of the robot when the command is started
	RobotPosition startPosition;
	
	public enum CommandType {Move, Rotate};
	public CommandType commandType;
	
	//If it is a move command we want to move x distance
	double move_distance;
	double move_speed;
	
	//If it is a rotate command we want to get to rotation x
	double rotate_desired_rotation;
	double rotate_speed;
}
