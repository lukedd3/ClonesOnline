package com.luke.clones.network.communication;

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

/**
 * From server to client
 */
public class UpdateBoard {
	
	/**
	 * Clone, jump etc.
	 */
	private ActionTypeBoard actionType;
	/**
	/*For which player clone, jump or another action occurs
	/*Player type, player X, and player Y
	 */
	private PlayerPosition playerPosition;
	/**
	/*Target of clone, jump or another action
	/*field X, field Y
	 */
	private Position position;
	
	public UpdateBoard(){
		
	}

	/**
	 * @param actionType
	 * @param playerPosition
	 * @param position
	 */
	public UpdateBoard(ActionTypeBoard actionType,PlayerPosition playerPosition, Position position) {
		this.actionType = actionType;
		this.playerPosition = playerPosition;
		this.position = position;
	}

	public ActionTypeBoard getActionType() {
		return actionType;
	}

	public PlayerPosition getPlayerPosition() {
		return playerPosition;
	}

	public Position getPosition() {
		return position;
	}
	
}
