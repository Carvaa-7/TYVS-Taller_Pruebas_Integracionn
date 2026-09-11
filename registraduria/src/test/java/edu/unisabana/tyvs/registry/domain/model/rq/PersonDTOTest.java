package edu.unisabana.tyvs.registry.domain.model.rq;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * PRUEBA UNITARIA: PersonDTO es un DTO simple, pero su constructor con
 * parametros nunca se ejecuta en el resto del proyecto -- Jackson arma el
 * objeto con el constructor vacio + setters al deserializar el JSON del
 * request HTTP. Sin esta prueba, ese constructor queda sin cobertura.
 */
public class PersonDTOTest {

    @Test
    public void shouldBuildDtoWithAllArgsConstructor() {
        // Arrange & Act
        PersonDTO dto = new PersonDTO("Laura", 555, 28, "FEMALE", true);

        // Assert
        assertEquals("Laura", dto.getName());
        assertEquals(555, dto.getId());
        assertEquals(28, dto.getAge());
        assertEquals("FEMALE", dto.getGender());
        assertEquals(true, dto.isAlive());
    }

    @Test
    public void shouldBuildDtoWithNoArgsConstructorAndSetters() {
        // Arrange
        PersonDTO dto = new PersonDTO();

        // Act
        dto.setName("Pedro");
        dto.setId(777);
        dto.setAge(19);
        dto.setGender("MALE");
        dto.setAlive(false);

        // Assert
        assertEquals("Pedro", dto.getName());
        assertEquals(777, dto.getId());
        assertEquals(19, dto.getAge());
        assertEquals("MALE", dto.getGender());
        assertFalse(dto.isAlive());
    }
}