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

public class ChessLikeMapModel extends MapModel{
	
	public ChessLikeMapModel(){
		super.numberOfPlayers = 2;
		super.mapName = "Chess like";
		super.mapType = MapType.SYMMETRIC;
		
		super.mapSize = new int[]{10,7};

		super.solids = new Position[]{
		new Position(0,0),
		new Position(0,1),
		new Position(0,2),
		new Position(0,3),
		new Position(0,4),
		new Position(0,5),
		new Position(0,6),
		new Position(9,0),
		new Position(9,1),
		new Position(9,2),
		new Position(9,3),
		new Position(9,4),
		new Position(9,5),
		new Position(9,6),
		};

		super.playerPositions = new PlayerPosition[]{
		new PlayerPosition(1, 0, PlayerType.BLUE),
		new PlayerPosition(1, 1, PlayerType.BLUE),
		new PlayerPosition(1, 2, PlayerType.BLUE),
		new PlayerPosition(1, 3, PlayerType.BLUE),
		new PlayerPosition(1, 4, PlayerType.BLUE),
		new PlayerPosition(1, 5, PlayerType.BLUE),
		new PlayerPosition(1, 6, PlayerType.BLUE),
		new PlayerPosition(8, 0, PlayerType.RED),
		new PlayerPosition(8, 1, PlayerType.RED),
		new PlayerPosition(8, 2, PlayerType.RED),
		new PlayerPosition(8, 3, PlayerType.RED),
		new PlayerPosition(8, 4, PlayerType.RED),
		new PlayerPosition(8, 5, PlayerType.RED),
		new PlayerPosition(8, 6, PlayerType.RED),
		};
	}
	

}
