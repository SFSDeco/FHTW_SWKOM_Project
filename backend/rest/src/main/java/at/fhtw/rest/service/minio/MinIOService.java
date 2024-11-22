package at.fhtw.rest.service.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinIOService {


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
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }
}
