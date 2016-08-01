package com.luke.clones.map.model2p;

import com.luke.clones.map.model.MapModel;
import com.luke.clones.map.model.MapType;
import com.luke.clones.model.type.PlayerPosition;
import com.luke.clones.model.type.PlayerType;
import com.luke.clones.model.type.Position;

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

public class EndTest2MapModel extends MapModel{

	public EndTest2MapModel(){
		super.numberOfPlayers = 2;
		super.mapName = "End Test 2";
		super.mapType = MapType.SYMMETRIC;
		
		super.mapSize = new int[]{7,6};

		super.solids = new Position[]{
		new Position(3,0),
		new Position(3,2),
		new Position(3,3),
		new Position(3,5),
		};

		super.playerPositions = new PlayerPosition[]{
		new PlayerPosition(0, 0, PlayerType.BLUE),
		new PlayerPosition(2, 2, PlayerType.BLUE),
		new PlayerPosition(4, 4, PlayerType.RED),
		new PlayerPosition(0, 1, PlayerType.BLUE),
		new PlayerPosition(2, 3, PlayerType.BLUE),
		new PlayerPosition(4, 5, PlayerType.RED),
		new PlayerPosition(0, 2, PlayerType.BLUE),
		new PlayerPosition(2, 4, PlayerType.BLUE),
		new PlayerPosition(0, 3, PlayerType.BLUE),
		new PlayerPosition(2, 5, PlayerType.BLUE),
		new PlayerPosition(0, 4, PlayerType.BLUE),
		new PlayerPosition(0, 5, PlayerType.BLUE),
		new PlayerPosition(5, 0, PlayerType.RED),
		new PlayerPosition(5, 1, PlayerType.RED),
		new PlayerPosition(5, 2, PlayerType.RED),
		new PlayerPosition(5, 3, PlayerType.RED),
		new PlayerPosition(1, 0, PlayerType.BLUE),
		new PlayerPosition(5, 4, PlayerType.RED),
		new PlayerPosition(1, 1, PlayerType.BLUE),
		new PlayerPosition(5, 5, PlayerType.RED),
		new PlayerPosition(1, 2, PlayerType.BLUE),
		new PlayerPosition(1, 3, PlayerType.BLUE),
		new PlayerPosition(1, 4, PlayerType.BLUE),
		new PlayerPosition(1, 5, PlayerType.BLUE),
		new PlayerPosition(6, 0, PlayerType.RED),
		new PlayerPosition(6, 1, PlayerType.RED),
		new PlayerPosition(4, 0, PlayerType.RED),
		new PlayerPosition(6, 2, PlayerType.RED),
		new PlayerPosition(4, 1, PlayerType.RED),
		new PlayerPosition(6, 3, PlayerType.RED),
		new PlayerPosition(2, 0, PlayerType.BLUE),
		new PlayerPosition(4, 2, PlayerType.RED),
		new PlayerPosition(6, 4, PlayerType.RED),
		new PlayerPosition(2, 1, PlayerType.BLUE),
		new PlayerPosition(4, 3, PlayerType.RED),
		new PlayerPosition(6, 5, PlayerType.RED),
		};
	}
}
