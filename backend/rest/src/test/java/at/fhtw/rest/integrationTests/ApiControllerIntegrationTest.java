/*package at.fhtw.rest.integrationTests;

import at.fhtw.rest.RestApplication;
import at.fhtw.rest.api.ApiController;
import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.service.DocumentService;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.rabbitmq.DocumentProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RestApplication.class)
@Testcontainers
public class ApiControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private ApiController apiController;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private DocumentProducer documentProducer;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setup() {
        Mockito.reset(documentService, documentProducer);
    }

    @Test
    void connectionToDatabase() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void testGetAllDocuments() {
        // Arrange
        DocumentDto documentDto = new DocumentDto();
        documentDto.setId(1L);
        documentDto.setName("Sample Document");
        when(documentService.getAllDocuments()).thenReturn(Collections.singletonList(documentDto));

        // Act
        List<DocumentDto> result = apiController.getAllDocuments();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Sample Document");
        verify(documentService).getAllDocuments();
    }

    @Test
    void testUploadDocument() {
        // Arrange
        String documentName = "TestDocument";
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Sample content".getBytes());

        // Simulate DocumentDto being returned after save
        DocumentDto documentDto = new DocumentDto();
        documentDto.setId(1L);
        documentDto.setName(documentName);

        when(documentService.saveDocument(any(), any())).thenReturn(documentDto);

        // Capture the DTO sent to the RabbitMQ producer
        ArgumentCaptor<DocumentDto> dtoCaptor = ArgumentCaptor.forClass(DocumentDto.class);

        // Act
        ResponseEntity<DocumentDto> response = apiController.uploadDocument(documentName, mockFile);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(documentDto);

        verify(documentService).saveDocument(documentName, mockFile);
        verify(documentProducer).sendDocumentEvent(dtoCaptor.capture());

        // Validate the correct DocumentDto was sent
        DocumentDto capturedDto = dtoCaptor.getValue();
        assertThat(capturedDto.getId()).isEqualTo(1L);
        assertThat(capturedDto.getName()).isEqualTo(documentName);
    }

    @Test
    void testUploadDocumentWithEmptyFile() {
        // Arrange
        String documentName = "EmptyFileDocument";
        MockMultipartFile mockFile = new MockMultipartFile("file", "", "text/plain", new byte[0]);

        // Simulate DocumentDto being returned after save
        DocumentDto documentDto = new DocumentDto();
        documentDto.setId(2L);
        documentDto.setName(documentName);

        when(documentService.saveDocument(any(), any())).thenReturn(documentDto);

        // Capture the DTO sent to the RabbitMQ producer
        ArgumentCaptor<DocumentDto> dtoCaptor = ArgumentCaptor.forClass(DocumentDto.class);

        // Act
        ResponseEntity<DocumentDto> response = apiController.uploadDocument(documentName, mockFile);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(documentDto);

        verify(documentService).saveDocument(documentName, mockFile);
        verify(documentProducer).sendDocumentEvent(dtoCaptor.capture());

        // Validate the correct DocumentDto was sent
        DocumentDto capturedDto = dtoCaptor.getValue();
        assertThat(capturedDto.getId()).isEqualTo(2L);
        assertThat(capturedDto.getName()).isEqualTo(documentName);
    }
}*/
