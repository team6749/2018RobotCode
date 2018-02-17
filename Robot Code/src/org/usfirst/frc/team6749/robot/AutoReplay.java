package org.usfirst.frc.team6749.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;

public class AutoReplay {

	
	DriveController dc;
	ArrayList<AutoReplayTick> recording;
	Timer t;
	
	boolean isReplaying = false;
	
	public void StartReplay (String recording, DriveController drive) {
		dc = drive;
		this.recording = ParseRecording(recording);
		isReplaying = true;
		t = new Timer();
		t.reset();
		t.start();
	}
	
	public void ReplayPeriodic () {
		AutoReplayTick tickToUse = new AutoReplayTick(-1, 0, 0);
		for (AutoReplayTick tick : recording) {
			if(tick.time <= t.get()) {
				//This is going to be the next closest tick
				tickToUse = tick;
			}
		}
		
		if(tickToUse.time != -1) {
			dc.DriveRelative(tickToUse.inputSpeed, tickToUse.inputRotation);
		} else {
			//We have finished because there is nothing else to do
			isReplaying = false;
		}
	}
	
	public ArrayList<AutoReplayTick> ParseRecording (String recording) {
		ArrayList<AutoReplayTick> parsed = new ArrayList<AutoReplayTick>();
		
		String[] splitByTick = recording.split("|");
		
		for (String s : splitByTick) {
			String[] splitTick = s.split(",");
			AutoReplayTick t = new AutoReplayTick(Double.parseDouble(splitTick[0]), Double.parseDouble(splitTick[1]), Double.parseDouble(splitTick[2]));
			parsed.add(t);
		}
		
		return parsed;
	}
	
	
}
