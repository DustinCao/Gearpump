package kafka2kafka;

import io.gearpump.Message;
import io.gearpump.cluster.UserConfig;
import io.gearpump.streaming.javaapi.Task;
import io.gearpump.streaming.task.StartTime;
import io.gearpump.streaming.task.TaskContext;

import java.io.IOException;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.Utf8;
import org.apache.log4j.Logger;

import com.infobird.data.entity.User;

public class ReadAvroObject extends Task{

	private Logger LOG = Logger.getLogger("kafka2kafka.ReadAvroRecord");
	
	public ReadAvroObject(TaskContext taskContext, UserConfig userConf) {
		super(taskContext, userConf);
	}

	@Override
	public void onStart(StartTime startTime) {
		LOG.info("ReadAvroRecord.onStart [" + startTime + "]");
	}
	
	@Override
	public void onNext(Message messagePayLoad) {
		LOG.info("ReadAvroRecord.onNext messagePayLoad = ["
				+ messagePayLoad + "]");
		LOG.debug("message.msg class"
				+ messagePayLoad.msg().getClass().getCanonicalName());
		
		Object msg = messagePayLoad.msg();		
		byte[] msgbytes = (byte[]) msg;
		LOG.info("[ReadAvroRecord:] [msg:]------------------" + msg);
		LOG.info("[ReadAvroRecord:] [value:]------------------" + msgbytes.length);
		LOG.info("[ReadAvroRecord:] [value:]------------------" + new String(msgbytes));
		
		SpecificDatumReader<User> datumReader = new SpecificDatumReader<User>(User.class);
		User user = null;
		BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(msgbytes,null);
		
		try {

			while(!binaryDecoder.isEnd()) {
				user = datumReader.read(user, binaryDecoder);
				
				Utf8 ageUtf = (Utf8)user.getAge();
				String age = new String(ageUtf.getBytes(),"Utf-8");
				
				Utf8 nameUtf = (Utf8)user.getName();
				String name = new String(nameUtf.getBytes(),"Utf-8");
				
				Utf8 genderUtf = (Utf8)user.getGender();
				String gender = new String(genderUtf.getBytes(),"Utf-8");
				
				LOG.info("[ReadAvroRecord:] [name:] " + name + "[age:] " + age +"[gender:] " + gender);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
