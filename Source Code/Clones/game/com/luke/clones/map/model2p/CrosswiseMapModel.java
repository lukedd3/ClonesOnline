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

public class CrosswiseMapModel extends MapModel{

	public CrosswiseMapModel() {
		super.numberOfPlayers = 2;
		super.mapName = "Crosswise";
		super.mapType = MapType.SYMMETRIC;
		
//		super.mapSize = new int[]{12,8};
//
//		super.solids = new Position[]{
//		new Position(0,0),
//		new Position(4,4),
//		new Position(0,2),
//		new Position(4,6),
//		new Position(0,4),
//		new Position(0,6),
//		new Position(11,1),
//		new Position(11,3),
//		new Position(11,5),
//		new Position(11,7),
//		new Position(9,1),
//		new Position(9,3),
//		new Position(5,1),
//		new Position(9,5),
//		new Position(5,3),
//		new Position(9,7),
//		new Position(1,1),
//		new Position(5,5),
//		new Position(1,3),
//		new Position(5,7),
//		new Position(1,5),
//		new Position(1,7),
//		new Position(6,0),
//		new Position(6,2),
//		new Position(2,0),
//		new Position(6,4),
//		new Position(2,2),
//		new Position(6,6),
//		new Position(2,4),
//		new Position(2,6),
//		new Position(7,1),
//		new Position(7,3),
//		new Position(3,1),
//		new Position(7,5),
//		new Position(3,3),
//		new Position(7,7),
//		new Position(3,5),
//		new Position(3,7),
//		new Position(10,0),
//		new Position(10,2),
//		new Position(10,4),
//		new Position(10,6),
//		new Position(8,0),
//		new Position(8,2),
//		new Position(4,0),
//		new Position(8,4),
//		new Position(4,2),
//		new Position(8,6),
//		};
//
//		super.playerPositions = new PlayerPosition[]{
//		new PlayerPosition(4, 5, PlayerType.BLUE),
//		new PlayerPosition(5, 4, PlayerType.BLUE),
//		new PlayerPosition(6, 3, PlayerType.RED),
//		new PlayerPosition(7, 2, PlayerType.RED),
//		};
		
		super.mapSize = new int[]{12,8};

		super.solids = new Position[]{
		new Position(0,0),
		new Position(4,4),
		new Position(0,2),
		new Position(4,6),
		new Position(0,4),
		new Position(0,6),
		new Position(11,1),
		new Position(11,3),
		new Position(11,5),
		new Position(11,7),
		new Position(9,1),
		new Position(9,3),
		new Position(5,1),
		new Position(5,3),
		new Position(9,7),
		new Position(1,1),
		new Position(5,5),
		new Position(1,3),
		new Position(5,7),
		new Position(1,7),
		new Position(6,0),
		new Position(6,2),
		new Position(2,0),
		new Position(6,4),
		new Position(6,6),
		new Position(2,4),
		new Position(2,6),
		new Position(7,1),
		new Position(7,3),
		new Position(3,1),
		new Position(7,5),
		new Position(3,3),
		new Position(7,7),
		new Position(3,5),
		new Position(3,7),
		new Position(10,0),
		new Position(10,4),
		new Position(10,6),
		new Position(8,0),
		new Position(8,2),
		new Position(4,0),
		new Position(8,4),
		new Position(4,2),
		new Position(8,6),
		};

		super.playerPositions = new PlayerPosition[]{
		new PlayerPosition(4, 5, PlayerType.BLUE),
		new PlayerPosition(5, 4, PlayerType.BLUE),
		new PlayerPosition(6, 3, PlayerType.RED),
		new PlayerPosition(7, 2, PlayerType.RED),
		};

	}
	
}
