package org.usfirst.frc.team6749.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;


public class AutoRecord {

	ArrayList<AutoReplayTick> tickList;
	
	Timer t;
	
	double lastSpeed;
	double lastRot;
	
	boolean recording;
	
	String storeName;
	
	public void StartRecording (String nameToStore) {
		tickList = new ArrayList<AutoReplayTick>();
		storeName = nameToStore;
		recording = true;
		t = new Timer();
		t.reset();
		t.start();
	}
	
	public void RecordingPeriodic (double speed, double rot) {
		if(lastSpeed != speed || lastRot == rot && recording == true) {
			AutoReplayTick newTick = new AutoReplayTick(t.get(), speed, rot);
			tickList.add(newTick);
		}
		lastSpeed = speed;
		lastRot = rot;
	}
	
	public void StopAndStoreRecording () {
		t.stop();
		recording = false;
		
		String xport = "";
		
		for(AutoReplayTick t : tickList) {
			if(xport == "") {
				//It is empty
			} else {
				xport += "|";
			}
			xport += t.time +  "," + t.inputSpeed + "," + t.inputRotation;
		}

		Preferences.getInstance().putString(storeName, xport);
		
	}
	
}

class AutoReplayTick {
	
	public AutoReplayTick (double time, double speed, double rot) {
		this.time = time;
		inputSpeed = speed;
		inputRotation = rot;
	}
	
	public double time;
	public double inputSpeed;
	public double inputRotation;
	
}
