# Registro de Defectos — Taller de Pruebas de Integración y Sistema

Este documento registra los defectos detectados durante la ejecución de pruebas unitarias, de integración y de sistema del proyecto **Registraduría**.

---

### Defecto 01 — Nombre vacío o en blanco aceptado como votante válido *(Prueba unitaria con Mockito)*

- **Caso de prueba:** `RegistryWithMockTest.shouldReturnInvalidWhenNameIsBlank()`
- **Entrada:** `Person(name="   ", id=16, age=25, gender=MALE, alive=true)`
- **Resultado esperado:** `INVALID` — un nombre vacío o en blanco es un dato mal capturado, no un votante real.
- **Resultado obtenido (versión defectuosa):** `VALID` — `Registry.registerVoter()` no validaba el nombre en absoluto y dejaba pasar el registro hasta guardarlo.
- **Causa probable:** faltaba una validación de `p.getName()` en `Registry.registerVoter()`, entre la validación del `id` y la de `alive`.
- **Tipo de prueba:** Unitaria (con mock de `RegistryRepositoryPort`, sin tocar base de datos).
- **Estado:** **Resuelto.**

**Corrección aplicada** en `Registry.java`:

```java
if (p.getName() == null || p.getName().isBlank())
    return RegisterResult.INVALID;
```

**Evidencia — antes de corregir (test en rojo):**

```
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 1.969 s <<< FAILURE! -- in edu.unisabana.tyvs.registry.application.usecase.RegistryWithMockTest
[ERROR] edu.unisabana.tyvs.registry.application.usecase.RegistryWithMockTest.shouldReturnInvalidWhenNameIsBlank -- Time elapsed: 1.937 s <<< FAILURE!
java.lang.AssertionError: expected:<INVALID> but was:<VALID>
	at org.junit.Assert.fail(Assert.java:89)
	at org.junit.Assert.failNotEquals(Assert.java:835)
	at org.junit.Assert.assertEquals(Assert.java:120)
	at org.junit.Assert.assertEquals(Assert.java:146)
	at edu.unisabana.tyvs.registry.application.usecase.RegistryWithMockTest.shouldReturnInvalidWhenNameIsBlank(RegistryWithMockTest.java:233)
...
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

**Evidencia — después de corregir (test en verde):**

```
[INFO] Running edu.unisabana.tyvs.registry.application.usecase.RegistryWithMockTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.897 s -- in edu.unisabana.tyvs.registry.application.usecase.RegistryWithMockTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Verificación de regresión:** tras el fix, `mvn clean verify` se corrió completo (16 tests, 0 fallos, 5 skipped por Testcontainers/Docker) confirmando que la corrección no rompió ninguna otra prueba.