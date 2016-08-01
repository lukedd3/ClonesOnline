package com.luke.clones.model;

import java.util.ArrayList;

import com.luke.clones.map.model.AvailableMapModelsType;
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

 public class MapAutoCreator {
	/**
	 * Creates map that is necessary to start clones game (precisely: to set ScreenLocalMultiplayerGame 
	 * or any other game screen as a current screen or to manually, using game method start game one more time);
	 * 
	 * @param PlayerNumber, accepted: 2, 4
	 * @param mapSize, accepted: "Small", "Medium", "Big"
	 * @param mapShape, accepted: "Rectangle", "Cross"
	 * @param mapObstacles, accepted: "None", "Small Obstacles", "Medium Obstacles", "Big Obstacles"
	 * @return Map containing it's width, height, solids (shape and obstacles) and player positions
	 */
	public static Map createMap(MapModel mapModel) throws MapCreatorArgumentException {
		int width; // wide side
		int height; // narrow side
		Position[] startSolids; //obstacle fields
		PlayerPosition[] startPlayers; // where player clones will be at the start
		MapType mapType;
		
		int[] intSize = mapModel.getMapSize();
		width = intSize[0];
		height = intSize[1];
		
		startSolids = mapModel.getSolids();
		startPlayers = mapModel.getPlayerPositions();
		
		mapType = mapModel.getMapType();
		
		return new Map(width, height, startSolids, startPlayers, mapType);
	}
	
	public static Map createMap(AvailableMapModelsType availableMapModelsType) throws MapCreatorArgumentException {
		return createMap(availableMapModelsType.getMap());
	}
	
	public static class MapCreatorArgumentException extends Exception{
		private static final long serialVersionUID = 7795742930373657903L;

		@Override
		public String getMessage() {
			String message = "Illegal argument passed to the method";
			return super.getMessage() + message;
		}
	}
	
}
