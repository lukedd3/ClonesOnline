package com.luke.clones.test.production.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.luke.clones.android.AndroidNativeHandler;
import com.luke.clones.test.production.ProductionTest;
import com.luke.clones.test.production.implementation.helper.TestRoomInfoWrapper;
import com.luke.clones.test.production.implementation.helper.TestRoomState;
import com.luke.network.controller.ClonesClient;

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

public class RoomCreateRemoveTest extends ProductionTest{
	private final static int S_TO_MS_MULTIPLER=1000;
	
	private int roomExistingTimeInSeconds = 30;
	private int roomNonExistingTimeInSeconds = 10;
	private int numberOfRooms = 100;
	private int nextRoomCreationIntervalInSeconds = 1;
	
	private List<TestRoomInfoWrapper> testRooms;
	
	private TimerTask startTask;
	private TimerTask testLoopTask;
	
	private ClonesClient roomCreateRemoveTestClonesClient;

	public RoomCreateRemoveTest(AndroidNativeHandler androidNativeHandler,
			int serverNumberOnServerList) {
		super(androidNativeHandler, serverNumberOnServerList);
		super.setNumberOfClients(1);
	}

	@Override
	protected void test() {
		initialize();
		prepareAndRunStartTask();
		prepareAndRunTestLoopTask();
	}
	
	private void initialize() {
		roomCreateRemoveTestClonesClient = clonesClientTestWrappers.get(0).getClonesClient();
		testRooms = new ArrayList<TestRoomInfoWrapper>();
	}
	
	private void prepareAndRunStartTask() {
		startTask = new TimerTask() {
			@Override
			public void run() {
				startLoop();
			}
		};
		
		Timer startTimer = new Timer();
		startTimer.schedule(startTask, 0, nextRoomCreationIntervalInSeconds*S_TO_MS_MULTIPLER);
	}
	
	private void startLoop(){
		if(testRooms.size()<numberOfRooms){
			TestRoomInfoWrapper testRoom = new TestRoomInfoWrapper();
			testRoom.createRoom(roomCreateRemoveTestClonesClient);
			testRooms.add(testRoom);
		}
		else{
			startTask.cancel();
		}
	}

	private void prepareAndRunTestLoopTask() {
		testLoopTask = new TimerTask() {
			@Override
			public void run() {
				testLoop();
			}
		};
		
		Timer testLoopTimer = new Timer();
		testLoopTimer.schedule(testLoopTask, 1000, 1000);
	}
	
	private void testLoop(){
		for(TestRoomInfoWrapper testRoom: testRooms){
			if(testRoom.getTestRoomState()==TestRoomState.EXISTING){
				if(testRoom.getExistingTime().getMeasuredTimeInMs()>roomExistingTimeInSeconds*S_TO_MS_MULTIPLER){
					testRoom.removeRoom(roomCreateRemoveTestClonesClient);
				}
			}
			else if(testRoom.getTestRoomState()==TestRoomState.NON_EXISTING){
				if(testRoom.getNonExistingTime().getMeasuredTimeInMs()>roomNonExistingTimeInSeconds*S_TO_MS_MULTIPLER){
					testRoom.createRoom(roomCreateRemoveTestClonesClient);
				}
			}
		}
	}

	@Override
	protected void stopTest() {
		try {
			if(startTask!=null) startTask.cancel();
			if(testLoopTask!=null) testLoopTask.cancel();
			if(testRooms!=null){
				for(TestRoomInfoWrapper testRoom: testRooms){
					testRoom.removeRoom(roomCreateRemoveTestClonesClient);
					Thread.sleep(100);
				}
			}
			roomCreateRemoveTestClonesClient.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
