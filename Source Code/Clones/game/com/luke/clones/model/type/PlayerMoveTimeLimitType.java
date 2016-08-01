package com.luke.clones.model.type;

import com.luke.clones.network.communication.PlayerMoveTimeLimit;

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

public enum PlayerMoveTimeLimitType {
	T5S("5s", new PlayerMoveTimeLimit(false, 5)),
	T10S("10s", new PlayerMoveTimeLimit(false, 10)),
	T15S("15s (default)", new PlayerMoveTimeLimit(false, 15)),
	T30S("30s", new PlayerMoveTimeLimit(false, 30)),
	T45S("45s", new PlayerMoveTimeLimit(false, 45)),
	T60S("60s", new PlayerMoveTimeLimit(false, 60)),
	NO_LIMIT("No limit", new PlayerMoveTimeLimit(true, -1));
	
	private String description;
	private PlayerMoveTimeLimit playerMoveTimeLimit;
	
	private PlayerMoveTimeLimitType(String description, PlayerMoveTimeLimit playerMoveTimeLimit){
		this.description = description;
		this.playerMoveTimeLimit = playerMoveTimeLimit;
	}

	public String getDescription() {
		return description;
	}

	public PlayerMoveTimeLimit getPlayerMoveTimeLimit() {
		return playerMoveTimeLimit;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
	public static PlayerMoveTimeLimitType fromPlayerMoveTimeLimit(PlayerMoveTimeLimit playerMoveTimeLimit){
		for(PlayerMoveTimeLimitType playerMoveTimeLimitType : PlayerMoveTimeLimitType.values()){
			if(playerMoveTimeLimitType.getPlayerMoveTimeLimit().equals(playerMoveTimeLimit)){
				return playerMoveTimeLimitType;
			}
		}
		return null;
	}
	
}
