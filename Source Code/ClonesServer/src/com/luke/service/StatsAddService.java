package com.luke.service;

import org.apache.log4j.Logger;

import com.luke.clones.network.communication.StatsType;
import com.luke.dao.StatsDAO;
import com.luke.dao.UsersDAO;
import com.luke.dto.GeneralStatsDTO;
import com.luke.dto.GeneralStatsTypeDTO;
import com.luke.dto.UserDTO;
import com.luke.type.GameResult;
import com.luke.type.GameType;
import com.luke.type.SuddenlyInterruptedGameResult;
import com.luke.type.WinLoseType;

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

public class StatsAddService {
	public static final StatsAddService INSTANCE = new StatsAddService();
	
	private Logger log = Logger.getLogger(StatsAddService.class);
	
	private StatsDAO statsDAO = StatsDAO.INSTANCE;
	private UsersDAO usersDAO = UsersDAO.INSTANCE;
	private StatsGetService statsGetService = StatsGetService.INSTANCE;
	
	private StatsAddService(){
		
	}
	
	public void addStartedGame(String userGoogleId, GameType gameType){
		log.info("Adding start game stats for user with google_id "+userGoogleId);
		StatsType statsType = null;
		if(gameType==GameType._2P){
			statsType = StatsType.PLAYED_2P_GAMES;
		}
		else if(gameType==GameType._4P){
			statsType = StatsType.PLAYED_4P_GAMES;
		}
		addToStatisticValue(userGoogleId, 1, statsType);
		log.info("Successfully added start game stats for user with google_id "+userGoogleId);
	}
	
	public void addFinishedGameResult(String userGoogleId, GameType gameType, GameResult gameResult){
		log.info("Adding finished game result stats for user with google_id "+userGoogleId);
		
		StatsType wonLostNumberOfPlayersStat = gameTypeAndGameResultToWonLostNumberOfPlayersStatType(gameType, gameResult);
		addToStatisticValue(userGoogleId, 1, wonLostNumberOfPlayersStat);
		
		if(gameResult.getWinLose()==WinLoseType.WIN){
			addToStatisticValue(userGoogleId, 1, StatsType.LAST_WON_IN_ROW);
		}
		else if(gameResult.getWinLose()==WinLoseType.LOSE){
			resetStatisticValueToZero(userGoogleId, StatsType.LAST_WON_IN_ROW);
		}
		else{
			throw new IllegalArgumentException();
		}
		
		long currentLastWonInRow = statsGetService.getGeneralStatValue(userGoogleId, StatsType.LAST_WON_IN_ROW);
		long currentBiggestWonInRow = statsGetService.getGeneralStatValue(userGoogleId, StatsType.BIGGEST_WON_IN_ROW);
		
		if(currentLastWonInRow>currentBiggestWonInRow){
			replaceStatisticValue(userGoogleId, currentLastWonInRow, StatsType.BIGGEST_WON_IN_ROW);
		}
		
		addToStatisticValue(userGoogleId, gameResult.getBallsScored(), StatsType.TOTAL_BALLS_SCORED);
		
		addToStatisticValue(userGoogleId, gameResult.getPlayTimeInSeconds(), StatsType.TOTAL_PLAY_TIME);
		
		log.info("Successfully added finished game result stats for user with google_id "+userGoogleId);
	}
	
	public void addSuddenlyInterruptedGameResult(String userGoogleId, GameType gameType, SuddenlyInterruptedGameResult suddenlyInterruptedGameResult){
		log.info("Adding suddenly interrupted game result stats for user with google_id "+userGoogleId);
		
		addToStatisticValue(userGoogleId, suddenlyInterruptedGameResult.getPlayTimeInSeconds(), StatsType.TOTAL_PLAY_TIME);
		
		log.info("Successfully added suddenly interrupted game result stats for user with google_id "+userGoogleId);
	}
	
	private void addToStatisticValue(String userGoogleId, long addedValue, StatsType statsType){
		UserDTO userDTO = usersDAO.getUserByGoogleId(userGoogleId);
		GeneralStatsTypeDTO generalStatsTypeDTO = statsDAO.getStatisticTypeByStatsType(statsType);
		
		long idToSave = 0;
		long currentValue=0;
		
		GeneralStatsDTO currentGeneralStatsDTO = statsDAO.getGeneralStatsByUserAndGeneralStatsType(userDTO.getId(), generalStatsTypeDTO.getId());
		
		if(currentGeneralStatsDTO!=null){
			idToSave = currentGeneralStatsDTO.getId();
			currentValue = currentGeneralStatsDTO.getValue();
		}
		
		
		long valueToSave = currentValue + addedValue;
		GeneralStatsDTO newGeneralStatsDTO = new GeneralStatsDTO(idToSave, valueToSave, generalStatsTypeDTO.getId(), userDTO.getId());
		statsDAO.saveOrUpdateStatisticValue(newGeneralStatsDTO);	
	}
	
	private void resetStatisticValueToZero(String userGooogleId, StatsType statsType){
		replaceStatisticValue(userGooogleId, 0, statsType);
	}
	
	private void replaceStatisticValue(String userGooogleId, long value, StatsType statsType){
		UserDTO userDTO = usersDAO.getUserByGoogleId(userGooogleId);
		GeneralStatsTypeDTO generalStatsTypeDTO = statsDAO.getStatisticTypeByStatsType(statsType);
		
		long idToSave = 0;
		
		GeneralStatsDTO currentGeneralStatsDTO = statsDAO.getGeneralStatsByUserAndGeneralStatsType(userDTO.getId(), generalStatsTypeDTO.getId());
		
		if(currentGeneralStatsDTO!=null){
			idToSave = currentGeneralStatsDTO.getId();
		}
		
		
		long valueToSave = value;
		GeneralStatsDTO newGeneralStatsDTO = new GeneralStatsDTO(idToSave, valueToSave, generalStatsTypeDTO.getId(), userDTO.getId());
		statsDAO.saveOrUpdateStatisticValue(newGeneralStatsDTO);	
	}
	
	private StatsType gameTypeAndGameResultToWonLostNumberOfPlayersStatType(GameType gameType, GameResult gameResult){
		if(gameResult.getWinLose()==WinLoseType.WIN){
			if(gameType==GameType._2P){
				return StatsType.WON_2P_GAMES;
			}
			else if(gameType==GameType._4P){
				return StatsType.WON_4P_GAMES;
			}
		}
		else if(gameResult.getWinLose()==WinLoseType.LOSE){
			if(gameType==GameType._2P){
				return StatsType.LOST_2P_GAMES;
			}
			else if(gameType==GameType._4P){
				return StatsType.LOST_4P_GAMES;
			}
		}
		
		throw new IllegalArgumentException();
	}
	
}
