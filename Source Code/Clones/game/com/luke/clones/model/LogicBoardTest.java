package com.luke.clones.model;

import junit.framework.Assert;

import org.junit.Test;

import com.luke.clones.model.type.PlayerType;

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

public class LogicBoardTest {

	@Test
	public void testCreate() {
		LogicBoard board = new LogicBoard(15, 30);
		Assert.assertNotNull(board);
	}

	@Test
	public void basicTest(){
		LogicBoard board = new LogicBoard(15, 30);
		Assert.assertNotNull(board);
		board.setFieldPlayer(2, 3, PlayerType.GREEN);
		board.setFieldPlayer(board.getWidth()-1, board.getHeight()-1, PlayerType.RED);
		
		boolean testB;
		
		testB = board.isMovePossible(2, 3, 1, 2);
		Assert.assertEquals(true, testB);
		testB = board.isMovePossible(2, 3, 1, 4);
		Assert.assertEquals(true, testB);
		testB = board.isMovePossible(2, 3, 1, 3);
		Assert.assertEquals(true, testB);
		
		testB = board.isClonePossible(2, 3, 3, 2);
		Assert.assertEquals(true, testB);
		testB = board.isClonePossible(2, 3, 3, 3);
		Assert.assertEquals(true, testB);
		testB = board.isClonePossible(2, 3, 3, 4);
		Assert.assertEquals(true, testB);
		testB = board.isClonePossible(2, 3, 2, 2);
		Assert.assertEquals(true, testB);
		testB = board.isClonePossible(2, 3, 1, 2);
		Assert.assertEquals(true, testB);
		testB = board.isClonePossible(2, 3, 2, 4);
		Assert.assertEquals(true, testB);
		testB = board.isClonePossible(2, 3, 1, 4);
		Assert.assertEquals(true, testB);
		testB = board.isClonePossible(2, 3, 1, 3);
		Assert.assertEquals(true, testB);
		
		testB = board.isJumpPossible(2, 3, 5, 4);
		Assert.assertEquals(false, testB);
		
		
		
	}
}
