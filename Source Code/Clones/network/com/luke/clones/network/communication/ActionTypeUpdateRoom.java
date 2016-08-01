package com.luke.clones.network.communication;

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

public enum ActionTypeUpdateRoom {
	/**
	 * Pure room list so player can see which he can join
	 */
	ROOM_LIST,
	/**
	 * When player joins the room room interior (lounge) is showed
	 */
	ROOM_INTERIOR,
	/**
	 * Room no longer exist so player can't join it
	 */
	NOT_EXIST,
	/**
	 * Room is full so player can't join it
	 */
	IS_FULL,
	/**
	 * Player can't create room with that name because it is already taken
	 */
	NAME_ALREADY_TAKEN,
	/**
	 * Player can't create room with than name because this name is forbidden (not matching regex)
	 */
	FORBIDDEN_NAME,
	/**
	 * Player successfully creates the room
	 */
	CREATE_SUCCESS,
	/**
	 * Player successfully enters the room
	 */
	ENTER_SUCCESS,
	/**
	 * When game is stopped for some reason
	 */
	BACK_TO_ROOM_INTERIOR,
	/**
	 * When admin leaves (close) the room
	 */
	BACK_TO_ROOM_LIST,
	/**
	 * When admin requests for room modify
	 */
	MODIFY_REQUEST_RESPONSE
}
