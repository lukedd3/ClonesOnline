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

public class TunelRatsMapModel extends MapModel{
	public TunelRatsMapModel() {
		super.numberOfPlayers = 2;
		super.mapName = "Tunnel Rats";
		super.mapType = MapType.SYMMETRIC;
		
		super.mapSize = new int[]{16,11};

		super.solids = new Position[]{
		new Position(0,0),
		new Position(0,1),
		new Position(8,9),
		new Position(0,2),
		new Position(0,3),
		new Position(0,4),
		new Position(0,6),
		new Position(0,7),
		new Position(0,8),
		new Position(0,9),
		new Position(1,10),
		new Position(11,0),
		new Position(11,2),
		new Position(5,10),
		new Position(12,10),
		new Position(11,8),
		new Position(9,2),
		new Position(9,4),
		new Position(9,6),
		new Position(1,0),
		new Position(1,1),
		new Position(1,2),
		new Position(1,4),
		new Position(1,6),
		new Position(1,8),
		new Position(1,9),
		new Position(11,10),
		new Position(12,0),
		new Position(12,2),
		new Position(12,4),
		new Position(12,6),
		new Position(12,8),
		new Position(2,0),
		new Position(2,2),
		new Position(2,4),
		new Position(2,6),
		new Position(2,8),
		new Position(13,0),
		new Position(0,10),
		new Position(13,2),
		new Position(13,4),
		new Position(4,10),
		new Position(13,6),
		new Position(15,10),
		new Position(13,8),
		new Position(3,0),
		new Position(3,2),
		new Position(3,4),
		new Position(3,6),
		new Position(3,8),
		new Position(10,10),
		new Position(14,0),
		new Position(14,1),
		new Position(14,2),
		new Position(14,4),
		new Position(14,6),
		new Position(14,8),
		new Position(14,9),
		new Position(4,0),
		new Position(4,2),
		new Position(4,8),
		new Position(15,0),
		new Position(15,1),
		new Position(15,2),
		new Position(15,3),
		new Position(15,4),
		new Position(3,10),
		new Position(15,6),
		new Position(15,7),
		new Position(15,8),
		new Position(14,10),
		new Position(15,9),
		new Position(5,0),
		new Position(5,2),
		new Position(5,4),
		new Position(5,6),
		new Position(6,4),
		new Position(6,6),
		new Position(6,8),
		new Position(2,10),
		new Position(13,10),
		new Position(7,1),
		new Position(7,3),
		new Position(7,7),
		new Position(7,8),
		new Position(7,9),
		new Position(10,0),
		new Position(10,4),
		new Position(10,6),
		new Position(8,1),
		new Position(10,8),
		new Position(8,2),
		new Position(8,3),
		new Position(8,7),
		};

		super.playerPositions = new PlayerPosition[]{
		new PlayerPosition(11, 5, PlayerType.RED),
		new PlayerPosition(4, 5, PlayerType.BLUE),
		};
		
	}
}
