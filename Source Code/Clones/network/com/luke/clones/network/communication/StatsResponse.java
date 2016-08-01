package com.luke.clones.network.communication;

import java.util.Map;

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

public class StatsResponse {
	
	private ActionTypeStats actionTypeStats;
	private Map<StatsType, Long> statsMap;
	private long level;
	private long percentToNextLevel;

	public StatsResponse(){
		
	}

	public ActionTypeStats getActionTypeStats() {
		return actionTypeStats;
	}

	public void setActionTypeStats(ActionTypeStats actionTypeStats) {
		this.actionTypeStats = actionTypeStats;
	}

	public Map<StatsType, Long> getStatsMap() {
		return statsMap;
	}

	public void setStatsMap(Map<StatsType, Long> statsMap) {
		this.statsMap = statsMap;
	}

	public long getLevel() {
		return level;
	}

	public void setLevel(long level) {
		this.level = level;
	}

	public long getPercentToNextLevel() {
		return percentToNextLevel;
	}

	public void setPercentToNextLevel(long percentToNextLevel) {
		this.percentToNextLevel = percentToNextLevel;
	}



}
