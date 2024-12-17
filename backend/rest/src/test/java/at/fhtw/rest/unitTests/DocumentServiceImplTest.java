package at.fhtw.rest.unitTests;

import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.impl.DocumentServiceImpl;
import at.fhtw.rest.service.mapper.DocumentMapper;
import at.fhtw.rest.service.minio.MinIOService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private MinIOService minIOService;

    @InjectMocks
    private DocumentServiceImpl documentServiceImpl;

    @Mock
    private MultipartFile mockFile;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveDocument_success() throws IOException {
        String documentName = "testDocument.pdf";
        String filePath = "path/to/file";
        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(1L)
                .name(documentName)
                .content("") // Initially empty, to be set after upload
                .build();

        DocumentDto documentDto = new DocumentDto(1L, documentName, filePath);

        // Mocking repository and MinIO service behavior
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(documentEntity);
        when(minIOService.uploadDocument(anyString(), eq(mockFile))).thenReturn(filePath);
        when(documentMapper.mapToDto(any(DocumentEntity.class))).thenReturn(documentDto);

        // Call the service method
        DocumentDto savedDocument = documentServiceImpl.saveDocument(documentName, mockFile);

        // Verify interactions
        //verify(documentRepository).save(any(DocumentEntity.class));
        verify(minIOService).uploadDocument(anyString(), eq(mockFile));
        verify(documentMapper).mapToDto(any(DocumentEntity.class));

        // Assert the result
        assertNotNull(savedDocument);
        assertEquals(1L, savedDocument.getId());
        assertEquals(documentName, savedDocument.getName());
        assertEquals(filePath, savedDocument.getContent());
    }

    @Test
    void testSaveDocument_failure_onMinIO() throws IOException {
        // Arrange
        String documentName = "testDocument.pdf";
        MultipartFile file = mock(MultipartFile.class);

        // Mock DocumentRepository save method
        DocumentEntity mockEntity = DocumentEntity.builder()
                .id(1L)
                .name(documentName)
                .content("")  // Placeholder for file path
                .build();
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(mockEntity);

        // Simulate MinIO failure (e.g., IOException)
        when(minIOService.uploadDocument(anyString(), eq(file)))
                .thenThrow(new IOException("MinIO upload failed"));

        // Act & Assert
        try {
            documentServiceImpl.saveDocument(documentName, file);
            fail("Expected RuntimeException due to MinIO failure");
        } catch (RuntimeException e) {
            assertEquals("Error uploading file to MinIO", e.getMessage());
        }

        // Verify interactions with the mocks
        verify(documentRepository).save(any(DocumentEntity.class));  // Ensure save was called
        verify(minIOService).uploadDocument(anyString(), eq(file));  // Ensure uploadDocument was called
    }

    @Test
    void testGetAllDocuments() {
        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(1L)
                .name("testDocument.pdf")
                .content("path/to/file")
                .build();

        DocumentDto documentDto = new DocumentDto(1L, "testDocument.pdf", "path/to/file");

        // Mock repository behavior
        when(documentRepository.findAll()).thenReturn(Collections.singletonList(documentEntity));

        // Mock mapping behavior for the list
        when(documentMapper.mapToDto(anyList())).thenReturn(Collections.singletonList(documentDto));

        // Call the service method
        List<DocumentDto> documents = documentServiceImpl.getAllDocuments();

        // Verify interactions
        verify(documentRepository).findAll();
        verify(documentMapper).mapToDto(anyList());

        // Assert the result
        assertNotNull(documents);
        assertEquals(1, documents.size());
        assertEquals("testDocument.pdf", documents.get(0).getName());
    }
}
