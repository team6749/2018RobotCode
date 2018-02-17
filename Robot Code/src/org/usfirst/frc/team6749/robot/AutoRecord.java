package org.usfirst.frc.team6749.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;


public class AutoRecord {

	ArrayList<AutoReplayTick> tickList;
	
	Timer t;
	
	float[] inputs;
	
	int oldHash;
	
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
	
	public void RecordingPeriodic (double[] inputs) {
		//Convert the inputs
		float[] convertedInputs = new float[inputs.length];
		for (int i = 0 ; i < inputs.length; i++) {
			convertedInputs[i] = (float)inputs[i];
		}
		
		if(inputs.hashCode() != oldHash && recording == true) {
			AutoReplayTick newTick = new AutoReplayTick((float)t.get(), convertedInputs);
			
			//We need the first seconds to be 0 otherwise it breaks for some reason.
			if(tickList.size() == 0) {
				//This will happen for the first element
				newTick.time = 0;
			}
			
			tickList.add(newTick);
		}
		oldHash = inputs.hashCode();
	}
	
	public void StopAndStoreRecording () {
		t.stop();
		recording = false;
		
		String xport = "";
		
		for(AutoReplayTick t : tickList) {
			if(xport == "") {
				//It is empty
			} else {
				xport += "a";
			}
			
			xport += t.time;
			
			for (float input : t.inputs) {
				xport += "b" + input;
			}
		}
		
		Preferences.getInstance().remove(storeName);
		Preferences.getInstance().putString(storeName, xport);
		
	}
	
}

class AutoReplayTick {
	
	public AutoReplayTick (float time, float[] inputs) {
		this.time = time;
		this.inputs = inputs;
	}
	
	public float time;
	public float[] inputs;
	
}
