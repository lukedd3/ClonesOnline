package com.luke.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.luke.dto.UserDTO;
import com.luke.service.DatabaseService;
import com.luke.service.UsersService;

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

public class UsersDAO {
	public static final UsersDAO INSTANCE = new UsersDAO();
	
	private Logger log = Logger.getLogger(UsersDAO.class);
	private DatabaseService databaseService = DatabaseService.INSTANCE;
	private Connection connection;
	
	private UsersDAO(){
		
	}
	
	public UserDTO getUserByGoogleId(String googleId){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String selectUserStatement = "select id, google_id, name from users where google_id=?";
			PreparedStatement preparedStatement = connection.prepareStatement(selectUserStatement);
			preparedStatement.setString(1, googleId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				Long resultId = resultSet.getLong("id");
				String resultGoogleId = resultSet.getString("google_id");
				String resultName = resultSet.getString("name");
				
				return new UserDTO(resultId, resultGoogleId, resultName);
			}
			return null;
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
			return null;
		}
	}
	
	public void insertUser(UserDTO user){
		try{
			connection = databaseService.connectToDatabaseIfNotConnectedAndReturnConnection();
			
			String insertUserStatement = "insert into users(google_id, name) values (?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(insertUserStatement);
			preparedStatement.setString(1, user.getGoogleId());
			preparedStatement.setString(2, user.getName());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("An error error occured during sql statement execution");
			log.error("",e);
		}
	}
}
