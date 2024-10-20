package at.fhtw.rest.unitTests;
import at.fhtw.rest.persistence.entity.DocumentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class DocumentValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidation() {
        DocumentEntity document = new DocumentEntity();
        document.setName(""); // Invalid name
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(document);
        assertFalse(violations.isEmpty());
    }

}
