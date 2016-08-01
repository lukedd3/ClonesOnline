package com.luke.clones.map.model;

import com.luke.clones.model.type.PlayerPosition;
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

public class MapModel {	
	protected int numberOfPlayers;
	protected String mapName;
	protected MapType mapType = MapType.SYMMETRIC;
	
	protected int[] mapSize;
	protected Position[] solids;
	//In asymetric maps starting player positions should be first on list
	protected PlayerPosition[] playerPositions; 
	
	public MapModel() {
		
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public String getMapName() {
		return mapName;
	}
	
	public MapType getMapType() {
		return mapType;
	}

	public int[] getMapSize() {
		return mapSize;
	}

	public Position[] getSolids() {
		return solids;
	}

	public PlayerPosition[] getPlayerPositions() {
		return playerPositions;
	}

}
