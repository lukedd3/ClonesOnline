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

public class UpdateRoom {
	private ActionTypeUpdateRoom actionType;
	private RoomInfo[] roomInfoList;
	private MessageToShowType messageToShow;
	private AchievementType[] newAchievements;
	/**
	 * Used only when player successfully joins the room
	 */
	private RoomInfo roomInterior;
	
	public UpdateRoom(){
		
	}

	public UpdateRoom(ActionTypeUpdateRoom actionType, RoomInfo[] roomInfoList, RoomInfo roomInterior) {
		this.actionType = actionType;
		this.roomInfoList = roomInfoList;
		this.roomInterior = roomInterior;
	}

	public ActionTypeUpdateRoom getActionType() {
		return actionType;
	}

	public RoomInfo[] getRoomInfoList() {
		return roomInfoList;
	}

	public RoomInfo getRoomInterior() {
		return roomInterior;
	}

	public MessageToShowType getMessageToShow() {
		return messageToShow;
	}

	public void setMessageToShow(MessageToShowType messageToShow) {
		this.messageToShow = messageToShow;
	}

	public AchievementType[] getNewAchievements() {
		return newAchievements;
	}

	public void setNewAchievements(AchievementType[] newAchievements) {
		this.newAchievements = newAchievements;
	}
	
}
