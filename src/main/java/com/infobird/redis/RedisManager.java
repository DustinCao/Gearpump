package com.infobird.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.infobird.utils.PropertiesUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class RedisManager {


	private static String host = "10.122.74.204";
	private static int port = 6388;
	private static int database = 1;
	private static String password = null;

	private static Jedis jedis;
	private static Pipeline pipeline;
	private static final Logger logger = Logger.getLogger("com.infobird.hive.HiveDemo"); 
	
	public static Jedis getJedisConnection() {
		host = PropertiesUtil.getKeyValue("REDIS_HOST");
		port = Integer.parseInt(PropertiesUtil.getKeyValue("REDIS_PORT"));
		database = Integer.parseInt(PropertiesUtil.getKeyValue("REDIS_DATABASE"));
		
	    password = "".equals(PropertiesUtil.getKeyValue("REDIS_PASSWORD"))? null : PropertiesUtil.getKeyValue("REDIS_PASSWORD");
	    System.out.println("host:" + host + "port:" + port + "database:" + database + "password:" + password);
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		JedisPool jedisPool = new JedisPool(poolConfig, host,
				port,0,password,database);
		jedis = jedisPool.getResource();
		
		return jedis;
	}
	
	
	
	public static void putHash(String key, String field, String value) {
		jedis.hset(key, field, value);
	}
	
	public static void putHashWithPipeline(String key,String field, String value, Pipeline pipeline) {
		
		pipeline.hset(key, field, value);
	}
	
	public static void putHashByte(byte[] key, byte[] field, byte[] value) {
		jedis.hset(key, field, value);
	}
	
	public static void putHashByteWithPipeline(byte[] key,byte[] field, byte[] value, Pipeline pipeline) {
		
		pipeline.hset(key, field, value);
	}
	/**
	 * @category 放一批数据
	 * @param key
	 * @param maps
	 * @return
	 */
	public static String putHash(String key, Map<String, String> maps) {
		
		Response<String> response = null;
		if(maps != null && maps.size() > 0) {
			response = pipeline.hmset(key, maps);
			pipeline.sync();
		}

		return response != null ? response.get() : null;
	}
	
	public static Map<String, String> getDataByKey(String key, String... fields) {

		logger.debug("[RedisOperation] [getDataByKey] [key:]" + key
				+ "--begin--");

		if(fields != null && fields.length > 0) {
			
			Response<List<String>> response = pipeline.hmget(key, fields);
			pipeline.sync();
			
			Map<String, String> map = new HashMap<String, String>();
			if (response != null) {
				List<String> list = response.get();
				
				logger.debug("[RedisOperation] [getDataByKey] [response.get().size():]"
				              + list.size() + "--");
				
				for (int i = 0; i < list.size(); i++) {
					map.put(fields[i], list.get(i));
				}
				
				return map;
			}
		}
		logger.debug("[RedisOperation] [getDataByKey] [key:]" + key + "--end--");

		return null;
	}
	
	
	public static long lpush(String key, String... values) {
		
		return jedis.lpush(key, values);
	}
	
	public static String lpop(String key) {
		return jedis.lpop(key);
	}
	
	public static Map<String, String> hgetall(String key) {
		return jedis.hgetAll(key);
	}
}
