package com.luke.clones.model;

import com.luke.clones.map.model.MapType;
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

public class Map {
	private int width; // wide side
	private int height; // narrow side
	private Position[] startSolids; //obstacle fields
	private PlayerPosition[] startPlayers; // where player clones will be at the start
	private MapType mapType;
	
	/**
	 *For serialization
	 */
	public Map(){
		
	}
	
	public Map(int width, int height, Position[] startSolids, PlayerPosition[] startPlayers, MapType mapType){
		this.width = width;
		this.height = height;
		this.startSolids = startSolids;
		this.startPlayers = startPlayers;
		this.mapType = mapType;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Position[] getStartSolids() {
		return startSolids;
	}
	
	public PlayerPosition[] getStartPlayers() {
		return startPlayers;
	}

	public MapType getMapType() {
		return mapType;
	}

	public void setStartPlayers(PlayerPosition[] startPlayers) {
		this.startPlayers = startPlayers;
	}
	
}
