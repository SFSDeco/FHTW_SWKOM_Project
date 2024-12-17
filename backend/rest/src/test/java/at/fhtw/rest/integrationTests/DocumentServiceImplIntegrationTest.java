/*package at.fhtw.rest.integrationTests;

import at.fhtw.rest.RestApplication;
import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.impl.DocumentServiceImpl;
import at.fhtw.rest.service.mapper.DocumentMapper;
import at.fhtw.rest.service.minio.MinIOService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RestApplication.class)
@Testcontainers
public class DocumentServiceImplIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private DocumentServiceImpl documentService;

    @MockBean
    private DocumentRepository documentRepository;

    @MockBean
    private DocumentMapper documentMapper;

    @MockBean
    private MinIOService minioService;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setup() {
        Mockito.reset(documentRepository, documentMapper, minioService);
    }

    @Test
    void testSaveDocument() throws IOException {
        // Arrange
        String documentName = "TestDocument";
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Sample content".getBytes());

        // Simulate MinIO file upload returning a path
        String filePath = "minio/test/path/test.txt";
        when(minioService.uploadDocument(eq(documentName), any(MultipartFile.class))).thenReturn(filePath);

        // Simulate saving to repository
        DocumentEntity savedEntity = DocumentEntity.builder()
                .name(documentName)
                .content(filePath)
                .build();
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(savedEntity);

        // Simulate mapping
        DocumentDto documentDto = new DocumentDto();
        documentDto.setName(documentName);
        when(documentMapper.mapToDto(any(DocumentEntity.class))).thenReturn(documentDto);

        // Act
        documentService.saveDocument(documentName, mockFile);

        // Assert
        Mockito.verify(minioService).uploadDocument(eq(documentName), any(MultipartFile.class));
        Mockito.verify(documentRepository).save(any(DocumentEntity.class));
        Mockito.verify(documentMapper).mapToDto(any(DocumentEntity.class));
    }

    @Test
    void testGetAllDocuments() {
        // Arrange
        DocumentEntity documentEntity = DocumentEntity.builder()
                .name("Sample Document")
                .content("minio/sample/path")
                .build();
        when(documentRepository.findAll()).thenReturn(Collections.singletonList(documentEntity));

        DocumentDto documentDto = new DocumentDto();
        documentDto.setName("Sample Document");
        when(documentMapper.mapToDto(Collections.singletonList(documentEntity)))
                .thenReturn(Collections.singletonList(documentDto));

        // Act
        List<DocumentDto> result = documentService.getAllDocuments();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Sample Document");
        Mockito.verify(documentRepository).findAll();
        Mockito.verify(documentMapper).mapToDto(Collections.singletonList(documentEntity));
    }
}*/
