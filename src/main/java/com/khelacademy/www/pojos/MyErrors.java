package com.khelacademy.www.pojos;

public class MyErrors {
	private String genericMsg = null;
	public MyErrors (String s){
		this.genericMsg = s;
	}
	public String getErrorMsg() {
		return genericMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.genericMsg = errorMsg;
	}
}
