package com.khelacademy.www.pojos;

import java.util.Date;
import java.util.HashMap;

public class Event {
    private Integer eventId;
    private String eventName;
    private Integer eventType;
	private Date date;
	private Integer status;
    private String description;
    private String eventVenue;
    private String eventCity;
    private Integer eventCityId;
    private String eventImgUrl;
    private HashMap<Integer, Integer> prices;
    private String[] sponsers;
    private String[] organizers;
    private String timings;
    private String phone;

    public HashMap <Integer, Integer> getPrices() {
        return prices;
    }
    public void setPrices(HashMap <Integer, Integer> prices) {
        this.prices = prices;
    }
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String city;

    public String[] getSponsers() {
        return sponsers;
    }

    public void setSponsers(String[] sponsers) {
        this.sponsers = sponsers;
    }

    public String[] getOrganizers() {
        return organizers;
    }

    public void setOrganizers(String[] organizers) {
        this.organizers = organizers;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    private Integer price;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }
	public String getEventCity() {
		return eventCity;
	}
	public void setEventCity(String eventCity) {
		this.eventCity = eventCity;
	}
    public Date getDate() {
		return date;
	}
	public String getEventImgUrl() {
		return eventImgUrl;
	}
	public void setEventImgUrl(String eventImgUrl) {
		this.eventImgUrl = eventImgUrl;
	}
	public String getTimings() {
		return timings;
	}
	public void setTimings(String timings) {
		this.timings = timings;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getEventCityId() {
		return eventCityId;
	}
	public void setEventCityId(Integer eventCityId) {
		this.eventCityId = eventCityId;
	}
}
