package com.infobird.test;

import java.io.IOException;
import java.util.Map;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.Utf8;

import redis.clients.jedis.Jedis;

import com.infobird.data.entity.User;
import com.infobird.redis.RedisManager;
import com.infobird.spark.parquet.entity.HiveUser;

public class TestMain {

	public static void main(String[] args) {
		Jedis jedis = RedisManager.getJedisConnection();
		
		String key = "qitongbao2";
	    
		Map<byte[], byte[]> values = jedis.hgetAll(key.getBytes());
		
		for (Map.Entry<byte[], byte[]> entry : values.entrySet()) {
			byte[] field = entry.getKey();
			
			
			byte[] value = entry.getValue();
			
			
			
			SpecificDatumReader<HiveUser> datumReader = new SpecificDatumReader<HiveUser>(HiveUser.class);
			HiveUser user = null;
			BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(value,null);
			
			try {
				System.out.println("field:" + new String(field,"Utf-8"));
				System.out.println("value:" + new String(value,"Utf-8"));
				while(!binaryDecoder.isEnd()) {
					user = datumReader.read(user, binaryDecoder);
					
					Utf8 ageUtf = (Utf8)user.getCitynameName();
					String age = new String(ageUtf.getBytes(),"Utf-8");
					
					Utf8 nameUtf = (Utf8)user.getHaoduanName();
					String name = new String(nameUtf.getBytes(),"Utf-8");
					
					Utf8 genderUtf = (Utf8)user.getTalkingTimeName();
					String gender = new String(genderUtf.getBytes(),"Utf-8");
					
					System.out.println("[ReadAvroRecord:] [name:] " + name + "[age:] " + age +"[gender:] " + gender);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
