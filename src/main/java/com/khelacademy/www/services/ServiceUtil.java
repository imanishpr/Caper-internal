package com.khelacademy.www.services;

import com.khelacademy.www.pojos.ApiFormatter;
import com.khelacademy.www.utils.Constants;

public class ServiceUtil {

    public static <T> ApiFormatter<T> convertToSuccessResponse(T t) {

        ApiFormatter<T> apiResponse = new ApiFormatter<T>();
        apiResponse.setCode(Constants.SUCCESS_RESPONSE_CODE);
        apiResponse.setResult(Constants.SUCCESS_RESPONSE_VALUE);
        apiResponse.setError(null);
        apiResponse.setResponse(t);
        return apiResponse;
    }

    public static <T> ApiFormatter<T> convertToFailureResponse(T t, String errorMessage, Integer statusCode) {
        ApiFormatter<T> apiResponse = new ApiFormatter<T>();
        apiResponse.setCode(statusCode);
        apiResponse.setResult(Constants.FAILURE_RESPONSE_VALUE);
        apiResponse.setResponse(t);
        apiResponse.setError(errorMessage);
        return apiResponse;
    }
}