package com.khelacademy.www.pojos;

import java.util.List;

public class BookingRequestObject {
	private Integer userId;
	private Integer eventId;
	private String email;
	private String phone;
	private Integer totalAmount;
	private Integer bookingId;
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	private List<PriceDetails> priceDetail;
	
	public Integer getUserId() {
		return userId;
	}
	public Integer getEventId() {
		return eventId;
	}
	public List<PriceDetails> getPriceDetail() {
		return priceDetail;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public void setPriceDetail(List<PriceDetails> priceDetail) {
		this.priceDetail = priceDetail;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "BookingRequestObject [userId=" + userId + ", eventId="
				+ eventId + ", email=" + email + ", phone=" + phone
				+ ", totalAmount=" + totalAmount + ", priceDetail="
				+ priceDetail.toString() + "]";
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}
}
