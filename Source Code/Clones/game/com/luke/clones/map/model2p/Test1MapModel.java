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

public class Test1MapModel extends MapModel{

	public Test1MapModel() {
		super.numberOfPlayers = 2;
		super.mapName = "Test 1";
		super.mapType = MapType.ASYMMETRIC;
		
		super.mapSize = new int[]{18,12};

		super.solids = new Position[]{
		new Position(0,0),
		new Position(0,1),
		new Position(0,2),
		new Position(0,3),
		new Position(0,4),
		new Position(0,5),
		new Position(0,6),
		new Position(0,7),
		new Position(0,8),
		new Position(0,9),
		new Position(0,10),
		new Position(0,11),
		new Position(17,0),
		new Position(17,1),
		new Position(17,2),
		new Position(17,3),
		new Position(17,4),
		new Position(17,5),
		new Position(17,6),
		new Position(17,7),
		new Position(17,11),
		new Position(17,8),
		new Position(17,10),
		new Position(17,9),
		};

		super.playerPositions = new PlayerPosition[]{
		new PlayerPosition(11, 5, PlayerType.RED),
		new PlayerPosition(11, 7, PlayerType.BLUE),
		new PlayerPosition(1, 5, PlayerType.RED),
		new PlayerPosition(1, 7, PlayerType.BLUE),
		new PlayerPosition(12, 4, PlayerType.BLUE),
		new PlayerPosition(12, 6, PlayerType.RED),
		new PlayerPosition(2, 4, PlayerType.BLUE),
		new PlayerPosition(2, 6, PlayerType.RED),
		new PlayerPosition(13, 5, PlayerType.RED),
		new PlayerPosition(13, 7, PlayerType.BLUE),
		new PlayerPosition(3, 5, PlayerType.RED),
		new PlayerPosition(3, 7, PlayerType.BLUE),
		new PlayerPosition(14, 4, PlayerType.BLUE),
		new PlayerPosition(14, 6, PlayerType.RED),
		new PlayerPosition(4, 4, PlayerType.BLUE),
		new PlayerPosition(4, 6, PlayerType.RED),
		new PlayerPosition(15, 5, PlayerType.RED),
		new PlayerPosition(15, 7, PlayerType.BLUE),
		new PlayerPosition(5, 5, PlayerType.RED),
		new PlayerPosition(5, 7, PlayerType.BLUE),
		new PlayerPosition(16, 4, PlayerType.BLUE),
		new PlayerPosition(16, 6, PlayerType.RED),
		new PlayerPosition(6, 4, PlayerType.BLUE),
		new PlayerPosition(6, 6, PlayerType.RED),
		new PlayerPosition(7, 5, PlayerType.RED),
		new PlayerPosition(7, 7, PlayerType.BLUE),
		new PlayerPosition(8, 4, PlayerType.BLUE),
		new PlayerPosition(8, 6, PlayerType.RED),
		new PlayerPosition(9, 5, PlayerType.RED),
		new PlayerPosition(9, 7, PlayerType.BLUE),
		new PlayerPosition(10, 4, PlayerType.BLUE),
		new PlayerPosition(10, 6, PlayerType.RED),
		};
		
	}

}
