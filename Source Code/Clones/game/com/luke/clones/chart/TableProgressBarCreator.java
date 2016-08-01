package com.luke.clones.chart;

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

public class TableProgressBarCreator extends TableChartCreator{
	
	private int posX;
	private int posY;
	
	public void setPosition(int posX, int posY){
		this.posX = posX;
		this.posY = posY;
	}

	@Override
	public TableChart createWithPercentScale(int part1Percent, int part2Percent, int part3Percent){
		checkInitValues();
		
		int part1Width = calculatePartWidthWithPercentScale(part1Percent);
		int part2Width = calculatePartWidthWithPercentScale(part2Percent);
		int part3Width = calculatePartWidthWithPercentScale(part3Percent);
		
		part1Width=trimWidthIfTooWide(part1Width, part2Width, part3Width);

		createNewTableChartIfNull();
		
		drawParts(part1Width, part2Width, part3Width);
		
		determineTableChartPositionAndSize();
		
		return tableChart;
	}
	
	public TableChart createWithPerMilScale(int part1PerMil, int part2PerMil, int part3PerMil){
		checkInitValues();
		
		int part1Width = calculatePartWidthWithPerMilScale(part1PerMil);
		int part2Width = calculatePartWidthWithPerMilScale(part2PerMil);
		int part3Width = calculatePartWidthWithPerMilScale(part3PerMil);
		
		part1Width=trimWidthIfTooWide(part1Width, part2Width, part3Width);

		createNewTableChartIfNull();
		
		drawParts(part1Width, part2Width, part3Width);
		
		determineTableChartPositionAndSize();
		
		return tableChart;
	}
	
	private int calculatePartWidthWithPerMilScale(int partPerMil) {
		int partWidth = Math.round((float)partPerMil/1000f*(float)chartWidth);
		return partWidth;
	}

	private void createNewTableChartIfNull(){
		if(tableChart==null){
			tableChart=new TableChart();
		}
	}
	
	private void determineTableChartPositionAndSize(){
		tableChart.setPosition(posX, posY);
		tableChart.setSize(chartWidth, chartHeight);
	}
	
}
