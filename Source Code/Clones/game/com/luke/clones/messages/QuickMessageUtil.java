package com.luke.clones.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.luke.clones.network.communication.AchievementGenreType;
import com.luke.clones.network.communication.AchievementType;
import com.luke.clones.screen.StartScreen;

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

public class QuickMessageUtil {
	public static List<QuickMessage> prepareQuickMessagesWithNewAchievements(AchievementType[] newAchievements, Skin skin, Skin smallSkin, StartScreen startScreen){
		List<QuickMessage> quickMessagesAboutAchievements = new ArrayList<QuickMessage>();
		for(AchievementType achievement: newAchievements){
			QuickMessage quickMessageAboutAchievement = new QuickMessage(achievement.getTitle(), skin, smallSkin);
			quickMessageAboutAchievement.setOptionalTitle("Achievement Unlocked");
			quickMessageAboutAchievement.setOptionalAdditionalText("["+achievement.getDescription()+"]");
			QuickMessageType quickMessageType = convertAchievementGenreTypeToQuickMessageType(achievement);
			if(quickMessageType!=null) quickMessageAboutAchievement.setQuickMessageType(quickMessageType);
			quickMessageAboutAchievement.setAutoHide(false);
			if(achievement.isShowRateItButton()){
				quickMessageAboutAchievement.setShowRateContent(true, startScreen);
			}
			quickMessageAboutAchievement.setConfirmButtonText("Close");
			quickMessagesAboutAchievements.add(quickMessageAboutAchievement);
		}
		Collections.reverse(quickMessagesAboutAchievements);
		return quickMessagesAboutAchievements;
	}
	
	private static QuickMessageType convertAchievementGenreTypeToQuickMessageType(AchievementType achievementType){
		if(achievementType.getAchievementGenreType()==AchievementGenreType.GAMES_PLAYED){
			return QuickMessageType.ACHIEVEMENT_GAMES_PLAYED;
		}
		else if(achievementType.getAchievementGenreType()==AchievementGenreType.GAMES_WON){
			return QuickMessageType.ACHIEVEMENT_GAMES_WON;
		}
		else if(achievementType.getAchievementGenreType()==AchievementGenreType.GAMES_WON_IN_ROW){
			return QuickMessageType.ACHIEVEMENT_GAMES_WON_IN_ROW;
		}
		else if(achievementType.getAchievementGenreType()==AchievementGenreType.PLAY_TIME){
			return QuickMessageType.ACHIEVEMENT_PLAY_TIME;
		}
		return null;
	}
}
