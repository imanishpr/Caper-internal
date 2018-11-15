package com.khelacademy.daoImpl;

import com.khelacademy.dao.HomeDao;
import com.khelacademy.www.pojos.*;
import com.khelacademy.www.services.ServiceUtil;
import com.khelacademy.www.utils.DBArrow;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class HomeDaoImpl implements HomeDao{
    @Override
    public Response getHome() throws SQLException {
        DBArrow SQLArrow = DBArrow.getArrow();
        ApiFormatter<Home> homeResponse;
        Home home = new Home();
        List<Banner> banners = new ArrayList<Banner>();
        List<Event> events = new ArrayList<Event>();
        Widget w = new Widget();
        PreparedStatement statement = SQLArrow.getPreparedStatement("SELECT * FROM event LIMIT 25");
        try (ResultSet rs = SQLArrow.fire(statement)) {
            while (rs.next()) {
                Banner b = new Banner();
                Event e = new Event();
                b.setEventId(rs.getInt("event_id"));
                b.setImageUrl(rs.getString("img_url"));
                b.setEventUrl(rs.getString("img_url"));
                e.setEventId(rs.getInt("event_id"));
                e.setEventName(rs.getString("event_name"));
                e.setPrice(100);
                e.setCity(rs.getString("venue"));
                banners.add(b);
                events.add(e);
            }
            w.setWidgets(events);
            home.setBanners(banners);
            home.setWidgets(w);
            homeResponse = ServiceUtil.convertToSuccessResponse(home);
            SQLArrow.relax(rs);
        }
        return Response.ok(new GenericEntity<ApiFormatter<Home>>(homeResponse) {
        }).build();
    }
}
