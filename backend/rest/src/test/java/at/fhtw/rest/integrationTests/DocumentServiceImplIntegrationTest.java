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

    // Use PostgreSQLContainer to simulate a real database environment
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    // Inject the service and mock the required beans
    @Autowired
    private DocumentServiceImpl documentService;

    @MockBean
    private DocumentRepository documentRepository;

    @MockBean
    private DocumentMapper documentMapper;

    @MockBean
    private MinIOService minioService;

    // Configure dynamic properties for the test container
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    // Reset mocks before each test to ensure clean state
    @BeforeEach
    void setup() {
        Mockito.reset(documentRepository, documentMapper, minioService);
    }

    @Test
    void testSaveDocument() throws IOException {
        // Arrange: Prepare test data
        String documentName = "TestDocument";
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Sample content".getBytes());

        // Simulate MinIO file upload returning a path
        String filePath = "minio/test/path/test.txt";
        when(minioService.uploadDocument(eq(documentName), any(MultipartFile.class))).thenReturn(filePath);

        // Simulate saving the document entity to the repository
        DocumentEntity savedEntity = DocumentEntity.builder()
                .id(1L) // Set an ID to simulate saved entity
                .name(documentName)
                .content(filePath)
                .build();
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(savedEntity);

        // Simulate mapping from entity to DTO
        DocumentDto documentDto = new DocumentDto();
        documentDto.setId(1L);  // Ensure ID is set
        documentDto.setName(documentName);
        when(documentMapper.mapToDto(any(DocumentEntity.class))).thenReturn(documentDto);

        // Act: Call the saveDocument method
        DocumentDto result = documentService.saveDocument(documentName, mockFile);

        // Assert: Verify expected interactions and results
        Mockito.verify(minioService).uploadDocument(eq("1_TestDocument"), any(MultipartFile.class));
        Mockito.verify(documentRepository).save(any(DocumentEntity.class));
        Mockito.verify(documentMapper).mapToDto(any(DocumentEntity.class));

        // Assert the result of the method call
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(documentName);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testGetAllDocuments() {
        // Arrange: Prepare test data
        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(1L)  // Add ID for the document entity
                .name("Sample Document")
                .content("minio/sample/path")
                .build();

        // Simulate repository returning a list of documents
        when(documentRepository.findAll()).thenReturn(Collections.singletonList(documentEntity));

        // Simulate mapping from entity to DTO
        DocumentDto documentDto = new DocumentDto();
        documentDto.setId(1L);  // Ensure ID is set
        documentDto.setName("Sample Document");
        when(documentMapper.mapToDto(Collections.singletonList(documentEntity)))
                .thenReturn(Collections.singletonList(documentDto));

        // Act: Call the getAllDocuments method
        List<DocumentDto> result = documentService.getAllDocuments();

        // Assert: Verify expected interactions and results
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Sample Document");
        Mockito.verify(documentRepository).findAll();
        Mockito.verify(documentMapper).mapToDto(Collections.singletonList(documentEntity));
    }
}*/
