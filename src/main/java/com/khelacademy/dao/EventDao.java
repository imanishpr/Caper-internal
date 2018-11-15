package com.khelacademy.dao;

import javax.ws.rs.core.Response;

import com.khelacademy.www.pojos.UploadableEvent;

public interface EventDao {
    Response getAllEvents();
    Response getEventByCityId(Integer city, Integer gameId);
    Response getEventPrice(Integer eventId);
    Response createEvent(UploadableEvent event);

}
