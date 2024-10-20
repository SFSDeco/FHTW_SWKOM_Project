package at.fhtw.rest.unitTests;

import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.mapper.DocumentMapper;
import at.fhtw.rest.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllDocuments() {
        // Arrange: Simuliere die Rückgabe einer leeren Liste von Dokumenten
        when(documentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act: Rufe die Methode auf
        String result = documentService.getAllDocuments();

        // Assert: Stelle sicher, dass das Ergebnis korrekt ist
        assertEquals("[]", result);
        verify(documentRepository, times(1)).findAll();
    }

    @Test
    public void testSaveDocument() {
        // Arrange: Simuliere das Speichern eines Dokuments
        DocumentEntity documentEntity = DocumentEntity.builder()
                .name("Test Document")
                .build();
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(documentEntity);

        // Act: Rufe die Methode auf
        documentService.saveDocument("Test Document");

        // Assert: Stelle sicher, dass die Speicherung korrekt erfolgt
        verify(documentRepository, times(1)).save(any(DocumentEntity.class));
    }
}