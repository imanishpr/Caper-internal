package com.khelacademy.www.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.khelacademy.dao.UserDao;
import com.khelacademy.daoImpl.UserDaoImpl;
import com.khelacademy.www.pojos.SMSResponse;

public class SMSService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);
	final String OtpUri = "https://2factor.in/API/V1/b991d218-3b42-11e8-a895-0200cd936042/SMS/";
	final String TransactionalUri = "https://2factor.in/API/V1/b991d218-3b42-11e8-a895-0200cd936042/ADDON_SERVICES/SEND/PSMS";
	final String TransactionalBalUri = "http://2factor.in/API/V1/b991d218-3b42-11e8-a895-0200cd936042/ADDON_SERVICES/BAL/PROMOTIONAL_SMS";
	final String OtpBal = "http://2factor.in/API/V1/b991d218-3b42-11e8-a895-0200cd936042/BAL/SMS";
	public String sendSMS(String phone)
	{
		try{
		    RestTemplate restTemplate = new RestTemplate();
		    String result = restTemplate.getForObject(OtpUri+phone+"/AUTOGEN", String.class);
		    Gson g = new Gson(); 
		    SMSResponse p = g.fromJson(result, SMSResponse.class);
		    if(p.getStatus().equals("Success")){
		    	return p.getDetails();
		    }else{
		    	LOGGER.error("ERROR in sending OTP to "+ phone);
		    }
		    return "ERROR";
		}catch(Exception e){
			return e.getMessage();
		}

	}
	public String verifyOTP(String SessionDetail, String OTP, String phone){
		try{
		    RestTemplate restTemplate = new RestTemplate();
		    String result = restTemplate.getForObject(OtpUri+"VERIFY/"+SessionDetail+"/"+OTP, String.class);
		    Gson g = new Gson(); 
		    SMSResponse p = g.fromJson(result, SMSResponse.class);
		    if(p.getStatus().equals("Success")){
		    	UserDao userStatus = new UserDaoImpl();
		    	userStatus.updateStatus(phone, 12);
		    	return p.getDetails();
		    }else{
		    	LOGGER.error("ERROR in verifying OTP of "+phone);
		    }
		    return "ERROR";
		}catch(Exception e){
			LOGGER.error("ERROR in verifying OTP of "+phone + "with ERROR" +e.getMessage());
			return "ERROR";
		}
	}
	public String sendTransactionalSMS(String from, String to, String msg){
		try{
		    RestTemplate restTemplate = new RestTemplate();
		    
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		    MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		    map.add("From", from);
		    map.add("To", to);
		    map.add("Msg", msg);

		    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		    org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity( TransactionalUri, request , String.class );
		    
		    Gson g = new Gson(); 
		    
		    SMSResponse p = g.fromJson(response.getBody(), SMSResponse.class);
		    if(p.getStatus().equals("Success")){
		    	return p.getDetails();
		    }else{
		    	LOGGER.error("ERROR in sendTransactionalSMS OTP of "+to);
		    }
		    return "ERROR";
		}catch(Exception e){
			LOGGER.error("ERROR in sendTransactionalSMS of "+to + "with ERROR" +e.getMessage());
			return "ERROR";
		}
	}
	public String getTransBalance(){
		try{
		    RestTemplate restTemplate = new RestTemplate();
		    String result = restTemplate.getForObject(TransactionalBalUri, String.class);
		    Gson g = new Gson(); 
		    SMSResponse p = g.fromJson(result, SMSResponse.class);
		    if(p.getStatus().equals("Success") && Integer.parseInt(p.getDetails()) >0){
		    	return p.getDetails();
		    }else{
		    	LOGGER.error("ERROR in getting balace");
		    }
		    return "ERROR";
		}catch(Exception e){
			LOGGER.error("ERROR in getting balance of with ERROR" +e.getMessage());
			return "ERROR";
		}
	}
	public String getSMSBalance(){
		try{
		    RestTemplate restTemplate = new RestTemplate();
		    String result = restTemplate.getForObject(OtpBal, String.class);
		    Gson g = new Gson(); 
		    SMSResponse p = g.fromJson(result, SMSResponse.class);
		    if(p.getStatus().equals("Success") && Integer.parseInt(p.getDetails()) >0){
		    	return p.getDetails();
		    }else{
		    	LOGGER.error("ERROR in getting balace");
		    }
		    return "ERROR";
		}catch(Exception e){
			LOGGER.error("ERROR in getting balance of with ERROR" +e.getMessage());
			return "ERROR";
		}
	}
}
