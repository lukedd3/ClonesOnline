package com.luke.clones.model.button;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.SnapshotArray;
import com.luke.clones.config.Config;
import com.luke.clones.model.actor.HighlightActorAnimatedColorChange;
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

public class FlashingTextButton extends TextButton{
	private static final float DEFAULT_ALPHA = 1f;
	private static final float DEFAULT_ALPHA_DURATION = 1/17f;
	
	List<Float> alphas;
	int currentAlphaNumber = 0;
	private float singleAlphaDuration = DEFAULT_ALPHA_DURATION;
	
	private float elapsedTime = 0f;
	private float fieldAlpha = DEFAULT_ALPHA;
	
	private boolean flashing = false;
	
	List<HighlightActorAnimatedColorChange> highlightActorAnimatedColorChanges;
	
	public FlashingTextButton(String text, Skin skin) {
		super(text, skin);
	}

	private FlashingTextButton(String text, Skin skin, String styleName) {
		super(text, skin, styleName);
	}

	private FlashingTextButton(String text, TextButtonStyle style) {
		super(text, style);
	}
	
//	@Override
//	public void draw(SpriteBatch batch, float parentAlpha) {
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(flashing){
			elapsedTime += Gdx.graphics.getDeltaTime();
			if(elapsedTime>=singleAlphaDuration){
				fieldAlpha = choiceNextAlpha();
				elapsedTime-=singleAlphaDuration;
			}
			super.draw(batch, fieldAlpha);
		}
		else{
			super.draw(batch, parentAlpha);
		}
	}
	
	private static float choiceStartAlpha(List<Float> alphas){
		float startAlpha;
		if(alphas!=null && alphas.size()>0){
			startAlpha=alphas.get(0);
		}
		else{
			startAlpha = DEFAULT_ALPHA;
		}
		return startAlpha;
	}
	
	private float choiceNextAlpha(){
		currentAlphaNumber++;
		
		float nextAlpha;
		if(alphas==null || alphas.size()==0){
			return DEFAULT_ALPHA;
		}
		if(currentAlphaNumber>alphas.size()-1){
			currentAlphaNumber=0;
		}
		
		nextAlpha=alphas.get(currentAlphaNumber);
		
		return nextAlpha;
	}
	
	public void startFlashing(){
		fieldAlpha = choiceStartAlpha(alphas);
		currentAlphaNumber = 0;
		elapsedTime = 0;
		flashing = true;
	}
	
	public void stopFlashing(){
		flashing = false;
	}

	public List<Float> getAlphas() {
		return alphas;
	}

	public void setAlphas(List<Float> alphas) {
		this.alphas = alphas;
	}

}
