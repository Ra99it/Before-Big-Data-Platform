package de.kafka;


import example.avro.Image;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

public class ImageProducer {
    public static void main(String[] args) throws IOException {
        String ImagePath_1 = "D:\\fastcampus\\de\\de-bigdata-project\\src\\main\\resources\\ML_IMAGES\\Image_0b00ce8d-3750-4836-864c-7580b89a1453.jpg";

        byte[] imageInByte;

        BufferedImage originalImage = ImageIO.read(new File(ImagePath_1));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        baos.flush();

        imageInByte = baos.toByteArray();
        ByteBuffer imageInByteBuffer = ByteBuffer.wrap(imageInByte);
        Image image_byte = new Image();
        image_byte.setTitle(ImagePath_1);
        image_byte.setImageData(imageInByteBuffer);
        baos.close();

        DatumWriter<Image> imageDatumWriter = new SpecificDatumWriter<>(Image.class);
        DataFileWriter<Image> dataFileWriter1 = new DataFileWriter<>(imageDatumWriter);
        dataFileWriter1.create(image_byte.getSchema(), new File("image.avro"));
        dataFileWriter1.append(image_byte);
        dataFileWriter1.close();

        //*******Producer******************
        Properties props = new Properties();

        String BOOTSTRAP_SERVER = "kafka-cluster-01:9092";
        String TOPIC_NAME = "Image-ML";

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        Producer<String, byte[]> producer = new KafkaProducer<>(props);

        ProducerRecord<String, byte[]> record = new ProducerRecord<>(TOPIC_NAME, image_byte.getImageData().array());
        producer.send(record);

        producer.flush();

        producer.close();
        //*********************************

    }
}
