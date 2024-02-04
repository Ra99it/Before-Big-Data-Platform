package de.kafka;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ImageDeserialization {
    @SuppressWarnings("unchecked")
    public static void img_save(String AvroPath) throws IOException {
        DatumReader<GenericRecord> imageDatumReader = new GenericDatumReader<>();
        DataFileReader<GenericRecord> imageDataFileReader = new DataFileReader<>(new File(AvroPath), imageDatumReader);

        ByteBuffer image = null;

        while (imageDataFileReader.hasNext()){
            image = (ByteBuffer) imageDataFileReader.next();

            UUID uuid = UUID.randomUUID();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(image.array());
            BufferedImage imageBuffer = ImageIO.read(inputStream);
            ImageIO.write(imageBuffer, "jpg", new File("D:\\Data\\picture\\byteToImage\\Image_"+uuid+".jpg"));
        }
    }

    public static void main(String[] args) throws IOException {
        img_save(".\\ML-DATA+1+0000000000+0000000002.avro");
    }

}
