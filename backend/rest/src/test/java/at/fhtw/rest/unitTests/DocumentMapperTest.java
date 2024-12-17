/*package at.fhtw.rest.unitTests;

import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.mapper.DocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentMapperTest {

    private DocumentMapper documentMapper;

    @BeforeEach
    public void setUp() {
        documentMapper = new DocumentMapper();
    }

    @Test
    public void testMapToDto_SingleEntity() {
        // Arrange
        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(1L)
                .name("Test Document")
                .content("minio/path/to/document")
                .build();

        // Act
        DocumentDto documentDto = documentMapper.mapToDto(documentEntity);

        // Assert
        assertNotNull(documentDto);
        assertEquals(1L, documentDto.getId());
        assertEquals("Test Document", documentDto.getName());
        assertEquals("minio/path/to/document", documentDto.getContent());
    }

    @Test
    public void testMapToDto_Collection() {
        // Arrange
        DocumentEntity documentEntity1 = DocumentEntity.builder()
                .id(1L)
                .name("Document 1")
                .content("minio/path/to/document1")
                .build();

        DocumentEntity documentEntity2 = DocumentEntity.builder()
                .id(2L)
                .name("Document 2")
                .content("minio/path/to/document2")
                .build();

        List<DocumentEntity> entities = List.of(documentEntity1, documentEntity2);

        // Act
        List<DocumentDto> documentDtos = documentMapper.mapToDto(entities);

        // Assert
        assertNotNull(documentDtos);
        assertEquals(2, documentDtos.size());

        DocumentDto dto1 = documentDtos.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Document 1", dto1.getName());
        assertEquals("minio/path/to/document1", dto1.getContent());

        DocumentDto dto2 = documentDtos.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Document 2", dto2.getName());
        assertEquals("minio/path/to/document2", dto2.getContent());
    }

    @Test
    public void testMapToDto_EmptyCollection() {
        // Arrange
        List<DocumentEntity> entities = List.of();

        // Act
        List<DocumentDto> documentDtos = documentMapper.mapToDto(entities);

        // Assert
        assertNotNull(documentDtos);
        assertTrue(documentDtos.isEmpty());
    }
}*/
