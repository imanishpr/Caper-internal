package com.khelacademy.www.pojos;

public class OTPContent {
	public String getPhone() {
		return phone;
	}
	public String getSessionDetails() {
		return sessionDetails;
	}
	public String getOtp() {
		return otp;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setSessionDetails(String sessionDetails) {
		this.sessionDetails = sessionDetails;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private String phone;
	private String sessionDetails;
	private String otp;
	private String status;

}
