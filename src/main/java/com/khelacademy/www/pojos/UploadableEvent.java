package com.khelacademy.www.pojos;

import java.util.List;

public class UploadableEvent {
	private Event event;
	private List<EventPrice> prices;
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public List<EventPrice> getPrices() {
		return prices;
	}
	public void setPrices(List<EventPrice> prices) {
		this.prices = prices;
	}
}
