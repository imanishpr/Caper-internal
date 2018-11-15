package com.khelacademy.daoImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.khelacademy.dao.SportsDao;
import com.khelacademy.www.pojos.ApiFormatter;
import com.khelacademy.www.pojos.Cities;
import com.khelacademy.www.pojos.MyErrors;
import com.khelacademy.www.pojos.Sports;
import com.khelacademy.www.services.ServiceUtil;
import com.khelacademy.www.utils.RedisBullet;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SportsDaoImpl implements SportsDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);
    JedisPool jedisPool = RedisBullet.getPool();
	Jedis jedis = jedisPool.getResource();
	//jedis.get(Integer.toString(rs.getInt("event_id")));
   
	@Override
	public Response getAllSports() {
		 List<Sports> sportsMap = new ArrayList<Sports>();
		try {
		    Set<String> names=jedis.keys("ACTIVE_GAMES_*");
		    Iterator<String> it = names.iterator();
		    while (it.hasNext()) {
		    	
		        String s = it.next();
		        Sports sport = new Sports();
		        sport.setSportsName(jedis.get(s));
		        sport.setSportsId(Integer.parseInt(s.substring(s.indexOf("ES_")+3, s.length())));
		        sportsMap.add(sport);
		    }
		}catch(Exception e) {
			LOGGER.error("Error in getting Cities "+ e.getMessage());
			MyErrors error = new MyErrors(e.getMessage());
        	ApiFormatter<MyErrors>  err= ServiceUtil.convertToFailureResponse(error, "true", 500);
            return Response.ok(new GenericEntity<ApiFormatter<MyErrors>>(err) {
            }).build();
		}finally {
			jedis.close();
			jedisPool.close();
		}

    	ApiFormatter<List<Sports>>  sprts= ServiceUtil.convertToSuccessResponse(sportsMap);
        return Response.ok(new GenericEntity<ApiFormatter<List<Sports>>>(sprts) {
        }).build();
	}
}
