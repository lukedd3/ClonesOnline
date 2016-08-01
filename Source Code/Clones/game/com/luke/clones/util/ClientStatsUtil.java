package com.luke.clones.util;

import java.util.HashMap;
import java.util.Map;

import com.luke.clones.network.communication.StatsType;

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

public class ClientStatsUtil {
	public static Map<StatsType, Integer> statsValuesToPercent(Map<StatsType, Long> statsMap){
		Map<StatsType, Integer> statsPercentMap = new HashMap<StatsType, Integer>();
		
		
		long finishedNotAbandoned2PlayerGames = statsMap.get(StatsType.WON_2P_GAMES) + statsMap.get(StatsType.LOST_2P_GAMES);
		int won2PGamesPercent = calculatePercent((float)statsMap.get(StatsType.WON_2P_GAMES),
				(float)finishedNotAbandoned2PlayerGames);
		int lost2PGamesPercent = calculatePercent((float)statsMap.get(StatsType.LOST_2P_GAMES),
				(float)finishedNotAbandoned2PlayerGames);
		int percent100Difference = 100 - (won2PGamesPercent+lost2PGamesPercent);
		if(percent100Difference<0) won2PGamesPercent += percent100Difference;
		statsPercentMap.put(StatsType.WON_2P_GAMES, won2PGamesPercent);
		statsPercentMap.put(StatsType.LOST_2P_GAMES, lost2PGamesPercent);
		
		long finishedNotAbandoned4PlayerGames = statsMap.get(StatsType.WON_4P_GAMES) + statsMap.get(StatsType.LOST_4P_GAMES);
		int won4PGamesPercent = calculatePercent((float)statsMap.get(StatsType.WON_4P_GAMES),
				(float)finishedNotAbandoned4PlayerGames);
		int lost4PGamesPercent = calculatePercent((float)statsMap.get(StatsType.LOST_4P_GAMES),
				(float)finishedNotAbandoned4PlayerGames);
		percent100Difference = 100 - (won4PGamesPercent+lost4PGamesPercent);
		if(percent100Difference<0) won4PGamesPercent += percent100Difference;
		statsPercentMap.put(StatsType.WON_4P_GAMES, won4PGamesPercent);
		statsPercentMap.put(StatsType.LOST_4P_GAMES, lost4PGamesPercent);
		
//		long totalWonInRow = statsMap.get(StatsType.LAST_WON_IN_ROW)+statsMap.get(StatsType.BIGGEST_WON_IN_ROW);
		int lastWonInRowGamesPercent = calculatePercent((float)statsMap.get(StatsType.LAST_WON_IN_ROW), (float)statsMap.get(StatsType.BIGGEST_WON_IN_ROW));
		int biggestWonInRowGamesPercent = 100 - lastWonInRowGamesPercent;
		percent100Difference = 100 - (lastWonInRowGamesPercent+biggestWonInRowGamesPercent);
		if(percent100Difference<0) lastWonInRowGamesPercent += percent100Difference;
		statsPercentMap.put(StatsType.LAST_WON_IN_ROW, lastWonInRowGamesPercent);
		statsPercentMap.put(StatsType.BIGGEST_WON_IN_ROW, biggestWonInRowGamesPercent);
		
		return statsPercentMap;
	}
	
	private static int calculatePercent(float divident, float divisor){
		float ratio = divident/divisor;
		int percent = Math.round(ratio*100);
		return percent;
	}
	
}
