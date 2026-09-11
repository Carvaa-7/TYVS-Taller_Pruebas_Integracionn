# Matriz de Pruebas — Registraduría

| # | Caso | Entrada | Resultado esperado | Tipo | Test que lo valida |
|---|------|---------|---------------------|------|----------------------|
| 1 | Persona válida se registra | `Person("Ana", id=100, edad=30, FEMALE, viva)` | `VALID` | Mock | `RegistryWithMockTest.shouldSaveWhenPersonIsValid` |
| 2 | Persona válida (round-trip completo con H2 real) | `Person("Ana", id=100, edad=30, FEMALE, viva)` | `VALID` | Integración (H2) | `RegistryIT.shouldRegisterValidPerson` |
| 3 | Id duplicado es rechazado | Mismo `id` ya existente | `DUPLICATED` | Mock | `RegistryWithMockTest.shouldReturnDuplicatedWhenRepoSaysExists` |
| 4 | Duplicado real contra base de datos | Registrar `id=100` dos veces | `VALID` luego `DUPLICATED` | Integración (H2) | `RegistryIT.shouldPersistValidVoterAndRejectDuplicates` |
| 5 | Se puede recuperar un votante ya guardado | `findById(200)` tras registrar a Carlos | Registro con los mismos datos guardados | Integración (H2) | `RegistryIT.shouldFindPersistedVoterById` |
| 6 | Buscar un id que no existe | `findById(999)` sin registro previo | `Optional.empty()` | Integración (H2) | `RegistryIT.shouldReturnEmptyWhenVoterDoesNotExist` |
| 7 | Persona `null` | `registerVoter(null)` | `INVALID` | Mock | `RegistryWithMockTest.shouldReturnInvalidWhenPersonIsNull` |
| 8 | Id no positivo | `id = 0` | `INVALID` | Mock | `RegistryWithMockTest.shouldReturnInvalidWhenIdIsNotPositive` |
| 9 | Nombre vacío o en blanco *(defecto propio, ver `defectos.md`)* | `name = "   "` | `INVALID` | Mock | `RegistryWithMockTest.shouldReturnInvalidWhenNameIsBlank` |
| 10 | Persona fallecida | `alive = false` | `DEAD` | Mock | `RegistryWithMockTest.shouldReturnDeadWhenPersonIsNotAlive` |
| 11 | Edad negativa | `edad = -1` | `INVALID_AGE` (no `UNDERAGE`) | Mock | `RegistryWithMockTest.shouldReturnInvalidAgeWhenAgeIsNegative` |
| 12 | Edad supera el máximo | `edad = MAX_AGE + 1` | `INVALID_AGE` | Mock | `RegistryWithMockTest.shouldReturnInvalidAgeWhenAgeExceedsMaximum` |
| 13 | Edad cero (menor de edad) | `edad = 0` | `UNDERAGE` | Mock | `RegistryWithMockTest.shouldReturnUnderageWhenAgeIsZero` |
| 14 | Menor de edad general | `edad = 17` | `UNDERAGE` | Mock | `RegistryWithMockTest.shouldRejectUnderageWithoutTouchingRepository` |
| 15 | Valor límite superior exacto | `edad = MAX_AGE` | `VALID` | Mock | `RegistryWithMockTest.shouldAcceptTheMaximumAge` |
| 16 | Fallo de persistencia (SQLException) | Repositorio lanza `SQLException` al guardar | Se envuelve en `RegistryPersistenceException` | Mock | `RegistryWithMockTest.shouldWrapPersistenceFailure` |
| 17 | Registro válido vía HTTP | `POST /register` con JSON válido | `200 OK`, cuerpo `VALID` | Sistema (HTTP) | `RegistryControllerIT.shouldRegisterValidPerson` |
| 18 | Duplicado vía HTTP | `POST /register` con id ya registrado | `200 OK`, cuerpo `DUPLICATED` | Sistema (HTTP) | `RegistryControllerIT.shouldReturnDuplicatedWhenIdAlreadyRegistered` |
| 19 | Menor de edad vía HTTP | `POST /register`, edad < mínima | `200 OK`, cuerpo `UNDERAGE` | Sistema (HTTP) | `RegistryControllerIT.shouldReturnUnderageWhenPersonIsMinor` |
| 20 | Fallecido vía HTTP | `POST /register`, `alive=false` | `200 OK`, cuerpo `DEAD` | Sistema (HTTP) | `RegistryControllerIT.shouldReturnDeadWhenPersonIsNotAlive` |
| 21 | Género inválido vía HTTP | `gender: "OTRO_TEXTO"` | `400 BAD_REQUEST` | Sistema (HTTP) | `RegistryControllerIT.shouldReturnBadRequestWhenGenderIsNotValid` |
| 22 | JSON malformado | Body no parseable | `400 BAD_REQUEST`, `"MALFORMED_JSON"` | Unitaria | `RegistryExceptionHandlerTest.shouldReturnBadRequestOnMalformedJson` |
| 23 | Fallo de persistencia traducido a HTTP | `RegistryPersistenceException` capturada | `503 SERVICE_UNAVAILABLE`, `"PERSISTENCE_ERROR"` | Unitaria | `RegistryExceptionHandlerTest.shouldReturnServiceUnavailableOnPersistenceFailure` |
| 24 | Construcción de `PersonDTO` (constructor completo) | 5 parámetros | Getters devuelven los mismos valores | Unitaria | `PersonDTOTest.shouldBuildDtoWithAllArgsConstructor` |
| 25 | Construcción de `PersonDTO` (constructor vacío + setters) | Setear campo por campo | Getters devuelven lo seteado | Unitaria | `PersonDTOTest.shouldBuildDtoWithNoArgsConstructorAndSetters` |
| 26 | Contrato consumidor: registro válido | Certificados pide registrar un votante nuevo | `200 OK` según el pacto | Contract (Pact, consumidor) | `CertificadoServicePactTest` (interacción "un registro de votante valido") |
| 27 | Contrato consumidor: registro repetido | Certificados pide registrar un votante ya existente | `200 OK` según el pacto | Contract (Pact, consumidor) | `CertificadoServicePactTest` (interacción "un registro de votante repetido") |
| 28 | Verificación del proveedor: sin votante previo | Estado "no hay ningun votante registrado con id 900" | Cumple el pacto generado | Contract (Pact, proveedor) | `RegistraduriaProviderPactIT` (`@State` id 900) |
| 29 | Verificación del proveedor: votante ya registrado | Estado "ya existe un votante registrado con id 901" | Cumple el pacto generado | Contract (Pact, proveedor) | `RegistraduriaProviderPactIT` (`@State` id 901) |
| 30 | Persistir y rechazar duplicado contra PostgreSQL real | Igual al caso 4, pero con Testcontainers | `VALID` luego `DUPLICATED` | Testcontainers *(actualmente Skipped — sin Docker en el entorno)* | `RegistryRepositoryPostgresIT.shouldPersistAndRejectDuplicate` |
| 31 | Round-trip contra PostgreSQL real | Igual al caso 5, pero con Testcontainers | Datos leídos = datos escritos | Testcontainers *(Skipped)* | `RegistryRepositoryPostgresIT.shouldRoundTripRecord` |
| 32 | Nombre más largo que la columna | 150 caracteres en `VARCHAR(100)` | Lanza excepción | Testcontainers *(Skipped)* | `RegistryRepositoryPostgresIT.shouldRejectOversizedName` |
| 33 | Divergencia real H2 vs PostgreSQL (mayúsculas/minúsculas) | `SELECT "name"` entrecomillado en minúsculas | Funciona en PostgreSQL, fallaría en H2 | Testcontainers *(Skipped)* | `RegistryRepositoryPostgresIT.shouldResolveQuotedLowercaseIdentifier` |
| 34 | Confirmar que el motor es PostgreSQL | Metadata de la conexión | `"PostgreSQL"` | Testcontainers *(Skipped)* | `RegistryRepositoryPostgresIT.shouldBeRunningOnPostgres` |

**Nota sobre los casos 30–34:** están implementados y son correctos, pero en el entorno de desarrollo actual Docker Desktop no quedó disponible para Testcontainers, así que Maven los reporta como *Skipped* (no como fallidos). Se documenta como limitación de entorno, no como ausencia de la prueba.
