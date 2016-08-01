package com.luke.clones.screen;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.luke.clones.android.AndroidNativeHandler;
import com.luke.clones.messages.QuickMessage;
import com.luke.clones.messages.QuickMessageManager;
import com.luke.clones.model.Map;
import com.luke.clones.model.type.PlayerMoveTimeLimitType;
import com.luke.clones.music.MusicManager;
import com.luke.clones.network.backstage.ServerFrontendInfo;
import com.luke.clones.network.communication.GameStart;
import com.luke.network.controller.ClonesClient;

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

public class StartScreen extends Game {
	private AndroidNativeHandler androidNativeHandler;
	private MusicManager musicManager;

	private ScreenLocalMultiplayerGame screenLocalMultiplayerGame;
	private ScreenMainMenu screenMainMenu;
	private ScreenLocalMultiplayer screenLocalMultiplayer;
	private ScreenOnline screenOnline;
	private ScreenOnlineServerSelection screenOnlineServerSelection;
	private ScreenOnlineLogin screenOnlineLogin;
	private ScreenOnlineGame screenOnlineGame;
	private ScreenAbout screenAbout;
	private ScreenHowToPlay screenHowToPlay;
	private ScreenSettings screenSettings;
	private ScreenMapCreator screenMapCreator;
	private ScreenReportBug screenReportBug;
	private ScreenTestTools screenTestTools;
	private ScreenReviewBeta screenReviewBeta;

	public StartScreen(AndroidNativeHandler androidNativeHandler) {
		this.androidNativeHandler = androidNativeHandler;
	}

	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);

		screenMainMenu = new ScreenMainMenu(this);
		screenLocalMultiplayer = new ScreenLocalMultiplayer(this);
		screenLocalMultiplayerGame = new ScreenLocalMultiplayerGame(this);
		screenOnline = new ScreenOnline(this);
		screenOnlineServerSelection = new ScreenOnlineServerSelection(this);
		screenOnlineLogin = new ScreenOnlineLogin(this);
		screenOnlineGame = new ScreenOnlineGame(this);
		screenAbout = new ScreenAbout(this);
		screenHowToPlay = new ScreenHowToPlay(this);
		screenReportBug = new ScreenReportBug(this);
		screenSettings = new ScreenSettings(this);
		screenMapCreator = new ScreenMapCreator(this);
		screenTestTools = new ScreenTestTools(this);
		screenReviewBeta = new ScreenReviewBeta(this);

		musicManager = MusicManager.INSTANCE;
		
		musicManager.pauseAndScheduleGradualVolumeIncreaseBeforePlay();//only to make gradual volume increase at start
		
		setToMainMenu();
	}

	public AndroidNativeHandler getAndroidNativeHandler() {
		return androidNativeHandler;
	}

	protected MusicManager getMusicManager() {
		return musicManager;
	}

	public void setToLocalMultiplayerGame(Map map,
			PlayerMoveTimeLimitType playerMoveTimeLimitType) {
		screenLocalMultiplayerGame.newGame(map, playerMoveTimeLimitType);
		setScreenAndPerformAdditionalActionsAndPauseMusic(screenLocalMultiplayerGame);
	}

	public void setToLocalMultiplayer() {
		setScreenAndPerformAdditionalActions(screenLocalMultiplayer);
	}

	public void setToMainMenu() {
		setScreenAndPerformAdditionalActions(screenMainMenu);
	}

	public void setToMainMenuWithMessage(QuickMessage quickMessage) {
		setToMainMenu();
		QuickMessageManager.showMessageOnSpecifiedStage(quickMessage,
				screenMainMenu.getStageForQuickMessage());
	}

	public void setToOnline(ClonesClient clonesClient, String playerName) {
		screenOnline.initialize(clonesClient, playerName);
		setScreenAndPerformAdditionalActions(screenOnline);
	}

	public void setToOnlineWithoutName(ClonesClient clonesClient,
			boolean switchToRoomInterior) {
		screenOnline.initializeWithoutName(clonesClient, switchToRoomInterior);
		setScreenAndPerformAdditionalActions(screenOnline);
	}

	public void setToOnlineWithoutNameWithMessage(ClonesClient clonesClient,
			boolean switchToRoomInterior, QuickMessage quickMessage) {
		setToOnlineWithoutName(clonesClient, switchToRoomInterior);
		QuickMessageManager.showMessageOnSpecifiedStage(quickMessage,
				screenOnline.getStageForQuickMessage());
	}

	public void setToOnlineWithoutNameWithMultipleMessages(
			ClonesClient clonesClient, boolean switchToRoomInterior,
			List<QuickMessage> quickMessages) {
		setToOnlineWithoutName(clonesClient, switchToRoomInterior);
		for (QuickMessage quickMessage : quickMessages) {
			QuickMessageManager.showMessageOnSpecifiedStage(quickMessage,
					screenOnline.getStageForQuickMessage());
		}
	}

	public void setToOnlineServerSelection() {
		setScreenAndPerformAdditionalActions(screenOnlineServerSelection);
	}

	public void setToOnlineLogin(ServerFrontendInfo serverInfo) {
		screenOnlineLogin.setServerInfo(serverInfo);
		setScreenAndPerformAdditionalActions(screenOnlineLogin);
	}

	public void setToOnlineGame(ClonesClient clonesClient, GameStart gameStart,
			String playerName) {
		screenOnlineGame.newGame(clonesClient, gameStart, playerName);
		setScreenAndPerformAdditionalActionsAndPauseMusic(screenOnlineGame);
	}

	public void setToHowToPlay() {
		setScreenAndPerformAdditionalActions(screenHowToPlay);
	}

	public void setToSettings() {
		setScreenAndPerformAdditionalActions(screenSettings);
	}

	public void setToMapCreator() {
		setScreenAndPerformAdditionalActions(screenMapCreator);
	}

	public void setToAbout() {
		setScreenAndPerformAdditionalActions(screenAbout);
	}

	public void setToReportBug() {
		setScreenAndPerformAdditionalActions(screenReportBug);
	}

	public void setToTestTools() {
		setScreenAndPerformAdditionalActions(screenTestTools);
	}

	public void setToReviewBeta() {
		setScreenAndPerformAdditionalActions(screenReviewBeta);
	}
	
	private void setScreenAndPerformAdditionalActionsAndPauseMusic(Screen screen) {
		setScreenAndPerformAdditionalActions(screen);
		pauseMusic();
	}

	private void setScreenAndPerformAdditionalActions(Screen screen) {
		setScreen(screen);
		hideSystemButtonBar();
		playMusic();
	}
	
	private void hideSystemButtonBar(){
		Gdx.app.postRunnable(new Runnable() {
			public void run() {
				androidNativeHandler.hideSystemButtonBar();
			}
		});
	}
	
	private void playMusic(){
		musicManager.play();
	}
	
	private void pauseMusic(){
		musicManager.pauseAndScheduleGradualVolumeIncreaseBeforePlay();
	}

}
