package com.luke.clones.config;

import com.badlogic.gdx.Gdx;

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

public class ConfigUtils {

	public static int getNextRoundAutoStartTimeDependingOnNumberOfPlayers(int numberOfPlayers){
		if(numberOfPlayers==4){
			return Config.nextRoundAutoStartTimeFor4PlayerGameInSeconds;
		}
		else{
			return Config.nextRoundAutoStartTimeFor2PlayerGameInSeconds;
		}
	}
	
	public static int getScreenWidth(){
		int deviceOriginalScreenWidth = Gdx.graphics.getWidth();
		int deviceOriginalScreenHeight = Gdx.graphics.getHeight();
		
		ScreenRatioType screenRatioType = getScreenRatio();
		
		int widthToReturn = deviceOriginalScreenWidth;
		if(deviceOriginalScreenHeight>1280){
			if(screenRatioType==ScreenRatioType.SR_16_TO_9){
				widthToReturn = 720;
			}
			if(screenRatioType==ScreenRatioType.SR_16_TO_10){
				widthToReturn = 800;
			}
			if(screenRatioType==ScreenRatioType.SR_5_TO_3){
				widthToReturn = 768;
			}
			if(screenRatioType==ScreenRatioType.SR_4_TO_3){
				widthToReturn = 960;
			}
			if(screenRatioType==ScreenRatioType.SR_3_TO_2){
				widthToReturn = 854;
			}
		}
//		if(deviceOriginalScreenHeight>1280 && screenRatioType==ScreenRatioType.SR_16_TO_9){//&& ratio==1.7
//			widthToReturn = 720;
//		}
		//test only
//		if(deviceOriginalScreenHeight>854 && screenRatioType==ScreenRatioType.SR_16_TO_9){//&& ratio==1.7
//			widthToReturn = 480;
//		}
		return widthToReturn;
	}
	
	public static int getScreenHeight(){
//		int deviceOriginalScreenWidth = Gdx.graphics.getWidth();
		int deviceOriginalScreenHeight = Gdx.graphics.getHeight();
		
		System.out.println("width, height"+Gdx.graphics.getWidth()+" "+Gdx.graphics.getHeight());
		
		ScreenRatioType screenRatioType = getScreenRatio();
		System.out.println("Ratio enum: "+screenRatioType);
		
		int heightToReturn = deviceOriginalScreenHeight;
		if(deviceOriginalScreenHeight>1280){
			if(screenRatioType==ScreenRatioType.SR_16_TO_9){
				heightToReturn = 1280;
			}
			else if(screenRatioType==ScreenRatioType.SR_16_TO_10){
				heightToReturn = 1280;
			}
			else if(screenRatioType==ScreenRatioType.SR_5_TO_3){
				heightToReturn = 1280;
			}
			else if(screenRatioType==ScreenRatioType.SR_4_TO_3){
				heightToReturn = 1280;
			}
			else if(screenRatioType==ScreenRatioType.SR_3_TO_2){
				heightToReturn = 1280;
			}
		}
//		if(deviceOriginalScreenHeight>1280 && screenRatioType==ScreenRatioType.SR_16_TO_9){//&& ratio==1.7
//			heightToReturn = 1280;
//		}
		//test only
//		if(deviceOriginalScreenHeight>854 && screenRatioType==ScreenRatioType.SR_16_TO_9){//&& ratio==1.7
//			heightToReturn = 854;
//		}
		return heightToReturn;
	}
	
	private static ScreenRatioType getScreenRatio(){
		int deviceOriginalScreenWidth = Gdx.graphics.getWidth();
		int deviceOriginalScreenHeight = Gdx.graphics.getHeight();
		
		float ratio = (float)deviceOriginalScreenHeight/(float)deviceOriginalScreenWidth;
		System.out.println("ratio: "+ratio);
		if(Math.abs(1.777-ratio)<0.01){
			return ScreenRatioType.SR_16_TO_9;
		}
		else if(Math.abs(1.6-ratio)<0.01){
			return ScreenRatioType.SR_16_TO_10;
		}
		else if(Math.abs(1.666-ratio)<0.01){
			return ScreenRatioType.SR_5_TO_3;
		}
		else if(Math.abs(1.333-ratio)<0.01){
			return ScreenRatioType.SR_4_TO_3;
		}
		else if(Math.abs(1.5-ratio)<0.01){
			return ScreenRatioType.SR_3_TO_2;
		}
		
		return ScreenRatioType.UNKNOWN;
	}
	
}