package com.luke.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.luke.clones.network.communication.AchievementGenreType;
import com.luke.clones.network.communication.AchievementType;
import com.luke.clones.network.communication.StatsType;
import com.luke.dao.AchievementsDAO;
import com.luke.dao.UsersDAO;
import com.luke.dto.AchievementDTO;
import com.luke.dto.AchievementTypeDTO;
import com.luke.dto.UserDTO;

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

public class AchievementsService {
	public static final AchievementsService INSTANCE = new AchievementsService();
	
	private Logger log = Logger.getLogger(AchievementsService.class);
	private UsersDAO usersDAO = UsersDAO.INSTANCE;
	private StatsGetService statsGetService = StatsGetService.INSTANCE;
	private AchievementsDAO achievementsDAO = AchievementsDAO.INSTANCE;
	
	private AchievementsService(){
		
	}

	public List<AchievementType> updateUserAchievements(String userGoogleId){
		log.info("Updating achievements for user with google_id "+userGoogleId);
		List<AchievementType> newAchievements = new ArrayList<AchievementType>();
		
		UserDTO userDTO = usersDAO.getUserByGoogleId(userGoogleId);
		
		long playTime = getUserPlayTime(userGoogleId);
		newAchievements.addAll(getNotYetAddedUserAchievementsByAcievementGenreType(userDTO, AchievementGenreType.PLAY_TIME, playTime));
		long gamesPlayed = getUserPlayedGames(userGoogleId);
		newAchievements.addAll(getNotYetAddedUserAchievementsByAcievementGenreType(userDTO, AchievementGenreType.GAMES_PLAYED, gamesPlayed));
		long gamesWon = getUserWonGames(userGoogleId);
		newAchievements.addAll(getNotYetAddedUserAchievementsByAcievementGenreType(userDTO, AchievementGenreType.GAMES_WON, gamesWon));
		long gamesWonInRow = getUserBiggestWonInRow(userGoogleId);
		newAchievements.addAll(getNotYetAddedUserAchievementsByAcievementGenreType(userDTO, AchievementGenreType.GAMES_WON_IN_ROW, gamesWonInRow));
		
		for(AchievementType newAchievement: newAchievements){
			AchievementTypeDTO achievementTypeDTO = achievementsDAO.getAchievementTypeByAchievementType(newAchievement);
			AchievementDTO achievementToInsert = new AchievementDTO(0L, achievementTypeDTO.getId(), userDTO.getId());
			achievementsDAO.insertAchievementValue(achievementToInsert);
		}
		
		log.info("Successfully updated achievements for user with google_id "+userGoogleId);
		return newAchievements;
	}
	
	public List<AchievementType> getUserAchievements(String userGoogleId){
		log.info("Getting achievements for user with google_id "+userGoogleId);
		List<AchievementType> userAchievements = new ArrayList<AchievementType>();
		
		UserDTO userDTO = usersDAO.getUserByGoogleId(userGoogleId);
		
		List<AchievementTypeDTO> userAchievementTypeDTOs = achievementsDAO.getUserAchievementTypes(userDTO.getId());
		for(AchievementTypeDTO achievementTypeDTO: userAchievementTypeDTOs){
			userAchievements.add(achievementTypeDTO.getAchievementType());
		}
		
		log.info("Successfully got achievements for user with google_id "+userGoogleId);
		return userAchievements;
	}
	
	private List<AchievementType> getNotYetAddedUserAchievementsByAcievementGenreType(UserDTO userDTO, AchievementGenreType achievementGenreType, long currentValue){
		log.info("Obtaining new achievements of type "+achievementGenreType+" for user with google_id "+userDTO.getGoogleId());
		List<AchievementType> newAchievements = new ArrayList<AchievementType>();
		
		for(AchievementType achievementType: AchievementType.valuesByGenre(achievementGenreType)){
			if(currentValue>=achievementType.getValue()){
				AchievementTypeDTO achievementTypeDTO = achievementsDAO.getAchievementTypeByAchievementType(achievementType);
				AchievementDTO achievement = achievementsDAO.getAchievementByUserAndAchievementsType(userDTO.getId(), achievementTypeDTO.getId());
				if(achievement==null){
					newAchievements.add(achievementType);
				}
			}
		}
		
		log.info("Successfully obtained new achievements of type "+achievementGenreType+" for user with google_id "+userDTO.getGoogleId());
		return newAchievements;
	}
	
	private long getUserPlayTime(String userGoogleId){
		log.info("Calculating current total play time for user with google_id "+userGoogleId);
		long playTimeMs=statsGetService.getGeneralStatValue(userGoogleId, StatsType.TOTAL_PLAY_TIME)*1000;
		long playTimeHours = TimeUnit.MILLISECONDS.toHours(playTimeMs);
		log.info("Successfully calculated current total play time for user with google_id "+userGoogleId);
		return playTimeHours;
	}
	
	private long getUserPlayedGames(String userGoogleId){
		log.info("Calculating current total played games for user with google_id "+userGoogleId);
		long playedGames = 0;
		playedGames+=statsGetService.getGeneralStatValue(userGoogleId, StatsType.PLAYED_2P_GAMES);
		playedGames+=statsGetService.getGeneralStatValue(userGoogleId, StatsType.PLAYED_4P_GAMES);
		log.info("Successfully calculated current total played games for user with google_id "+userGoogleId);
		return playedGames;
	}
	
	private long getUserWonGames(String userGoogleId){
		log.info("Calculating current total won games for user with google_id "+userGoogleId);
		long wonGames = 0;
		wonGames+=statsGetService.getGeneralStatValue(userGoogleId, StatsType.WON_2P_GAMES);
		wonGames+=statsGetService.getGeneralStatValue(userGoogleId, StatsType.WON_4P_GAMES);
		log.info("Successfully calculated current total won games for user with google_id "+userGoogleId);
		return wonGames;
	}
	
	private long getUserBiggestWonInRow(String userGoogleId){
		log.info("Calculating current biggest won in row for user with google_id "+userGoogleId);
		long biggestWonInRow=statsGetService.getGeneralStatValue(userGoogleId, StatsType.BIGGEST_WON_IN_ROW);
		log.info("Successfully calculated current biggest won in row for user with google_id "+userGoogleId);
		return biggestWonInRow;
	}
	
}
