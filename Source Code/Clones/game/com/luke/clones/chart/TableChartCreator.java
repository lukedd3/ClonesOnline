package com.luke.clones.chart;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

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

public class TableChartCreator {
	
	protected int chartWidth;
	protected int chartHeight;

	private Texture part1Texture;
	private Texture part2Texture;
	private Texture part3Texture;
	
	protected TableChart tableChart;
	
	public TableChartCreator() {
		
	}
	
	public void setChartSize(int chartWidth, int chartHeight) {
		this.chartWidth = chartWidth;
		this.chartHeight = chartHeight;
	}
	
	public void setTextures(Texture part1Texture, Texture part2Texture, Texture part3Texture){
		this.part1Texture = part1Texture;
		this.part2Texture = part2Texture;
		this.part3Texture = part3Texture;
	}
	
	public TableChart createWithPercentScale(int part1Percent, int part2Percent, int part3Percent){
		part1Percent = preventPercentValueFromBeingNegativeOrBiggerThan100(part1Percent);
		part2Percent = preventPercentValueFromBeingNegativeOrBiggerThan100(part2Percent);
		part3Percent = preventPercentValueFromBeingNegativeOrBiggerThan100(part3Percent);
		
		checkInitValues();
		
		int part1Width = calculatePartWidthWithPercentScale(part1Percent);
		int part2Width = calculatePartWidthWithPercentScale(part2Percent);
		int part3Width = calculatePartWidthWithPercentScale(part3Percent);
		
		part1Width=trimWidthIfTooWide(part1Width, part2Width, part3Width);

		createNewTableChart();
		
		drawParts(part1Width, part2Width, part3Width);
		
		return tableChart;
	}
	
	private int preventPercentValueFromBeingNegativeOrBiggerThan100(int percentValue){
		percentValue = Math.abs(percentValue);
		if(percentValue>100) percentValue = 100;
		return percentValue;
	}
	
	protected void checkInitValues(){
		boolean texturesNotInitialized = part1Texture==null || part2Texture==null || part3Texture==null;
		boolean chartSizeNotInitialized = chartWidth<=0 || chartHeight<=0;
		if(texturesNotInitialized || chartSizeNotInitialized){
			throw new ChartCreatorNotInitializedException();
		}
	}
	
	protected int calculatePartWidthWithPercentScale(int partPercent){
		int partWidth = Math.round((float)partPercent/100f*(float)chartWidth);
		return partWidth;
	}
	
	protected int trimWidthIfTooWide(int part1Width, int part2Width, int part3Width){
		int widthDifference = chartWidth - (part1Width+part2Width+part3Width);
		part1Width+=widthDifference;
		return part1Width;
	}
	
	private void createNewTableChart(){
		tableChart = new TableChart();
	}
	
	protected void drawParts(int part1Width, int part2Width, int part3Width){
		final TextureRegion part1TextureRegion = new TextureRegion(part1Texture, 0, 0, part1Width, chartHeight);
		final TextureRegion part2TextureRegion = new TextureRegion(part2Texture, 0, 0, part2Width, chartHeight);
		final TextureRegion part3TextureRegion = new TextureRegion(part3Texture, 0, 0, part3Width, chartHeight);
		
		tableChart.clear();
		
		Image part1Image = new Image(part1TextureRegion);
		Image part2Image = new Image(part2TextureRegion);
		Image part3Image = new Image(part3TextureRegion);
		
		tableChart.add(part1Image);
		tableChart.add(part2Image);
		tableChart.add(part3Image);
	}

}
