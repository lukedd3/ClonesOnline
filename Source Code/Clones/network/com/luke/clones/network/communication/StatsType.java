package com.luke.clones.network.communication;

/* The MIT License (MIT)

Copyright (c) 2016 �ukasz Dziak

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

public enum StatsType {
	PLAYED_2P_GAMES("Played 2 player games"),
	WON_2P_GAMES("Won 2 player games"),
	LOST_2P_GAMES("Lost 2 player games"),
	PLAYED_4P_GAMES("Played 4 player games"),
	WON_4P_GAMES("Won 4 player games"),
	LOST_4P_GAMES("Lost 4 player games"),
	LAST_WON_IN_ROW("Last won in row"),
	BIGGEST_WON_IN_ROW("Biggest won in row"),
	TOTAL_BALLS_SCORED("Total balls scored"),
	TOTAL_PLAY_TIME("Total play time");
	
	private String description;
	
	private StatsType(String description){
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}