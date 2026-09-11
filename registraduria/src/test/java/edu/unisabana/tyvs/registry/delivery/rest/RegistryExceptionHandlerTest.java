package edu.unisabana.tyvs.registry.delivery.rest;

import edu.unisabana.tyvs.registry.application.usecase.RegistryPersistenceException;
import org.junit.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.Assert.assertEquals;

/**
 * PRUEBA UNITARIA: los 3 manejadores de RegistryExceptionHandler traducen
 * excepciones a codigos HTTP. RegistryControllerIT (sistema, con
 * TestRestTemplate) solo dispara el de IllegalArgumentException (genero
 * invalido). Estas dos pruebas llaman los otros dos manejadores
 * directamente, sin levantar el contexto de Spring completo.
 */
public class RegistryExceptionHandlerTest {

    private final RegistryExceptionHandler handler = new RegistryExceptionHandler();

    @Test
    public void shouldReturnBadRequestOnMalformedJson() {
        // Arrange: el segundo argumento (HttpInputMessage) solo importa para
        // que Spring pueda volver a leer el body en logs; no hace falta uno
        // real para probar que el manejador traduce la excepcion a 400.
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("JSON invalido", (HttpInputMessage) null);

        // Act
        ResponseEntity<String> response = handler.handleMalformedJson(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("MALFORMED_JSON", response.getBody());
    }

    @Test
    public void shouldReturnServiceUnavailableOnPersistenceFailure() {
        // Arrange
        RegistryPersistenceException ex =
                new RegistryPersistenceException("Fallo de base de datos", new RuntimeException("causa"));

        // Act
        ResponseEntity<String> response = handler.handlePersistence(ex);

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("PERSISTENCE_ERROR", response.getBody());
    }
}