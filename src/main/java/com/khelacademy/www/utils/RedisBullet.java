package com.khelacademy.www.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
@Component
public class RedisBullet {
	private static String REDIS_URL;
	
    @Value("${redisUrl}")
	private void setDbUser(String REDIS_URL) {
    	RedisBullet.REDIS_URL = REDIS_URL;
	}
	public static JedisPool getPool() {
		  URI redisURI = null;
		try {
			redisURI = new URI(REDIS_URL);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  JedisPoolConfig poolConfig = new JedisPoolConfig();
		  poolConfig.setMaxTotal(20);
		  poolConfig.setMaxIdle(5);
		  poolConfig.setMinIdle(1);
		  poolConfig.setTestOnBorrow(true);
		  poolConfig.setTestOnReturn(true);
		  poolConfig.setTestWhileIdle(true);
		  JedisPool pool = new JedisPool(poolConfig, redisURI);
		  return pool;
		}

		// In your multithreaded code this is where you'd checkout a connection
		// and then return it to the pool
/*		try (Jedis jedis = pool.getResource()){
		  jedis.set("foo", "bar");
		}*/
}
