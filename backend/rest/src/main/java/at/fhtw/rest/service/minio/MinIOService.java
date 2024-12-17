package at.fhtw.rest.service.minio;

import at.fhtw.rest.service.rabbitmq.DocumentProducer;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Log4j2
@Service
public class MinIOService {

    private static final Logger logger = LoggerFactory.getLogger(MinIOService.class);


    @Autowired
    private MinIOProperties properties;





    private MinioClient minioClient;

    @PostConstruct
    public void initializeMinioClient() {
        this.minioClient = MinioClient.builder()
                .endpoint(properties.getUrl())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }



    public String uploadDocument(String fileName, MultipartFile file) throws IOException {
        try (var inputStream = file.getInputStream()) {
            // Hochladen der Datei
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return properties.getBucketName() + "/" + fileName;
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.error("Error uploading file to MinIO. FileName: {}, Error: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    public byte[] downloadDocument(String filePath) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(properties.getBucketName())
                        .object(filePath)
                        .build())) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from MinIO", e);
        }
    }

    public void deleteDocument(String filePath) throws IOException {
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(filePath)
                    .build();


            minioClient.removeObject(args);
        } catch (Exception e) {
            throw new IOException("Failed to delete document from MinIO", e);
        }
    }
}
