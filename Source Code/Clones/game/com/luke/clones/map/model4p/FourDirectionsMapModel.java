package com.luke.clones.map.model4p;

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

public class FourDirectionsMapModel extends MapModel{

	public FourDirectionsMapModel() {
		super.numberOfPlayers = 4;
		super.mapName = "Four Directions";
		super.mapType = MapType.SYMMETRIC;
		
		super.mapSize = new int[]{17,13};

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
		new Position(1,10),
		new Position(11,0),
		new Position(1,11),
		new Position(11,1),
		new Position(1,12),
		new Position(11,2),
		new Position(11,3),
		new Position(12,12),
		new Position(12,11),
		new Position(12,10),
		new Position(11,9),
		new Position(1,0),
		new Position(1,1),
		new Position(1,2),
		new Position(1,3),
		new Position(1,4),
		new Position(1,5),
		new Position(1,6),
		new Position(1,7),
		new Position(1,8),
		new Position(1,9),
		new Position(11,10),
		new Position(12,0),
		new Position(12,1),
		new Position(12,2),
		new Position(12,3),
		new Position(12,9),
		new Position(2,0),
		new Position(2,1),
		new Position(2,2),
		new Position(2,3),
		new Position(2,9),
		new Position(13,0),
		new Position(0,10),
		new Position(13,1),
		new Position(0,11),
		new Position(13,2),
		new Position(0,12),
		new Position(13,3),
		new Position(11,12),
		new Position(11,11),
		new Position(13,9),
		new Position(3,0),
		new Position(3,1),
		new Position(3,2),
		new Position(3,3),
		new Position(3,9),
		new Position(14,0),
		new Position(14,1),
		new Position(14,2),
		new Position(14,3),
		new Position(14,9),
		new Position(4,0),
		new Position(4,1),
		new Position(4,2),
		new Position(4,3),
		new Position(4,9),
		new Position(15,0),
		new Position(15,1),
		new Position(15,2),
		new Position(15,3),
		new Position(15,4),
		new Position(15,5),
		new Position(15,6),
		new Position(15,7),
		new Position(15,8),
		new Position(15,9),
		new Position(5,0),
		new Position(5,1),
		new Position(5,2),
		new Position(5,3),
		new Position(5,9),
		new Position(16,0),
		new Position(16,1),
		new Position(16,2),
		new Position(16,3),
		new Position(16,4),
		new Position(16,5),
		new Position(16,6),
		new Position(16,7),
		new Position(16,8),
		new Position(16,9),
		new Position(7,6),
		new Position(8,5),
		new Position(8,6),
		new Position(8,7),
		new Position(16,12),
		new Position(16,11),
		new Position(16,10),
		new Position(5,10),
		new Position(5,11),
		new Position(5,12),
		new Position(9,6),
		new Position(15,12),
		new Position(4,10),
		new Position(15,11),
		new Position(4,11),
		new Position(15,10),
		new Position(4,12),
		new Position(3,10),
		new Position(3,11),
		new Position(14,12),
		new Position(3,12),
		new Position(14,11),
		new Position(14,10),
		new Position(2,10),
		new Position(2,11),
		new Position(2,12),
		new Position(13,12),
		new Position(13,11),
		new Position(13,10),
		};

		super.playerPositions = new PlayerPosition[]{
		new PlayerPosition(9, 12, PlayerType.ORANGE),
		new PlayerPosition(2, 5, PlayerType.GREEN),
		new PlayerPosition(2, 6, PlayerType.GREEN),
		new PlayerPosition(2, 7, PlayerType.GREEN),
		new PlayerPosition(8, 12, PlayerType.ORANGE),
		new PlayerPosition(14, 5, PlayerType.BLUE),
		new PlayerPosition(14, 6, PlayerType.BLUE),
		new PlayerPosition(14, 7, PlayerType.BLUE),
		new PlayerPosition(7, 12, PlayerType.ORANGE),
		new PlayerPosition(7, 0, PlayerType.RED),
		new PlayerPosition(8, 0, PlayerType.RED),
		new PlayerPosition(9, 0, PlayerType.RED),
		};
	}
	
}
