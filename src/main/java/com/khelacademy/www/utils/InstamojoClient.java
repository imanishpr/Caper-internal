package com.khelacademy.www.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.instamojo.wrapper.api.Instamojo;
import com.instamojo.wrapper.api.InstamojoImpl;
import com.instamojo.wrapper.exception.ConnectionException;
@Component
public class InstamojoClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DBArrow.class);
    private static String CLIENT_ID;
	private static String CLIENT_SECRET;
    private static String API_ENDPOINT;
    private static String AUTH_ENDPOINT;

    @Value("${instaMojoClientId}")
	private void setDbUser(String CLIENT_ID) {
    	InstamojoClient.CLIENT_ID = CLIENT_ID;
	}
    @Value("${instaMojoSecretKey}")
    private void setDbName(String CLIENT_SECRET) {
    	InstamojoClient.CLIENT_SECRET = CLIENT_SECRET;
	}
    @Value("${instaMojoApiUrl}")
    private void setDbPass(String API_ENDPOINT) {
    	InstamojoClient.API_ENDPOINT = API_ENDPOINT;
	}
    @Value("${instaMojoTokenUrl}")
    private void setDatabase(String AUTH_ENDPOINT) {
    	InstamojoClient.AUTH_ENDPOINT = AUTH_ENDPOINT;
    }
    public static Instamojo getInstamojoClient() throws ConnectionException {
    	return InstamojoImpl.getApi(CLIENT_ID, CLIENT_SECRET, API_ENDPOINT, AUTH_ENDPOINT);
    }
}
