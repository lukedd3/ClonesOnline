package com.luke.clones.model;

/* The MIT License (MIT)

Copyright (c) 2016 £ukasz Dziak

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE. */

public class TimeMeasure {
	private TimeMeasureState timeMeasureState = TimeMeasureState.BEFORE_FIRST_START;
	
	private Long startTime = 0L;
	private Long stopTime = 0L;
	private Long measuredTime = 0L;
	
	public void start(){
		timeMeasureState = TimeMeasureState.STARTED;
		
		startTime = System.nanoTime();
	}
	
	public void update(){
		measuredTime = System.nanoTime()-startTime;
	}
	
	public void stop(){
		timeMeasureState = TimeMeasureState.STOPPED;
		
		stopTime = System.nanoTime();
		measuredTime = stopTime-startTime;
	}
	
	public Long getStartTimeInMs(){
		return startTime/1000000;
	}
	
	public Long getStopTimeInMs(){
		return stopTime/1000000;
	}
	
	public Long getMeasuredTimeInMs(){
		if(timeMeasureState==TimeMeasureState.STARTED){
			update();
		}
		return measuredTime/1000000;
	}

}
