package com.luke.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.luke.clones.network.communication.AchievementType;
import com.luke.clones.network.communication.StatsType;
import com.luke.dto.AchievementDTO;
import com.luke.dto.AchievementTypeDTO;
import com.luke.dto.GeneralStatsTypeDTO;
import com.luke.service.DatabaseService;

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

public class AchievementsDAO {
	public static final AchievementsDAO INSTANCE = new AchievementsDAO();
	
	private Logger log = Logger.getLogger(AchievementsDAO.class);
	private DatabaseService databaseService = DatabaseService.INSTANCE;
	private Connection connection;
	
	private AchievementsDAO() {
		
	}
	
	public AchievementTypeDTO getAchievementTypeByAchievementType(AchievementType achievementType){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String selectAchievementTypeStatement = "select id, type from achievements_type where type=?";
			PreparedStatement preparedStatement = connection.prepareStatement(selectAchievementTypeStatement);
			preparedStatement.setString(1, achievementType.name());
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				Long resultId = resultSet.getLong("id");
				String resultType = resultSet.getString("type");
				return new AchievementTypeDTO(resultId, achievementType);
			}
			return null;
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
			return null;
		}
	}
	
//	private List<AchievementDTO> getUserAchievements(long userId){
//		try{
//			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
//			
//			String selectAchievementsByUserAndAchievementsTypeStatement = "select id, achievements_type, _user from achievements where _user=?";
//			PreparedStatement preparedStatement = connection.prepareStatement(selectAchievementsByUserAndAchievementsTypeStatement);
//			preparedStatement.setLong(1, userId);
//			ResultSet resultSet = preparedStatement.executeQuery();
//			
//			List<AchievementDTO> userAchievements = new ArrayList<AchievementDTO>();
//			while(resultSet.next()){
//				Long resultId = resultSet.getLong("id");
//				Long resultGeneralStatsType = resultSet.getLong("achievements_type");
//				Long resultUser = resultSet.getLong("_user");
//				
//				AchievementDTO achievementDTO = new AchievementDTO(resultId, resultGeneralStatsType, resultUser);
//				userAchievements.add(achievementDTO);
//			}
//			if(userAchievements.size()>0) return userAchievements;
//			else return null;
//		} catch (SQLException e) {
//			log.error("An error error occured during sql statement execution");
//			log.error("",e);
//			return null;
//		}
//	}
	
	public List<AchievementTypeDTO> getUserAchievementTypes(long userId){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String selectAchievementsByUserAndAchievementsTypeStatement = "select at.id, at.type from achievements a inner join achievements_type at on a.achievements_type=at.id where _user=?";
			PreparedStatement preparedStatement = connection.prepareStatement(selectAchievementsByUserAndAchievementsTypeStatement);
			preparedStatement.setLong(1, userId);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			List<AchievementTypeDTO> userAchievementTypes = new ArrayList<AchievementTypeDTO>();
			while(resultSet.next()){
				Long resultId = resultSet.getLong("id");
				String resultAchievementType = resultSet.getString("type");
				
				AchievementTypeDTO achievementTypeDTO = new AchievementTypeDTO(resultId, AchievementType.valueOf(resultAchievementType));
				userAchievementTypes.add(achievementTypeDTO);
			}
			return userAchievementTypes;
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
			return new ArrayList<AchievementTypeDTO>();
		}
	}
	
	public AchievementDTO getAchievementByUserAndAchievementsType(long userId, long achievementTypeId){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String selectAchievementsByUserAndAchievementsTypeStatement = "select id, achievements_type, _user from achievements where _user=? and achievements_type=?";
			PreparedStatement preparedStatement = connection.prepareStatement(selectAchievementsByUserAndAchievementsTypeStatement);
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, achievementTypeId);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				Long resultId = resultSet.getLong("id");
				Long resultAchievementType = resultSet.getLong("achievements_type");
				Long resultUser = resultSet.getLong("_user");
				
				AchievementDTO achievementDTO = new AchievementDTO(resultId, resultAchievementType, resultUser);
				return achievementDTO;
			}
			return null;
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
			return null;
		}
	}
	
	public void insertAchievementValue(AchievementDTO achievement){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String insertStatisticStatement = "insert into achievements (achievements_type, _user) values (?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(insertStatisticStatement);
			preparedStatement.setLong(1, achievement.getAchievementType());
			preparedStatement.setLong(2, achievement.getUser());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
		}
	}
	
}
