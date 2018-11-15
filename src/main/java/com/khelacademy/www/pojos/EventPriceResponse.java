package com.khelacademy.www.pojos;

import java.util.List;
import java.util.Map;

public class EventPriceResponse {
	private Integer eventId;
	private Map<Integer,List<EventPrice>> priceDetails;
	public Integer getEventId() {
		return eventId;
	}
	public Map<Integer, List<EventPrice>> getPriceDetails() {
		return priceDetails;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public void setPriceDetails(Map<Integer, List<EventPrice>> priceDetails) {
		this.priceDetails = priceDetails;
	}
}

