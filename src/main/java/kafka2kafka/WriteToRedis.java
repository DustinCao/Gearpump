

package kafka2kafka;

import java.io.IOException;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.Utf8;
import org.slf4j.Logger;

import com.infobird.redis.RedisManager;
import com.infobird.spark.parquet.entity.HiveUser;
import com.infobird.utils.PropertiesUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import io.gearpump.Message;
import io.gearpump.cluster.UserConfig;
import io.gearpump.streaming.javaapi.Task;
import io.gearpump.streaming.task.StartTime;
import io.gearpump.streaming.task.TaskContext;

public class WriteToRedis extends Task{

	private Logger LOG = super.LOG();
	private static long num;
	private static Jedis jedis;
	private static String KEY = "CONNECT_CALL_INFO_HISTORY";
	private static Pipeline pipeline;
	
	public WriteToRedis(TaskContext taskContext, UserConfig userConf) {
		super(taskContext, userConf);
	}

	private Long now() {
		return System.currentTimeMillis();
	}
	
	@Override
	public void onStart(StartTime startTime) {
		num = 0;
		KEY = PropertiesUtil.getKeyValue("HASH_TABLE_NAME");
		LOG.info("WriteToRedis === [KEY:]" + KEY);
	    jedis = RedisManager.getJedisConnection();
		LOG.info("WriteToRedis.onStart [" + startTime + "]" + "[num:]" + num);
	}
	
	@Override
	public void onNext(Message messagePayLoad) {
		LOG.info("WriteToRedis.onNext messagePayLoad = ["
				+ messagePayLoad + "]");
		LOG.debug("message.msg class"
				+ messagePayLoad.msg().getClass().getCanonicalName());
		
		String fields = "Message_"+now();
		
		Object msg = messagePayLoad.msg();	
		byte[] msgbytes = (byte[]) msg;
		
		LOG.info("WriteToRedis === begion" + new String((byte[]) msg) + "[messagePayLoad.productPrefix():]" + messagePayLoad.productPrefix());

		SpecificDatumReader<HiveUser> datumReader = new SpecificDatumReader<HiveUser>(HiveUser.class);
		HiveUser user = null;
		BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(msgbytes,null);
		
		try {

			while(!binaryDecoder.isEnd()) {
				
				user = datumReader.read(user, binaryDecoder);
				
				Utf8 phoneUtf = (Utf8)user.getTelphoneName();
				String phone = new String(phoneUtf.getBytes(),"Utf-8");
				
				Utf8 cityUtf = (Utf8)user.getCitynameName();
				String city = new String(cityUtf.getBytes(),"Utf-8");
				
				Utf8 talkingUtf = (Utf8)user.getTalkingTimeName();
				String talking = new String(talkingUtf.getBytes(),"Utf-8");
				
				LOG.info("[ReadAvroRecord:] [phone:] " + phone + "[city:] " + city +"[talking:] " + talking);
				
				RedisManager.putHash(KEY, phone, city+";"+talking);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LOG.info("WriteToRedis === end" );
	
	}
	
	@Override
	public void onStop() {
		System.out.println("================onStop=====================begion");
		if(pipeline != null) {
			pipeline.exec();
		}
		
		System.out.println("================onStop=====================end");
	}

}
