package com.luke.clones.test.production.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.android.AndroidNativeHandler;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.test.production.ProductionTest;
import com.luke.clones.test.production.helper.ClonesClientTestWrapper;
import com.luke.clones.test.production.implementation.helper.TestRoomInfoWrapper;
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

public class GameSimulationTest extends ProductionTest implements
		ListenerService {
	private final static int S_TO_MS_MULTIPLER = 1000;

	private int numberOfGames = 49;
	private int playersEachRoom = 2; // do not change, 4 not implemented yet
	private int numberOfPlayers;

	private int nextRoomCreationIntervalInSeconds = 1;

	private volatile List<TestRoomInfoWrapper> testRooms;

	private TimerTask startTask;

	public GameSimulationTest(AndroidNativeHandler androidNativeHandler,
			int serverNumberOnServerList) {
		super(androidNativeHandler, serverNumberOnServerList);
		numberOfPlayers = numberOfGames * playersEachRoom;
		super.setNumberOfClients(numberOfPlayers);
	}

	@Override
	protected void test() {
		initialize();
		prepareAndRunStartTask();
	}

	private void initialize() {
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
		startTimer.schedule(startTask, 0, nextRoomCreationIntervalInSeconds
				* S_TO_MS_MULTIPLER);
	}

	private void startLoop() {
		try {
			if (testRooms.size() < numberOfGames) {
				int j;
				if (testRooms.size() == 0)
					j = (testRooms.size()) * 2;
				else {
					j = (testRooms.size() - 1) * 2;
				}
				final TestRoomInfoWrapper testRoom = new TestRoomInfoWrapper();
				final ClonesClient clientAdmin = clonesClientTestWrappers
						.get(j).getClonesClient();
				final ClonesClient clientB = clonesClientTestWrappers
						.get(j + 1).getClonesClient();
				testRoom.createRoom(clientAdmin);
				Thread.sleep(100L);
				testRoom.joinRoom(clientB);
				Thread.sleep(100L);
				testRoom.startGame(clientAdmin);
				testRooms.add(testRoom);

				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						// System.out.println("A");
						testRoom.skipTurn(clientAdmin);
					}
				}, 3000, 6000);
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						// System.out.println("B");
						testRoom.skipTurn(clientB);
					}
				}, 6000, 6000);
			} else {
				startTask.cancel();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void stopTest() {
		try {
			startTask.cancel();
			Thread.sleep(300L);
			for (ClonesClientTestWrapper clonesClientTestWrapper : clonesClientTestWrappers) {
				clonesClientTestWrapper.getClonesClient().stop();
				System.out.println("One of clients stopped");
				Thread.sleep(100L);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void received(Connection connection, Object obj) {
		// TODO Auto-generated method stub
		super.received(connection, obj);
	}

}
