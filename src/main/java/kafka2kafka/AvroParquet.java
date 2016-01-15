package kafka2kafka;

import io.gearpump.Message;
import io.gearpump.cluster.UserConfig;
import io.gearpump.streaming.javaapi.Task;
import io.gearpump.streaming.task.StartTime;
import io.gearpump.streaming.task.TaskContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import parquet.avro.AvroSchemaConverter;
import parquet.avro.AvroWriteSupport;
import parquet.hadoop.ParquetWriter;
import parquet.hadoop.api.WriteSupport;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.schema.MessageType;

import com.infobird.data.entity.User;

public class AvroParquet extends Task{

	private Logger LOG = Logger.getLogger("kafka2kafka.ReadAvroRecord");
	private static String schemaPath="/software/data/User.avsc";
	private static String outFile="/software/data/users.parquet";
	
	// set Parquet file block size and page size values
	private static int blockSize = 256 * 1024 * 1024;
	private static int pageSize = 64 * 1024;
	
	// choose compression scheme
	private static CompressionCodecName compressionCodecName = CompressionCodecName.SNAPPY;
	private static Path outPath;
	private static WriteSupport<IndexedRecord> writeSupport;
	
	public AvroParquet(TaskContext taskContext, UserConfig userConf) {
		super(taskContext, userConf);
	}

	@Override
	public void onStart(StartTime startTime) {
		LOG.info("ReadAvroRecord.onStart [" + startTime + "]");
		File file = new File(schemaPath);
		InputStream in;
			
			try {
				in = new FileInputStream(file);
				Schema avroSchema = new Schema.Parser().parse(in);
				System.out.println(new AvroSchemaConverter().convert(avroSchema).toString());
				
				// generate the corresponding Parquet schema
				MessageType parquetSchema = new AvroSchemaConverter().convert(avroSchema);
				 
				// create a WriteSupport object to serialize your Avro objects
				writeSupport = new AvroWriteSupport(parquetSchema, avroSchema);
				 
				outPath = new Path(outFile);
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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

			// the ParquetWriter object that will consume Avro GenericRecords
			ParquetWriter<IndexedRecord> parquetWriter = new ParquetWriter<IndexedRecord>(outPath,
			        writeSupport, compressionCodecName, blockSize, pageSize);
			
			while(!binaryDecoder.isEnd()) {
				user = datumReader.read(user, binaryDecoder);
				 parquetWriter.write(user);
				LOG.info("[ReadAvroRecord:] [user:] " + user);
			}
			parquetWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
