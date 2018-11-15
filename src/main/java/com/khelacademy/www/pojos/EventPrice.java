package com.khelacademy.www.pojos;

public class EventPrice {
	private Integer priceId;
	private Integer priceAmount;
	private String desc;
	private String currency;
	private Integer category;
	private String name;
	
	public Integer getPriceId() {
		return priceId;
	}
	public Integer getPriceAmount() {
		return priceAmount;
	}
	public String getDesc() {
		return desc;
	}
	public String getCurrency() {
		return currency;
	}

	public void setPriceId(Integer priceId) {
		this.priceId = priceId;
	}
	public void setPriceAmount(Integer priceAmount) {
		this.priceAmount = priceAmount;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
