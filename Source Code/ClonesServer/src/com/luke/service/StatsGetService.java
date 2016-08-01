package com.luke.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.luke.clones.network.communication.StatsType;
import com.luke.dao.StatsDAO;
import com.luke.dao.UsersDAO;
import com.luke.dto.GeneralStatsDTO;
import com.luke.dto.GeneralStatsTypeDTO;
import com.luke.dto.LevelPointsDTO;
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

public class StatsGetService {
	public static StatsGetService INSTANCE = new StatsGetService();
	
	private Logger log = Logger.getLogger(StatsGetService.class);
	
	private StatsDAO statsDAO = StatsDAO.INSTANCE;
	private UsersDAO usersDAO = UsersDAO.INSTANCE;
	
	private StatsGetService(){
		
	}
	
	public long getGeneralStatValue(String userGoogleId, StatsType statsType){
		UserDTO userDTO = usersDAO.getUserByGoogleId(userGoogleId);
		GeneralStatsTypeDTO statisticTypeDTO = statsDAO.getStatisticTypeByStatsType(statsType);
		GeneralStatsDTO generalStatsDTO = statsDAO.getGeneralStatsByUserAndGeneralStatsType(userDTO.getId(), statisticTypeDTO.getId());
		if(generalStatsDTO!=null){
			return generalStatsDTO.getValue();
		}
		else{
			return 0;
		}
	}
	
	public Map<StatsType, Long> getAllGeneralStatsValuesMap(String userGoogleId){
		StatsType[] generalStatsTypes = StatsType.values();
		Map<StatsType, Long> generalStatsMap = new HashMap<StatsType, Long>();
		
		for(StatsType statsType: generalStatsTypes){
			long value = getGeneralStatValue(userGoogleId, statsType);
			generalStatsMap.put(statsType, value);
		}
		return generalStatsMap;
	}
	
	public long getUserLevel(String userGoogleId){
		long  userPoints = getUserPoints(userGoogleId);
		LevelPointsDTO currentLevelPointsDTO = statsDAO.getLevelPointsByPoints(userPoints);
		long userLevel = currentLevelPointsDTO.getLevel();
		return userLevel;	
	}
	
	public long getUserPercentToNextLevel(String userGoogleId){
		long  userPoints = getUserPoints(userGoogleId);
		LevelPointsDTO currentLevelPointsDTO = statsDAO.getLevelPointsByPoints(userPoints);
		long userLevel = currentLevelPointsDTO.getLevel();
		long currentLevelPoints = currentLevelPointsDTO.getPoints();

		LevelPointsDTO nextLevelPointsDTO = statsDAO.getLevelPointsByLevel(userLevel+1);
		long nextLevelPoints = nextLevelPointsDTO.getPoints();
		
		long pointsDifferenceBetweenLevels = nextLevelPoints-currentLevelPoints;
		long userPointsAboveCurrentLevel = userPoints-currentLevelPoints;
		
		double userPointsAboveCurrentLevelToDifferenceBetweenLevels = (double)userPointsAboveCurrentLevel/(double)pointsDifferenceBetweenLevels;
		long userPointsAboveCurrentLevelToDifferenceBetweenLevelsPercent =
				Math.round(userPointsAboveCurrentLevelToDifferenceBetweenLevels*100);
		
		long toNextLevelPercent = 100 - userPointsAboveCurrentLevelToDifferenceBetweenLevelsPercent;
		
		return toNextLevelPercent;
	}
	
	private long getUserPoints(String userGoogleId){
		long  userPoints = 0;
		userPoints+=getGeneralStatValue(userGoogleId, StatsType.WON_2P_GAMES)*100;
		userPoints+=getGeneralStatValue(userGoogleId, StatsType.WON_4P_GAMES)*150;
		userPoints+=getGeneralStatValue(userGoogleId, StatsType.TOTAL_BALLS_SCORED);
		return userPoints;
	}
	
}
