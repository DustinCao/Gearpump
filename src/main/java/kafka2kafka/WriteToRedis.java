package kafka2kafka;

import org.slf4j.Logger;

import com.infobird.redis.RedisManager;
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
		//String fields1 = messagePayLoad.productPrefix();
		
		Object msg = messagePayLoad.msg();	
		
		LOG.info("WriteToRedis === begion" + num + new String((byte[]) msg) + "[messagePayLoad.productPrefix():]" + messagePayLoad.productPrefix());
		
		
		RedisManager.putHash(KEY, fields, new String((byte[]) msg));
		
		num = num++;
		
		LOG.info("WriteToRedis === end" + num);
/*		num = num++;
		
		
	    pipeline = jedis.pipelined();

		RedisManager.putHashWithPipeline(KEY, "Message_"+num, new String((byte[]) msg), pipeline);
		
		if( 10000 == num) {
			num = 0;
			pipeline.exec();
		}*/
	
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
