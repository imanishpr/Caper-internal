package com.khelacademy.dao;

import com.khelacademy.www.pojos.BookingRequestObject;
import com.khelacademy.www.pojos.User;

import javax.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    Response getUserById(Integer userId) throws SQLException;

    Response getUserByEmailId(Integer emailId);

    String registerUser(User userDetails) throws SQLException;
    
    String recordTempUsers(BookingRequestObject requestObject) throws SQLException;
    
	boolean updateStatus(String phone, Integer status);
	
	List<User> getUsersByEventId(Integer eventId);
}
