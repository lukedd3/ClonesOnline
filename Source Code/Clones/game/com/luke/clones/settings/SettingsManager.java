package com.luke.clones.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

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

public class SettingsManager {
	private static final String SETTINGS_PREFERENCES_NAME = "SETTINGS";
	
	public static boolean getSettingValue(SettingType setting){
		Preferences prefs = Gdx.app.getPreferences(SETTINGS_PREFERENCES_NAME);
		boolean settingValue = prefs.getBoolean(setting.toString(), setting.getDefaultValue());
		return settingValue;
	}
	
	public static void setSettingValue(SettingType setting, boolean newValue){
		Preferences prefs = Gdx.app.getPreferences(SETTINGS_PREFERENCES_NAME);
		prefs.putBoolean(setting.toString(), newValue);
		prefs.flush();
	}
	
}
