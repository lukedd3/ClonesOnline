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

public class EscapeMapModel extends MapModel{

	public EscapeMapModel() {
		super.numberOfPlayers = 4;
		super.mapName = "Escape";
		super.mapType = MapType.SYMMETRIC;
		
		super.mapSize = new int[]{16,10};

		super.solids = new Position[]{
		new Position(9,3),
		new Position(9,4),
		new Position(9,5),
		new Position(9,6),
		new Position(1,1),
		new Position(1,2),
		new Position(1,3),
		new Position(1,6),
		new Position(1,7),
		new Position(1,8),
		new Position(12,1),
		new Position(12,2),
		new Position(12,3),
		new Position(12,6),
		new Position(12,7),
		new Position(12,8),
		new Position(2,1),
		new Position(2,3),
		new Position(2,6),
		new Position(2,8),
		new Position(13,1),
		new Position(13,3),
		new Position(13,6),
		new Position(13,8),
		new Position(3,1),
		new Position(3,2),
		new Position(3,3),
		new Position(3,6),
		new Position(3,7),
		new Position(3,8),
		new Position(14,1),
		new Position(14,2),
		new Position(14,3),
		new Position(14,6),
		new Position(14,7),
		new Position(14,8),
		new Position(6,3),
		new Position(6,4),
		new Position(6,5),
		new Position(6,6),
		new Position(7,3),
		new Position(7,6),
		new Position(8,3),
		new Position(8,6),
		};

		super.playerPositions = new PlayerPosition[]{
		new PlayerPosition(2, 2, PlayerType.GREEN),
		new PlayerPosition(2, 7, PlayerType.BLUE),
		new PlayerPosition(13, 2, PlayerType.RED),
		new PlayerPosition(13, 7, PlayerType.ORANGE),
		};
	}
	
}
