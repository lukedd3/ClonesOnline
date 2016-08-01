package com.luke.clones.model.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luke.clones.model.type.FieldType;
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

public class FieldActor2 extends Actor{
	private Texture emptyTexture, solidTexture, ballTexture, backgroundTexture;
	private Color fieldColor;
	private Position position;
	private FieldType type;
	
	public FieldActor2(Texture emptyTexture, Texture solidTexture, Texture ballTexture, Texture backgroundTexture, Color color, Position position, FieldType fieldType){
		this.emptyTexture = emptyTexture;
		this.solidTexture = solidTexture;
		this.ballTexture = ballTexture;
		this.backgroundTexture = backgroundTexture;
		type = fieldType;
		if(color!=null)	fieldColor = color;
		this.position = position;
	}

//	@Override
//	public void draw(SpriteBatch sb, float parentAlpha) {
	@Override
	public void draw(Batch sb, float parentAlpha) {
		if(type == FieldType.EMPTY){
			if(fieldColor!=null) sb.setColor(fieldColor);
			else sb.setColor(Color.WHITE);
			sb.draw(emptyTexture, getX(), getY(), getWidth(), getHeight());
		}
		else if(type == FieldType.SOLID){
			if(fieldColor!=null) sb.setColor(fieldColor);
			else sb.setColor(Color.WHITE);
			sb.draw(solidTexture, getX(), getY(), getWidth(), getHeight());
		}
		else if(type == FieldType.TAKEN){
			sb.setColor(Color.WHITE);
			sb.draw(backgroundTexture, getX(), getY(), getWidth(), getHeight());
			if(fieldColor!=null) sb.setColor(fieldColor);
			sb.draw(ballTexture, getX(), getY(), getWidth(), getHeight());
		}
	}
	
	public Position getPosition(){
		return position;
	}
	
	public void setFieldType(FieldType fieldType){
		type = fieldType;
	}
	
	public FieldType getFieldType(){
		return type;
	}
	
	public void setColor(Color color){
		//if(color!=null)super.setColor(color);
		fieldColor = color;
	}
	
	@Override
	public void setColor(float r, float g, float b, float a) {
		fieldColor.set(r, g, b, a);
	}

	@Override
	public Color getColor() {
		return fieldColor;
	}
	
}
