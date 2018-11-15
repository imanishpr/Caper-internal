/*
 * Copyright 2016-2017 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.khelacademy.www.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.holonplatform.jaxrs.swagger.annotations.ApiDefinition;
import com.instamojo.wrapper.api.Instamojo;
import com.instamojo.wrapper.api.InstamojoImpl;
import com.instamojo.wrapper.exception.ConnectionException;
import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.CreatePaymentOrderResponse;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;
import com.khelacademy.dao.BookEventDao;
import com.khelacademy.dao.CityDao;
import com.khelacademy.dao.EventDao;
import com.khelacademy.dao.HomeDao;
import com.khelacademy.dao.SportsDao;
import com.khelacademy.dao.UserDao;
import com.khelacademy.daoImpl.CityDaoImpl;
import com.khelacademy.daoImpl.BookEventDaoImpl;
import com.khelacademy.daoImpl.EventDaoImpl;
import com.khelacademy.daoImpl.HomeDaoImpl;
import com.khelacademy.daoImpl.SportsDaoImpl;
import com.khelacademy.daoImpl.UserDaoImpl;
import com.khelacademy.www.pojos.ApiFormatter;
import com.khelacademy.www.pojos.BookingRequestObject;
import com.khelacademy.www.pojos.MyErrors;
import com.khelacademy.www.pojos.OTPContent;
import com.khelacademy.www.pojos.Order;
import com.khelacademy.www.pojos.UploadableEvent;
import com.khelacademy.www.pojos.User;
import com.khelacademy.www.services.ServiceUtil;
import com.khelacademy.www.utils.Constants;
import com.khelacademy.www.utils.InstamojoClient;
import com.khelacademy.www.utils.PaymentRequestValidator;
import com.khelacademy.www.utils.RedisBullet;
import com.khelacademy.www.utils.SMSService;

/**
 * JAX-RS API endpoint.
 */
@SuppressWarnings("restriction")
@ApiDefinition(docsPath = "/api/docs", title = "Example API", version = "v1", prettyPrint = true)
@Api("Test API")
@Component
@Path("/api")
public class ApiEndpoint {	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiEndpoint.class);
	
    @ApiOperation("Ping request")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException {
    	LOGGER.debug("Server is running Fine Check DB credentials");
        return Response.ok("{\"message\":\"pong\"}").build();
    }

    @ApiOperation("Home request")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GET
    @Path("/home")
    @Produces(MediaType.APPLICATION_JSON)
    public Response home() {
        HomeDao home = new HomeDaoImpl();
        try {
            return home.getHome();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @ApiOperation("get user request")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GET
    @Path("/user_details")
    @Produces(MediaType.APPLICATION_JSON)
    public Response user_details(@QueryParam("user_id") Integer userId) throws SQLException {
        UserDao user = new UserDaoImpl();
        return user.getUserById(userId);
    }

    @ApiOperation("get event request")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GET
    @Path("/event")
    @Produces(MediaType.APPLICATION_JSON)
    public Response events(@QueryParam("city_id") Integer cityId, @QueryParam("game_id") Integer gameId) throws SQLException {
        EventDao event = new EventDaoImpl();
        return event.getEventByCityId(cityId, gameId);
    }
    
    @ApiOperation("get event Prices request")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GET
    @Path("/event_prices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eventsPrices(@QueryParam("event_id") Integer eventId) throws SQLException {
        EventDao event = new EventDaoImpl();
        return event.getEventPrice(eventId);
    }
    
    @ApiOperation("post user request")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @POST
    @Path("/user_register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response user_registration(@RequestBody User userDetails) throws SQLException {
        UserDao user = new UserDaoImpl();
        String Register = user.registerUser(userDetails);
        if(Register.equals("EXIST")){
			MyErrors error = new MyErrors("User Exist Try Loggin In");
        	ApiFormatter<MyErrors>  err= ServiceUtil.convertToFailureResponse(error, "true", 500);
            return Response.ok(new GenericEntity<ApiFormatter<MyErrors>>(err) {
            }).build();
        }
        if(Register.equals("FAILURE")){
			MyErrors error = new MyErrors("Technical Problem");
        	ApiFormatter<MyErrors>  err= ServiceUtil.convertToFailureResponse(error, "true", 500);
            return Response.ok(new GenericEntity<ApiFormatter<MyErrors>>(err) {
            }).build();
        }
        if(!Register.equals("ERROR"))
        	userDetails.setSessionValue(Register);
    	ApiFormatter<User>  usr= ServiceUtil.convertToSuccessResponse(userDetails);
        return Response.ok(new GenericEntity<ApiFormatter<User>>(usr) {
        }).build();
    }
    @ApiOperation("Start Booking")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @POST
    @Path("/book_event")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response book_event(@RequestBody BookingRequestObject bookingRequestObject) throws SQLException {
    	PaymentRequestValidator paymentRequestValidator = new PaymentRequestValidator();
    	if(paymentRequestValidator.validate(bookingRequestObject)) {
    		BookEventDao book = new BookEventDaoImpl();
    		if(bookingRequestObject.getPriceDetail().size() == 1 && bookingRequestObject.getPriceDetail().get(0).getQuantity() == 1 && bookingRequestObject.getPriceDetail().get(0).getPlayerNames().size() ==1) {
    			try {
					return book.bookSingleTicket(bookingRequestObject,true);
				} catch (UnsupportedEncodingException e) {
	
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}else{
				try {
					return book.bookSingleTicket(bookingRequestObject, false);
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		}
    		
    	}

		MyErrors error = new MyErrors("Technical Problem");
    	ApiFormatter<MyErrors>  err= ServiceUtil.convertToFailureResponse(error, "true", 500);
        return Response.ok(new GenericEntity<ApiFormatter<MyErrors>>(err) {
        }).build();
    }
    
    @ApiOperation("verify OTP request")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @POST
    @Path("/verify_otp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verify_otp(@RequestBody OTPContent otpContent) throws SQLException {
        SMSService smsService = new SMSService();
        String Register = smsService.verifyOTP(otpContent.getSessionDetails(), otpContent.getOtp(), otpContent.getPhone());
        if(Register.equals("ERROR")){
			MyErrors error = new MyErrors("Technical Problem");
        	ApiFormatter<MyErrors>  err= ServiceUtil.convertToFailureResponse(error, "true", 500);
            return Response.ok(new GenericEntity<ApiFormatter<MyErrors>>(err) {
            }).build();
        }
        otpContent.setStatus(Register);
    	ApiFormatter<OTPContent>  usr= ServiceUtil.convertToSuccessResponse(otpContent);
        return Response.ok(new GenericEntity<ApiFormatter<OTPContent>>(usr) {
        }).build();
    }
    
    
    @ApiOperation("instamojo payment user")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response payment(@RequestBody Order orderObj) throws SQLException {
        Instamojo api = null;
        PaymentOrder order = new PaymentOrder();
        order.setName(orderObj.getName());
        order.setEmail(orderObj.getEmail());
        order.setPhone(orderObj.getPhone());
        order.setDescription(orderObj.getDesc());
        order.setCurrency(orderObj.getCurrency());
        order.setAmount(orderObj.getAmount());
        order.setRedirectUrl(orderObj.getRedirectUrl());
        order.setWebhookUrl(orderObj.getWebhook());
        order.setTransactionId(orderObj.getTransactionId().toString());
        
        try {
            // gets the reference to the instamojo api
            api = InstamojoClient.getInstamojoClient();
        } catch (Exception e) {

        }
        boolean isOrderValid = order.validate();

        if (isOrderValid) {
            ApiFormatter<CreatePaymentOrderResponse> paymentOrderResponse;
            try {
                CreatePaymentOrderResponse createPaymentOrderResponse = api.createNewPaymentOrder(order);
                // print the status of the payment order.
                //System.out.println(createPaymentOrderResponse.getPaymentOrder().toString());
               // createPaymentOrderResponse.getJsonResponse()
                //paymentOrderResponse = ServiceUtil.convertToSuccessResponse(createPaymentOrderResponse.toString());
                return Response.ok(createPaymentOrderResponse).build();
            } catch (Exception e) {

                if (order.isTransactionIdInvalid()) {
                    System.out.println("Transaction id is invalid. This is mostly due to duplicate  transaction id.");
                }
                if (order.isCurrencyInvalid()) {
                    System.out.println("Currency is invalid.");
                }
            }
        } else {
            // inform validation errors to the user.
            if (order.isTransactionIdInvalid()) {
                System.out.println("Transaction id is invalid.");
            }
            if (order.isAmountInvalid()) {
                System.out.println("Amount can not be less than 9.00.");
            }
            if (order.isCurrencyInvalid()) {
                System.out.println("Please provide the currency.");
            }
            if (order.isDescriptionInvalid()) {
                System.out.println("Description can not be greater than 255 characters.");
            }
            if (order.isEmailInvalid()) {
                System.out.println("Please provide valid Email Address.");
            }
            if (order.isNameInvalid()) {
                System.out.println("Name can not be greater than 100 characters.");
            }
            if (order.isPhoneInvalid()) {
                System.out.println("Phone is invalid.");
            }
            if (order.isRedirectUrlInvalid()) {
                System.out.println("Please provide valid Redirect url.");
            }

            if (order.isWebhookInvalid()) {
                System.out.println("Provide a valid webhook url");
            }
        }
        return Response.ok("{\"message\":\"success\"}").build();
    }
    @ApiOperation("instamojo payment user")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GET
    @Path("/payment_status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response paymentStatus(@QueryParam("paymentId") String paymentId) throws SQLException {
    	Instamojo api = null;
        try {
            // gets the reference to the instamojo api
            api = InstamojoClient.getInstamojoClient();
        } catch (Exception e) {

        }
        PaymentOrderDetailsResponse paymentOrderDetailsResponse = null;
		try {
			paymentOrderDetailsResponse = api.getPaymentOrderDetails(paymentId);

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // print the status of the payment order.
		
        System.out.println(paymentOrderDetailsResponse.getStatus().toString());
		MyErrors error = new MyErrors(paymentOrderDetailsResponse.getStatus().toString());
    	ApiFormatter<MyErrors>  err= ServiceUtil.convertToSuccessResponse(error);
        return Response.ok(new GenericEntity<ApiFormatter<MyErrors>>(err) {
        }).build();
    }
    @ApiOperation("instamojo webhook")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @POST
    @Path("/instamojo_webhook")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public void instamojoWebhook(@FormParam("status") String status, @FormParam("payment_id") String paymentId, @FormParam("id") String id, @FormParam("transaction_id") String transactionId, @FormParam("mac") String mac, @FormParam("amount") String amount, @FormParam("buyer_phone") String phone) {
    	try{
    		BookEventDao book = new BookEventDaoImpl();
    		//
        	Instamojo api = null;
            try {
                // gets the reference to the instamojo api
                api = InstamojoClient.getInstamojoClient();
            } catch (Exception e) {

            }
            PaymentOrderDetailsResponse paymentOrderDetailsResponse = null;
    		try {
    			paymentOrderDetailsResponse = api.getPaymentOrderDetails(id);

    		} catch (ConnectionException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		if(book.UpdateStatusFromWbhook(id,paymentOrderDetailsResponse.getStatus().toString())){
    			LOGGER.info("SUCCESSFULLY UPDATED STATUS FOR ID " + id);
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    @ApiOperation("get Cities")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GET
    @Path("/cities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response city_details() throws SQLException {
    	CityDao ct = new CityDaoImpl();
        return ct.getAllCities();
    }
    @ApiOperation("get sports")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GET
    @Path("/sports")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sports_details() throws SQLException {
       SportsDao spt = new SportsDaoImpl();
        return spt.getAllSports();
    }
    
    @ApiOperation("Create Event")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @POST
    @Path("/create_event")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create_event(@RequestBody UploadableEvent uploadableEvent) throws SQLException {
    	EventDao event = new EventDaoImpl();
    	return event.createEvent(uploadableEvent);
    }
}
