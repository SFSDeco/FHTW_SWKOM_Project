package at.fhtw.rest.unitTests;
import at.fhtw.rest.persistence.entity.DocumentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentEntityTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidDocument() {
        // Arrange: Erstelle ein gültiges Dokument
        DocumentEntity document = DocumentEntity.builder()
                .id(1L)
                .name("Sample Document")
                .content("This is a valid document content.")
                .build();

        // Act: Führe die Validierung durch
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(document);

        // Assert: Stelle sicher, dass es keine Validierungsfehler gibt
        assertTrue(violations.isEmpty(), "There should be no validation errors.");
    }

    @Test
    public void testInvalidDocument_NoNameOrContent() {
        // Arrange: Erstelle ein ungültiges Dokument ohne Name und Inhalt
        DocumentEntity document = DocumentEntity.builder()
                .id(1L)
                .name("")  // Leerer Name
                .content("")  // Leerer Inhalt
                .build();

        // Act: Führe die Validierung durch
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(document);

        // Assert: Stelle sicher, dass es Validierungsfehler gibt
        assertFalse(violations.isEmpty(), "There should be validation errors.");

        // Optional: Überprüfe spezifische Fehlermeldungen
        violations.forEach(violation -> {
            System.out.println(violation.getMessage());
        });
    }
}
