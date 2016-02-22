cd /software/gearpump-java-example
mvn package
scp target/streaming-java-template-1.2-SNAPSHOT-jar-with-dependencies.jar 10.122.74.163:/software
#ssh 10.122.74.163 /software/run.sh
