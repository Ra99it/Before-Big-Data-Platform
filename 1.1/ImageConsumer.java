package de.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ImageConsumer {
    public static void main(String[] args) {
        //*******Consumer******************
        Properties props = new Properties();

        String BOOTSTRAP_SERVERS = "kafka-cluster-01:9092,kafka-cluster-02:9092,kafka-cluster-03:9092";
        String TOPIC_NAME = "image";

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test2");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        Consumer<String, byte[]> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        try {
            while(true){
                ConsumerRecords<String,byte[]> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String,byte[]> record : records){
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(record.value());
                    BufferedImage image = ImageIO.read(inputStream);
                    ImageIO.write(image, "jpg", new File("D:\\Data\\picture\\some.jpg"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            consumer.close();
        }
        //*******Consumer******************
    }
}
