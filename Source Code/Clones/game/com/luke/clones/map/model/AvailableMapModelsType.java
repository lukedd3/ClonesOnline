package com.luke.clones.map.model;

import com.luke.clones.map.model2p.ChessLikeMapModel;
import com.luke.clones.map.model2p.CrosswiseMapModel;
import com.luke.clones.map.model2p.DividedMapModel;
import com.luke.clones.map.model2p.InTheMiddleMapModel;
import com.luke.clones.map.model2p.MayhemMapModel;
import com.luke.clones.map.model2p.Test1MapModel;
import com.luke.clones.map.model2p.TunelRatsMapModel;
import com.luke.clones.map.model4p.EscapeMapModel;
import com.luke.clones.map.model4p.FourDirectionsMapModel;

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


public enum AvailableMapModelsType {
	//2P
//	ASYMMETRIC_TEST_1(new AsymmetricTest1MapModel()),
//	ASYMMETRIC_TEST_2(new AsymmetricTest2MapModel()),
//	END_TEST(new EndTestMapModel()),
//	END_TEST_2(new EndTest2MapModel()),
//	TEST1(new Test1MapModel()),
	TUNNEL_RATS(new TunelRatsMapModel()),
	IN_THE_MIDDLE(new InTheMiddleMapModel()),
	CHESS_LIKE(new ChessLikeMapModel()),
	CROSSWISE(new CrosswiseMapModel()),
//	DIVIDED(new DividedMapModel()),
//	MAYHEM(new MayhemMapModel()),
	
	//4P
//	END_TEST_FOR_FOUR(new EndTestForFourMapModel()),
	ESCAPE(new EscapeMapModel()),
	FOUR_DIRECTIONS(new FourDirectionsMapModel());
	
	private MapModel map;

	private AvailableMapModelsType(MapModel map) {
		this.map = map;
	}

	public MapModel getMap() {
		return map;
	}
	
}
