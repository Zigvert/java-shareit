package ru.practicum.shareit.user.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUserDto_noViolations() {
        UserDto user = new UserDto();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_violations() {
        UserDto user = new UserDto();
        user.setName("");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void invalidEmail_violations() {
        UserDto user = new UserDto();
        user.setName("John Doe");
        user.setEmail("invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void blankEmail_violations() {
        UserDto user = new UserDto();
        user.setName("John Doe");
        user.setEmail("");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}
