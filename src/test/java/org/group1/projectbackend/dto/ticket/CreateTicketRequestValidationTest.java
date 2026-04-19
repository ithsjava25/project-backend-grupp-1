package org.group1.projectbackend.dto.ticket;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.group1.projectbackend.entity.enums.TicketPriority;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateTicketRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void shouldFailValidationWhenPriorityIsMissing() {
        CreateTicketRequest request = new CreateTicketRequest(
                "Broken VPN access",
                "Cannot connect to the company VPN from home.",
                null
        );

        Set<ConstraintViolation<CreateTicketRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v ->
                "priority".equals(v.getPropertyPath().toString())
                        && "Priority is required".equals(v.getMessage())));
    }

    @Test
    void shouldPassValidationWhenPriorityIsProvided() {
        CreateTicketRequest request = new CreateTicketRequest(
                "Broken VPN access",
                "Cannot connect to the company VPN from home.",
                TicketPriority.HIGH
        );

        Set<ConstraintViolation<CreateTicketRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}
