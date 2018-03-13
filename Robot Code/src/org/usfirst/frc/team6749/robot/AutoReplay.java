package org.usfirst.frc.team6749.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoReplay {
	
	ArrayList<AutoReplayTick> recording;
	Timer t;
	
	boolean isReplaying = false;
	
	public void StartReplay (String recording) {
		this.recording = ParseRecording(recording);
		isReplaying = true;
		t = new Timer();
		t.reset();
		t.start();
	}
	
	public double[] ReplayPeriodic () {
		AutoReplayTick tickToUse = new AutoReplayTick(-1, new float[] {0});
		for (AutoReplayTick tick : recording) {
			if(tick.time <= t.get()) {
				//This is going to be the next closest tick
				tickToUse = tick;
			}
		}
		
		if(tickToUse.time != -1) {
			//We have to run some inputs yay
			
			//convert the floats to doubles
			double[] converted = new double[tickToUse.inputs.length];
			
			for(int i = 0; i < tickToUse.inputs.length; i++) {
				converted[i] = (double)tickToUse.inputs[i];
			}
			
			return converted;
		} else {
			//We have finished because there is nothing else to do
			isReplaying = false;
			return new double[] {0, 0, 0, 0, 0};
		}
	}
	
	public ArrayList<AutoReplayTick> ParseRecording (String recording) {
		ArrayList<AutoReplayTick> parsed = new ArrayList<AutoReplayTick>();
		
		String[] splitByTick = recording.split("a");
		
		
		for (String s : splitByTick) {
			String[] splitTick = s.split(Pattern.quote("b"));
			
			//Subtract the first element from the array and then process the rest into an array
			String[] withoutFirst = Arrays.copyOfRange(splitTick, 1, splitTick.length);
			
			
			ArrayList<Float> tempDoubleArray = new ArrayList<Float>();
			
			for (String floatParams : withoutFirst) {
				float x = 0;

				if(floatParams != "") {
					x = Float.parseFloat(floatParams);
				}
				
				tempDoubleArray.add(x);
			}
			
			//Convert the temp arrayList to an array to pass to the robot output
			float[] finalInputDoubleArray = new float[tempDoubleArray.size()];
			for(float x : tempDoubleArray) {
				finalInputDoubleArray[tempDoubleArray.indexOf(x)] = x;
			}
			
			
			AutoReplayTick t = new AutoReplayTick(Float.parseFloat(splitTick[0]), finalInputDoubleArray);
			parsed.add(t);
		}
		
		return parsed;
	}
	
	
}
