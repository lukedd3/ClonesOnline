package com.luke.clones.network.communication;

import java.util.ArrayList;
import java.util.List;

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

public enum AchievementType {
	
	GAMES_WON_10("You are a winner", AchievementGenreType.GAMES_WON, 10, false),
	GAMES_WON_50("Nice", AchievementGenreType.GAMES_WON, 50),
	GAMES_WON_100("Ultra", AchievementGenreType.GAMES_WON, 100),
	GAMES_WON_300("This is Sparta!", AchievementGenreType.GAMES_WON, 300),
	GAMES_WON_500("Winning Mania", AchievementGenreType.GAMES_WON, 500),
	GAMES_WON_1000("Great", AchievementGenreType.GAMES_WON, 1000),
	GAMES_WON_5000("You rule", AchievementGenreType.GAMES_WON, 5000),
	GAMES_WON_10000("Clones Master", AchievementGenreType.GAMES_WON, 10000),

	GAMES_PLAYED_15("I'll give it a try", AchievementGenreType.GAMES_PLAYED, 15, false),
	GAMES_PLAYED_50("I like it", AchievementGenreType.GAMES_PLAYED, 50),
	GAMES_PLAYED_128("Round number", AchievementGenreType.GAMES_PLAYED, 128),
	GAMES_PLAYED_250("125+125=", AchievementGenreType.GAMES_PLAYED, 250),
	GAMES_PLAYED_500("Indianapolis", AchievementGenreType.GAMES_PLAYED, 500),
	GAMES_PLAYED_2000("Addiction", AchievementGenreType.GAMES_PLAYED, 2000),
	GAMES_PLAYED_5000("Maniac", AchievementGenreType.GAMES_PLAYED, 5000),
	GAMES_PLAYED_10000("Master", AchievementGenreType.GAMES_PLAYED, 10000),

	PLAY_TIME_1("Time killer", AchievementGenreType.PLAY_TIME, 1, false),
	PLAY_TIME_8("Office worker", AchievementGenreType.PLAY_TIME, 8),
	PLAY_TIME_24("Like a clock", AchievementGenreType.PLAY_TIME, 24),
	PLAY_TIME_72("Long weekend", AchievementGenreType.PLAY_TIME, 72),
	PLAY_TIME_128("Power of two", AchievementGenreType.PLAY_TIME, 128),
	PLAY_TIME_256("Till death", AchievementGenreType.PLAY_TIME, 256),
	PLAY_TIME_512("There is a beautiful world outside", AchievementGenreType.PLAY_TIME, 512),
	PLAY_TIME_1024("No life 4 ever", AchievementGenreType.PLAY_TIME, 1024),

	GAMES_WON_IN_ROW_3("Third time lucky", AchievementGenreType.GAMES_WON_IN_ROW, 3, false),
	GAMES_WON_IN_ROW_5("Winning fury", AchievementGenreType.GAMES_WON_IN_ROW, 5, false),
	GAMES_WON_IN_ROW_10("Masterpiece", AchievementGenreType.GAMES_WON_IN_ROW, 10),
	GAMES_WON_IN_ROW_50("Man of Steel", AchievementGenreType.GAMES_WON_IN_ROW, 50),
	GAMES_WON_IN_ROW_100("Yoda", AchievementGenreType.GAMES_WON_IN_ROW, 100);
	
	private AchievementGenreType achievementGenreType;
	private int value;
	private String title;
	private boolean showRateItButton;
	
	private AchievementType(String title, AchievementGenreType achievementGenreType, int value) {
		this.title = title;
		this.achievementGenreType = achievementGenreType;
		this.value = value;
		this.showRateItButton = true;
	}
	
	private AchievementType(String title, AchievementGenreType achievementGenreType, int value, boolean showRateItButton) {
		this.title = title;
		this.achievementGenreType = achievementGenreType;
		this.value = value;
		this.showRateItButton = showRateItButton;
	}

	public AchievementGenreType getAchievementGenreType() {
		return achievementGenreType;
	}

	public int getValue() {
		return value;
	}
	
	public String getTitle() {
		return title;
	}

	public boolean isShowRateItButton() {
		return showRateItButton;
	}

	public String getDescription(){
		if(achievementGenreType==AchievementGenreType.PLAY_TIME){
			if(value==1){
				return "Play Clones for "+value+" hour";
			}
			else{
				return "Play Clones for "+value+" hours";
			}
		}
		else if(achievementGenreType==AchievementGenreType.GAMES_PLAYED){
			return "Play "+value+" games";
		}
		else if(achievementGenreType==AchievementGenreType.GAMES_WON){
			return "Win "+value+" games";
		}
		else if(achievementGenreType==AchievementGenreType.GAMES_WON_IN_ROW){
			return "Win "+value+" games in a row";
		}
		else{
			return "";
		}
	}
	
	public static AchievementType[] valuesByGenre(AchievementGenreType achievementGenreType){
		List<AchievementType> selectedAchievements = new ArrayList<AchievementType>();
		for(AchievementType achievementType: AchievementType.values()){
			if(achievementType.getAchievementGenreType()==achievementGenreType){
				selectedAchievements.add(achievementType);
			}
		}
		return selectedAchievements.toArray(new AchievementType[0]);
	}
	
	}
