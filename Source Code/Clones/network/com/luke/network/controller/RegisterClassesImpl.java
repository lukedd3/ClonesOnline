package com.luke.network.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.luke.clones.map.model.MapType;
import com.luke.clones.model.Map;
import com.luke.clones.model.PlayerScore;
import com.luke.clones.model.SingleStat;
import com.luke.clones.model.TurnStats;
import com.luke.clones.model.type.PlayerPosition;
import com.luke.clones.model.type.PlayerType;
import com.luke.clones.model.type.Position;
import com.luke.clones.network.communication.AchievementGenreType;
import com.luke.clones.network.communication.AchievementType;
import com.luke.clones.network.communication.AchievementsRequest;
import com.luke.clones.network.communication.AchievementsResponse;
import com.luke.clones.network.communication.ActionTypeBoard;
import com.luke.clones.network.communication.ActionTypeMove;
import com.luke.clones.network.communication.ActionTypeMoveRoom;
import com.luke.clones.network.communication.ActionTypeStats;
import com.luke.clones.network.communication.ActionTypeUpdateLogin;
import com.luke.clones.network.communication.ActionTypeUpdateRoom;
import com.luke.clones.network.communication.GameOver;
import com.luke.clones.network.communication.GameStart;
import com.luke.clones.network.communication.GetServerBackendInfo;
import com.luke.clones.network.communication.MessageShowRequest;
import com.luke.clones.network.communication.MessageToShowType;
import com.luke.clones.network.communication.MoveBoard;
import com.luke.clones.network.communication.MoveLogin;
import com.luke.clones.network.communication.MoveRoom;
import com.luke.clones.network.communication.PlayerMoveTimeLimit;
import com.luke.clones.network.communication.RoomCreateInfo;
import com.luke.clones.network.communication.RoomInfo;
import com.luke.clones.network.communication.RoomModifyInfo;
import com.luke.clones.network.communication.ServerBackendInfo;
import com.luke.clones.network.communication.StatsRequest;
import com.luke.clones.network.communication.StatsResponse;
import com.luke.clones.network.communication.StatsType;
import com.luke.clones.network.communication.Token;
import com.luke.clones.network.communication.UpdateBoard;
import com.luke.clones.network.communication.UpdateLogin;
import com.luke.clones.network.communication.UpdateRoom;
import com.luke.clones.network.interfaces.RegisterClasses;

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

public class RegisterClassesImpl implements RegisterClasses{
	
	//Classes whose objects server/client can send or receive
	@Override
	public void registerClasses(Kryo kryo) {
		kryo.register(GameStart.class);
		kryo.register(Token.class);
		kryo.register(Map.class);
		kryo.register(PlayerType.class);
		kryo.register(PlayerType[].class);
		kryo.register(PlayerPosition.class);
		kryo.register(PlayerPosition[].class);
		kryo.register(Position[].class);
		kryo.register(Position.class);
		kryo.register(MoveBoard.class);
		kryo.register(UpdateBoard.class);
		kryo.register(ActionTypeBoard.class);
		kryo.register(ActionTypeMove.class);
		kryo.register(GameOver.class);
		kryo.register(PlayerScore.class);
		kryo.register(PlayerScore[].class);
		kryo.register(TurnStats.class);
		kryo.register(SingleStat.class);
		kryo.register(SingleStat[].class);
		kryo.register(String.class);
		kryo.register(String[].class);
		kryo.register(RoomInfo.class);
		kryo.register(RoomInfo[].class);
		kryo.register(ActionTypeMoveRoom.class);
		kryo.register(ActionTypeUpdateRoom.class);
		kryo.register(MoveRoom.class);
		kryo.register(UpdateRoom.class);
		kryo.register(MoveLogin.class);
		kryo.register(UpdateLogin.class);
		kryo.register(ActionTypeUpdateLogin.class);
		kryo.register(RoomCreateInfo.class);
		kryo.register(GetServerBackendInfo.class);
		kryo.register(ServerBackendInfo.class);
		kryo.register(ActionTypeStats.class);
		kryo.register(StatsRequest.class);
		kryo.register(StatsResponse.class);
		kryo.register(StatsType.class);
		kryo.register(HashMap.class);
		kryo.register(PlayerMoveTimeLimit.class);
		kryo.register(MessageShowRequest.class);
		kryo.register(MessageToShowType.class);
		kryo.register(RoomModifyInfo.class);
		kryo.register(AchievementGenreType.class);
		kryo.register(AchievementType.class);
		kryo.register(AchievementType[].class);
		kryo.register(AchievementsRequest.class);
		kryo.register(AchievementsResponse.class);
		kryo.register(List.class);
		kryo.register(ArrayList.class);
		kryo.register(MapType.class);
	}
}
