package at.fhtw.rest.unitTests;

import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.impl.DocumentServiceImpl;
import at.fhtw.rest.service.mapper.DocumentMapper;
import at.fhtw.rest.service.minio.MinIOService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private MinIOService minioService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveDocument_Success() throws IOException {
        // Arrange
        String documentName = "Test Document";
        String filePath = "minio/path/to/document";
        MultipartFile file = mock(MultipartFile.class);

        when(minioService.uploadDocument(documentName, file)).thenReturn(filePath);

        DocumentEntity savedEntity = DocumentEntity.builder()
                .name(documentName)
                .content(filePath)
                .build();

        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(savedEntity);

        DocumentDto documentDto = DocumentDto.builder()
                .name(documentName)
                .content(filePath)
                .build();

        when(documentMapper.mapToDto(any(DocumentEntity.class))).thenReturn(documentDto);

        // Act
        documentService.saveDocument(documentName, file);

        // Assert
        verify(minioService, times(1)).uploadDocument(documentName, file);
        verify(documentRepository, times(1)).save(any(DocumentEntity.class));
        verify(documentMapper, times(1)).mapToDto(any(DocumentEntity.class));
    }

    @Test
    public void testSaveDocument_ThrowsExceptionOnMinIOFailure() throws IOException {
        // Arrange
        String documentName = "Test Document";
        MultipartFile file = mock(MultipartFile.class);

        when(minioService.uploadDocument(documentName, file)).thenThrow(new IOException("MinIO upload failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                documentService.saveDocument(documentName, file));

        assertEquals("Error uploading file to MinIO", exception.getMessage());
        verify(minioService, times(1)).uploadDocument(documentName, file);
        verify(documentRepository, never()).save(any(DocumentEntity.class));
    }

    @Test
    public void testGetAllDocuments_ReturnsEmptyList() {
        // Arrange
        when(documentRepository.findAll()).thenReturn(Collections.emptyList());
        when(documentMapper.mapToDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<DocumentDto> result = documentService.getAllDocuments();

        // Assert
        assertTrue(result.isEmpty());
        verify(documentRepository, times(1)).findAll();
        verify(documentMapper, times(1)).mapToDto(Collections.emptyList());
    }

    @Test
    public void testGetAllDocuments_ReturnsListOfDocuments() {
        // Arrange
        DocumentEntity documentEntity = DocumentEntity.builder()
                .name("Document 1")
                .content("minio/path/to/document1")
                .build();

        List<DocumentEntity> entities = List.of(documentEntity);
        when(documentRepository.findAll()).thenReturn(entities);

        DocumentDto documentDto = DocumentDto.builder()
                .name("Document 1")
                .content("minio/path/to/document1")
                .build();

        when(documentMapper.mapToDto(entities)).thenReturn(List.of(documentDto));

        // Act
        List<DocumentDto> result = documentService.getAllDocuments();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Document 1", result.get(0).getName());
        assertEquals("minio/path/to/document1", result.get(0).getContent());

        verify(documentRepository, times(1)).findAll();
        verify(documentMapper, times(1)).mapToDto(entities);
    }
}
