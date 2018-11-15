package com.khelacademy.daoImpl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import net.bytebuddy.implementation.bytecode.Throw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.CreatePaymentOrderResponse;
import com.khelacademy.dao.BookEventDao;
import com.khelacademy.dao.UserDao;
import com.khelacademy.www.pojos.ApiFormatter;
import com.khelacademy.www.pojos.BookingRequestObject;
import com.khelacademy.www.pojos.MyErrors;
import com.khelacademy.www.pojos.PriceDetails;
import com.khelacademy.www.pojos.User;
import com.khelacademy.www.services.BookingStatus;
import com.khelacademy.www.services.PresenceStatus;
import com.khelacademy.www.services.ServiceUtil;
import com.khelacademy.www.services.UserStatus;
import com.khelacademy.www.utils.Constants;
import com.khelacademy.www.utils.DBArrow;
import com.khelacademy.www.utils.InstamojoPaymentHelper;
import com.khelacademy.www.utils.NoOfTickerCalculator;
import com.khelacademy.www.utils.SMSService;

public class BookEventDaoImpl implements BookEventDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);
    DBArrow SQLArrow = DBArrow.getArrow();
	@Override
	public Response bookSingleTicket(BookingRequestObject bookingRequestObject, boolean isSingle) throws UnsupportedEncodingException {
		User user = new User();
		UserDao userDao = new UserDaoImpl();
		if(isSingle)
			user.setFirstName(bookingRequestObject.getPriceDetail().get(0).getPlayerNames().get(1).toString());
		else
			user.setFirstName("khelacademyPlayer");
		user.setLastName("");
		user.setCity("");
		user.setAddress("");
		user.setContactNumber(bookingRequestObject.getPhone());
		user.setEmail(bookingRequestObject.getEmail());
		user.setStatus(UserStatus.INITIATED.toString());
		CreatePaymentOrderResponse response = null;
		try {
			
			if(userDao.registerUser(user).equalsIgnoreCase(PresenceStatus.REGISTRED_SUCCESSFULLY.toString()) || userDao.registerUser(user).equalsIgnoreCase(PresenceStatus.EXISTS.toString())){
//		        String TXN_ID = Long.toString(System.nanoTime());
//		        byte[] bytesOfMessage = TXN_ID.getBytes("UTF-8");
//
//		        MessageDigest md = null;
//				try {
//					md = MessageDigest.getInstance("MD5");
//				} catch (NoSuchAlgorithmException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		        byte[] thedigest = md.digest(bytesOfMessage);
//		        TXN_ID = thedigest.toString();
		        
		        PreparedStatement statement = SQLArrow.getPreparedStatementForId("INSERT INTO booking  (booking_date, no_of_tickets, status ) values (NOW(), ?, ?)	");
		        statement.setInt(1,(NoOfTickerCalculator.totalTicket(bookingRequestObject)));
		       // statement.setString(2, TXN_ID);
		        statement.setString(2, BookingStatus.INITIATED.toString());
		        int bookingTableUpdate = SQLArrow.fireBowfishing(statement);
		        Integer bookingId = null;
                ResultSet rs = statement.getGeneratedKeys();
                if(rs.next()){
					bookingId = rs.getInt(1);
					bookingRequestObject.setBookingId(bookingId);
				}
	            if(bookingTableUpdate > 0 && bookingId > 0){
	            	StringBuffer SQLString = new StringBuffer("INSERT INTO booking_prices  (booking_id, price_id, quantity ) values ");
	            	
	            	StringBuffer vals = new StringBuffer("");
	            	int i=0;
	            	for (PriceDetails p : bookingRequestObject.getPriceDetail()) {
	            		if(i==0) {
	            			vals.append("("+ bookingId + "," +  p.getPriceId() + ","+ p.getQuantity()+")");
	            		}
	            		else{
	            			vals.append(", ("+ bookingId + ","  +  p.getPriceId() + ","+ p.getQuantity()+")");
	            		}
	            		i++;
	            		 
	            	}
	            	SQLString.append(vals);
	            	
	            	
	            	statement = SQLArrow.getPreparedStatement(SQLString.toString());
	            	if(SQLArrow.fireBowfishing(statement) >= 1) {
	            		int userId = 0;
	            		statement = SQLArrow.getPreparedStatement("SELECT id FROM user where phone = ?");
	            		statement.setString(1, bookingRequestObject.getPhone());
	                    try (ResultSet rs1 = SQLArrow.fire(statement)) {
	                        while (rs1.next()) {
	                        	userId = rs1.getInt("id");
	                        }
	                        bookingRequestObject.setUserId(userId);
	                    }catch(Exception e) {
	                    	e.printStackTrace();
	                    	throw e;
	                    }
	    				if(!userDao.recordTempUsers(bookingRequestObject).equals(PresenceStatus.ALL_TEMP_USER_SUCCESS.toString())){
	    					LOGGER.error("TEMP USERS GOT FUCKED FOR SOME REASON");
	    					SQLArrow.rollBack(null);
	    					throw new Error("TEMP USERS GOT FUCKED FOR SOME REASON");
	    				}
	            		statement = SQLArrow.getPreparedStatementForId("INSERT INTO ticket  (event_id, booking_id, user_id ) values ( ?, ?, ?)");
	            		statement.setInt(1, bookingRequestObject.getEventId());
	            		statement.setInt(2, bookingId);
	            		statement.setInt(3, userId);
	            		if(SQLArrow.fireBowfishing(statement) >= 1) {
	            			
	        				PaymentOrder order = new PaymentOrder();
	        				//order.setId(Integer.toString(bookingId));
	        		        order.setName(user.getFirstName());
	        		        order.setEmail(user.getEmail());
	        		        order.setPhone(user.getContactNumber());
	        		        order.setDescription("Booking a event for  : " + user.getContactNumber() + " and Event Id " + bookingRequestObject.getEventId());
	        		        order.setCurrency("INR");
	        		        order.setAmount((double) bookingRequestObject.getTotalAmount());
	        		        order.setRedirectUrl(Constants.REDIRECT_URL);
	        		        order.setWebhookUrl(Constants.WEBHOOK_URL);
	        		        order.setTransactionId(Integer.toString(bookingId));
	        		    	InstamojoPaymentHelper instamojoPaymentHelper = new InstamojoPaymentHelper();
	        		    	instamojoPaymentHelper.setOrder(order);
	        		    	try{
	        		    		response = instamojoPaymentHelper.initiatePayment(order);	
	        		    	}catch(Exception e) {
	        		    		LOGGER.error("COULD NOT INITIATE PAYMENT FOR ORDER"+ order.getTransactionId(), e);
	        		    		SQLArrow.rollBack(null);
	        		    	}
	        		    	statement = SQLArrow.getPreparedStatement("UPDATE booking SET txn_id=? where booking_id=?");
	        		    	try {
	        					statement.setString(1, response.getPaymentOrder().getId());
	        					statement.setInt(2, bookingId);
	        					if(SQLArrow.fireBowfishing(statement) >= 1){
	        						LOGGER.info("ADDED ORDERID FOR BOOKING ID " + bookingId);
	        					}
	        				} catch (SQLException e) {
	        					LOGGER.debug("ERROR IN UPDATING ORDERID FOR BOOKING ID :" + bookingId + "WITH ERROR " + e.getMessage());
	        					SQLArrow.rollBack(null);
	        				}
	     
	            			
	            		}else{
	            			SQLArrow.rollBack(null);
	            			MyErrors err = new MyErrors("Could not Book Ticket for user :" + userId);
	            	    	ApiFormatter<MyErrors>  res= ServiceUtil.convertToFailureResponse(err, "true", 500);
	            	        return Response.ok(new GenericEntity<ApiFormatter<MyErrors>>(res) {
	            	        }).build();
	            		}
	            		
	            	}
	            	SQLArrow.relax(null);
	        
	            }
		        
			}
		} catch (SQLException e) {
			LOGGER.error("Could not book ticket for user with phone " + bookingRequestObject.getPhone() + " and email "+ bookingRequestObject.getEmail());
			e.printStackTrace();
		}
		
//    	ApiFormatter<CreatePaymentOrderResponse>  res= ServiceUtil.convertToSuccessResponse(response);
//        return Response.ok(new GenericEntity<ApiFormatter<CreatePaymentOrderResponse>>(res) {
//        }).build();
		MyErrors err = new MyErrors(response.getPaymentOptions().getPaymentUrl().toString());
    	ApiFormatter<MyErrors>  paymentUrl= ServiceUtil.convertToSuccessResponse(err);
        return Response.ok(new GenericEntity<ApiFormatter<MyErrors>>(paymentUrl) {
        }).build();
	}

	@Override
	public Response bookMultipleTicket(BookingRequestObject bookingRequestObject) {

		User user = new User();
		UserDao userDao = new UserDaoImpl();
		user.setFirstName("Khelacademy Player");
		user.setLastName("");
		user.setCity("");
		user.setAddress("");
		user.setContactNumber(bookingRequestObject.getPhone());
		user.setEmail(bookingRequestObject.getEmail());
		user.setStatus(UserStatus.INITIATED.toString());
		
		try {
			if(userDao.registerUser(user).equalsIgnoreCase(PresenceStatus.REGISTRED_SUCCESSFULLY.toString())){
				LOGGER.info("New Registered User " + user.getContactNumber());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	
	}

	@Override
	public boolean UpdateStatusFromWbhook(String id, String status) {
    	PreparedStatement statement = SQLArrow.getPreparedStatement("UPDATE booking SET status=? where txn_id=?");
    	try {
			statement.setString(1, status);
			statement.setString(2, id);
			int i = SQLArrow.fireBowfishing(statement);
			if(i >= 1){
				String phone;
				statement = SQLArrow.getPreparedStatement("select A.phone from user as A inner join ticket as T on A.id = T.user_id inner join booking as B on T.booking_id = B.booking_id where B.txn_id = ?");
				statement.setString(1, id);
				ResultSet rs = SQLArrow.fire(statement);
	            if(rs.next())
	            {
	            	phone = rs.getString(1);
	    			SMSService smsService = new SMSService();
	    			smsService.sendTransactionalSMS("CAPER-KA", phone, "Thank you for Booking with KA. your TXN_ID is : "+id);
	            }
				SQLArrow.relax(null);
				return true;
			}
		} catch (SQLException e) {
			LOGGER.debug("ERROR UPDATING STATUS FOR TXN_ID : " + id + "WITH ERROR " + e.getMessage());
			return false;
		}
    	try {
			SQLArrow.rollBack(null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
	}
	//NOT IN USE make use in future
	@Override
	public String checkBookingStatus(String paymentId) {
    	InstamojoPaymentHelper instamojoPaymentHelper = new InstamojoPaymentHelper();
    	return instamojoPaymentHelper.checkStatus(paymentId);
	}
	@Override
	public Integer getLastUserGameId() {
		PreparedStatement statement=null;
		Integer gameId=null;
		try{
			statement = SQLArrow.getPreparedStatement("SELECT  game_user_id from temp_users order by game_user_id DESC LIMIT 1");
			ResultSet rs = SQLArrow.fire(statement);
            if(rs.next())
            {
            	gameId = rs.getInt(1);
            }
		}catch(Exception e) {
			LOGGER.error("ERROR IN PREPARING STATEMENT FOR LAST GAME ID : ", e);
		}
		if(gameId == null) {
			return 1;
		}
		return gameId;
	}

	@Override
	public Integer getCategoryId(Integer priceId) {
		PreparedStatement statement=null;
		Integer catId=null;
		try{
			statement = SQLArrow.getPreparedStatement("SELECT  category_id from price where price_id=?");
			statement.setInt(1, priceId);
			ResultSet rs = SQLArrow.fire(statement);
            if(rs.next())
            {
            	catId = rs.getInt(1);
            }
		}catch(Exception e) {
			LOGGER.error("ERROR IN PREPARING STATEMENT FOR CATEGORY ID : ", e);
		}
		return catId.intValue();
	}
}
