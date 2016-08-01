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

public class DividedMapModel extends MapModel{

	public DividedMapModel(){
		super.numberOfPlayers = 2;
		super.mapName = "Divided";
		super.mapType = MapType.SYMMETRIC;
		
		super.mapSize = new int[]{13,9};

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
		new Position(12,0),
		new Position(12,1),
		new Position(12,2),
		new Position(12,3),
		new Position(12,4),
		new Position(12,5),
		new Position(12,6),
		new Position(12,7),
		new Position(12,8),
		new Position(6,1),
		new Position(6,2),
		new Position(6,3),
		new Position(6,4),
		new Position(6,5),
		new Position(6,6),
		new Position(6,7),
		};

		super.playerPositions = new PlayerPosition[]{
		new PlayerPosition(5, 0, PlayerType.BLUE),
		new PlayerPosition(5, 2, PlayerType.BLUE),
		new PlayerPosition(5, 4, PlayerType.BLUE),
		new PlayerPosition(5, 6, PlayerType.BLUE),
		new PlayerPosition(5, 8, PlayerType.BLUE),
		new PlayerPosition(7, 0, PlayerType.RED),
		new PlayerPosition(7, 2, PlayerType.RED),
		new PlayerPosition(7, 4, PlayerType.RED),
		new PlayerPosition(7, 6, PlayerType.RED),
		new PlayerPosition(7, 8, PlayerType.RED),
		};
	}
}
