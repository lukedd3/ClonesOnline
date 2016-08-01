package com.luke.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.luke.clones.network.communication.StatsType;
import com.luke.dto.GeneralStatsDTO;
import com.luke.dto.GeneralStatsTypeDTO;
import com.luke.dto.LevelPointsDTO;
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

public class StatsDAO {
	public static final StatsDAO INSTANCE = new StatsDAO();
	
	private Logger log = Logger.getLogger(StatsDAO.class);
	private DatabaseService databaseService = DatabaseService.INSTANCE;
	private Connection connection;
	
	private StatsDAO(){
		
	}
	
	public GeneralStatsTypeDTO getStatisticTypeByStatsType(StatsType statsType){
		try{
			//
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String selectStatsticStatement = "select id, type from general_stats_type where type=?";
			PreparedStatement preparedStatement = connection.prepareStatement(selectStatsticStatement);
			preparedStatement.setString(1, statsType.name());
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				Long resultId = resultSet.getLong("id");
				String resultType = resultSet.getString("type");
				
				return new GeneralStatsTypeDTO(resultId, statsType);
			}
			return null;
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
			return null;
		}
	}
	
	private GeneralStatsDTO getGeneralStatsById(long id){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String selectGeneralStatsByIdStatement = "select id, value, general_stats_type, _user from general_stats where id=?";
			PreparedStatement preparedStatement = connection.prepareStatement(selectGeneralStatsByIdStatement);
			preparedStatement.setLong(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				Long resultId = resultSet.getLong("id");
				Long resultValue = resultSet.getLong("value");
				Long resultGeneralStatsType = resultSet.getLong("general_stats_type");
				Long resultUser = resultSet.getLong("_user");
				
				GeneralStatsDTO generalStatsDTO = new GeneralStatsDTO(resultId, resultValue, resultGeneralStatsType, resultUser);
				return generalStatsDTO;
			}
			return null;
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
			return null;
		}
	}
	
	public GeneralStatsDTO getGeneralStatsByUserAndGeneralStatsType(long userId, long generalStatsTypeId){
		try{
			//
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String selectGeneralStatsByUserAndGeneralStatsTypeStatement = "select id, value, general_stats_type, _user from general_stats where _user=? and general_stats_type=?";
			PreparedStatement preparedStatement = connection.prepareStatement(selectGeneralStatsByUserAndGeneralStatsTypeStatement);
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, generalStatsTypeId);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				Long resultId = resultSet.getLong("id");
				Long resultValue = resultSet.getLong("value");
				Long resultGeneralStatsType = resultSet.getLong("general_stats_type");
				Long resultUser = resultSet.getLong("_user");
				
				GeneralStatsDTO generalStatsDTO = new GeneralStatsDTO(resultId, resultValue, resultGeneralStatsType, resultUser);
				return generalStatsDTO;
			}
			return null;
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
			return null;
		}
	}
	
	public void saveOrUpdateStatisticValue(GeneralStatsDTO statistic){
			if(statistic.getId()>0 && getGeneralStatsById(statistic.getId())!=null){
				updateStatisticValue(statistic);
			}
			else{
				insertStatisticValue(statistic);
			}
	}
	
	public LevelPointsDTO getLevelPointsByPoints(long points){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String selectLevelPointsByPoints = "select id, level as max_level, points from level_points where points<=? group by id, points order by max_level desc limit 1;";
			PreparedStatement preparedStatement = connection.prepareStatement(selectLevelPointsByPoints);
			preparedStatement.setLong(1, points);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				Long resultId = resultSet.getLong("id");
				Long resultLevel = resultSet.getLong("max_level");
				Long resultPoints = resultSet.getLong("points");
				
				LevelPointsDTO levelPointsDTO = new LevelPointsDTO(resultId, resultLevel, resultPoints);
				return levelPointsDTO;
			}
			return null;
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
			return null;
		}
	}
	
	public LevelPointsDTO getLevelPointsByLevel(long level){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String selectLevelPointsByPoints = "select id, level, points from level_points where level=?";
			PreparedStatement preparedStatement = connection.prepareStatement(selectLevelPointsByPoints);
			preparedStatement.setLong(1, level);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				Long resultId = resultSet.getLong("id");
				Long resultLevel = resultSet.getLong("level");
				Long resultPoints = resultSet.getLong("points");
				
				LevelPointsDTO levelPointsDTO = new LevelPointsDTO(resultId, resultLevel, resultPoints);
				return levelPointsDTO;
			}
			return null;
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
			return null;
		}
	}
	
	private void updateStatisticValue(GeneralStatsDTO statistic){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String updateStatisticStatement = "update general_stats set value=?, general_stats_type=?, _user=? where id=?";
			PreparedStatement preparedStatement = connection.prepareStatement(updateStatisticStatement);
			preparedStatement.setLong(1, statistic.getValue());
			preparedStatement.setLong(2, statistic.getGeneral_stats_type());
			preparedStatement.setLong(3, statistic.getUser());
			preparedStatement.setLong(4, statistic.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
		}
	}
	
	private void insertStatisticValue(GeneralStatsDTO statistic){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String insertStatisticStatement = "insert into general_stats (value, general_stats_type, _user) values (?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(insertStatisticStatement);
			preparedStatement.setLong(1, statistic.getValue());
			preparedStatement.setLong(2, statistic.getGeneral_stats_type());
			preparedStatement.setLong(3, statistic.getUser());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
		}
	}
	
}
