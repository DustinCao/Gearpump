package kafka2kafka;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.log4j.Logger;

import com.infobird.data.entity.User;

import scala.Tuple2;
import io.gearpump.Message;
import io.gearpump.cluster.UserConfig;
import io.gearpump.streaming.javaapi.Task;
import io.gearpump.streaming.task.StartTime;
import io.gearpump.streaming.task.TaskContext;

public class ReadAvroRecord extends Task{

	private Logger LOG = Logger.getLogger("kafka2kafka.ReadAvroRecord");
	private static FileOutputStream fos;
	
	public ReadAvroRecord(TaskContext taskContext, UserConfig userConf) {
		super(taskContext, userConf);
	}

	@Override
	public void onStart(StartTime startTime) {
/*		File file = new File("/software/testUser.avro");
	    try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		LOG.info("ReadAvroRecord.onStart [" + startTime + "]");
	}
	
	@Override
	public void onNext(Message messagePayLoad) {
		LOG.info("ReadAvroRecord.onNext messagePayLoad = ["
				+ messagePayLoad + "]");
		LOG.debug("message.msg class"
				+ messagePayLoad.msg().getClass().getCanonicalName());
		
		Object msg = messagePayLoad.msg();		
		byte[] key = null;
		byte[] value = null;
		try {
			byte[] msgbytes = (byte[]) msg;
			LOG.info("[ReadAvroRecord:] [msg:]------------------" + msg);
			key = "message".getBytes("UTF-8");
			value = ("[changed:]"+ new String((byte[]) msg)).getBytes("UTF-8");
			
			//LOG.info("[ReadAvroRecord:] [key:]------------------" + key);
			LOG.info("[ReadAvroRecord:] [value:]------------------" + value.length);
			LOG.info("[ReadAvroRecord:] [value:]------------------" + new String(msgbytes));
			
			Schema schema = null;
			try {
				schema = new Schema.Parser()
						.parse(new File(
								"/software/data/User.avsc"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (schema == null)
				System.out.println("schema=is=null");
			// File file1 = new File("c:/users.avro");
			DatumReader<User> datumReader = new SpecificDatumReader<User>(
					schema);

		/*	InputStream in = new ByteArrayInputStream((byte[]) msg);

			Object decoded = datumReader.read(null, DecoderFactory.get()
				      .binaryDecoder(in, null));*/
			
			User user = datumReader.read(null, DecoderFactory.get()
				      .binaryDecoder(msgbytes, 0,msgbytes.length,null));
			//User user =(User)decoded;
			
			LOG.info("[ReadAvroRecord:] [user:]------------------" + user);
/*			try {
				LOG.info("[ReadAvroRecord:] [WRITE BEGION:]------------------" + (byte[]) msg);
				fos.write((byte[]) msg);
				LOG.info("[ReadAvroRecord:] [WRITE SUCCESEE:]------------------" + (byte[]) msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
/*			Tuple2<byte[], byte[]> tuple = new Tuple2<byte[], byte[]>(key,
					value);
			context.output(new Message(tuple, now()));*/
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			LOG.info("sending message as is.");
			//context.output(new Message(msg, now()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
