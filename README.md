# Parcial Final Programación N-Capas – (Seguridad con Spring Security + JWT)

Este repositorio contiene un proyecto para evaluar y practicar los conceptos de seguridad en aplicaciones Spring Boot usando JWT, roles y Docker.

### Estudiantes
- **Nombre del estudiante 1**: Julio Alberto Rodriguez Valencia - 00163922
- **Nombre del estudiante 2**: Allan Josue Lopez Escalante - 00049222
- Sección: 02
---

## Sistema de Soporte Técnico

### Descripción
Simula un sistema donde los usuarios pueden crear solicitudes de soporte (tickets) y los técnicos pueden gestionarlas. Actualmente **no tiene seguridad implementada**.

Su tarea es **agregar autenticación y autorización** utilizando **Spring Security + JWT**, y contenerizar la aplicación con Docker.

### Requisitos generales

- Proyecto funcional al ser clonado y ejecutado con Docker.
- Uso de PostgreSQL (ya incluido en docker-compose).
- Seguridad implementada con JWT.
- Roles `USER` y `TECH`.
- Acceso restringido según el rol del usuario.
- Evidencia de funcionamiento (colección de Postman/Insomnia/Bruno o capturas de pantalla).

**Nota: El proyecto ya tiene una estructura básica de Spring Boot con endpoints funcionales para manejar tickets. No es necesario modificar la lógica de negocio, solo agregar seguridad. Ademas se inclye un postman collection para probar los endpoints. **

_Si van a crear mas endpoints como el login o registrarse recuerden actualizar postman/insomnia/bruno collection_

### Partes de desarrollo

#### Parte 1: Implementar login con JWT
- [ ] Crear endpoint `/auth/login`.
- [ ] Validar usuario y contraseña (puede estar en memoria o en BD).
- [ ] Retornar JWT firmado.

#### Parte 2: Configurar filtros y validación del token
- [ ] Crear filtro para validar el token en cada solicitud.
- [ ] Extraer usuario desde el JWT.
- [ ] Añadir a contexto de seguridad de Spring.

#### Parte 3: Proteger endpoints con Spring Security
- [ ] Permitir solo el acceso al login sin token.
- [ ] Proteger todos los demás endpoints.
- [ ] Manejar errores de autorización adecuadamente.

#### Parte 4: Aplicar roles a los endpoints

| Rol   | Acceso permitido                                 |
|--------|--------------------------------------------------|
| USER  | Crear tickets, ver solo sus tickets              |
| TECH  | Ver todos los tickets, actualizar estado         |

- [ ] Usar `@PreAuthorize` o reglas en el `SecurityFilterChain`.
- [ ] Validar que un USER solo vea sus tickets.
- [ ] Validar que solo un TECH pueda modificar tickets.

#### Parte 5: Agregar Docker
- [ ] `Dockerfile` funcional para la aplicación.
- [ ] `docker-compose.yml` que levante la app y la base de datos.
- [ ] Documentar cómo levantar el entorno (`docker compose up`).

#### Parte 6: Evidencia de pruebas
- [ ] Probar todos los flujos con Postman/Insomnia/Bruno.
- [ ] Mostrar que los roles se comportan correctamente.
- [ ] Incluir usuarios de prueba (`user`, `tech`) y contraseñas.


# Sistema de Soporte Técnico  
**Parcial Final – Programación N-Capas (Spring Security + JWT + Docker)**  

> Simula un sistema donde los usuarios finales crean *tickets* de soporte y los técnicos los gestionan.  
> Incluye autenticación JWT, autorización por roles (`USER`, `TECH`) y despliegue con Docker Compose.

---

---

## Tecnologías

| Capa | Herramientas |
|------|--------------|
| Backend | Spring Boot 3.5, Spring Data JPA, Spring Security, JWT (jjwt 0.11), Lombok |
| Base de datos | PostgreSQL 15 |
| Contenerización | Docker 23 y Docker Compose v2 |
| Documentación API | Springdoc OpenAPI 2 |
| Build | Maven 3.9 (JDK 21) |

---

## Requisitos previos

* **JDK 21** (para ejecución local)  
* **Maven ≥3.9**  
* **Docker Desktop** (si usarás contenedores)  

---

## Seguridad (JWT)

* **/auth/login**  
  *Recibe* `correo` y `password` → Autentica con `AuthenticationManager`.  
  *Devuelve* token **Bearer** firmado SHA-256, válido `jwt.expirationMs` (ms).  
* Un filtro `JwtAuthenticationFilter` inspecciona cada petición:
  1. Extrae token del header `Authorization: Bearer …`  
  2. Lo valida y recupera el usuario  
  3. Inserta la autenticación en el `SecurityContext`  

---

## Roles y permisos

| Rol  | Permisos principales |
|------|----------------------|
| **USER** | • Crear tickets<br>• Ver **solo** sus tickets |
| **TECH** | • Ver **todos** los tickets<br>• Cambiar estado<br>• Editar/eliminar tickets<br>• CRUD de usuarios |

Las restricciones se aplican vía `@PreAuthorize` en los controladores y filtros.

---

## API REST

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| `POST` | `/auth/login` | Público | Login, devuelve JWT |
| `POST` | `/api/tickets` | USER | Crear ticket |
| `GET` | `/api/tickets` | USER / TECH | USER: propios · TECH: todos |
| `GET` | `/api/tickets/{id}` | USER / TECH | Detalle (USER solo propio) |
| `PUT` | `/api/tickets` | TECH | Actualizar estado/detalle |
| `DELETE` | `/api/tickets/{id}` | TECH | Eliminar ticket |
| `GET` | `/api/users/all` | TECH | Listar usuarios |
| `POST` | `/api/users` | TECH | Crear usuario |
| … | … | … | Resto de CRUD de usuarios |

> Consulta la colección Postman incluida para ejemplos de cuerpo y headers.

---

## Variables de entorno

| Variable | Predeterminado | Uso |
|----------|----------------|-----|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/supportdb` | Cadena JDBC |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Usuario BD |
| `SPRING_DATASOURCE_PASSWORD` | `admin` | Contraseña BD |
| `JWT_SECRET` (o `jwt.secret` en `yml`) | `MiSuperSecretoParaJWT...` | Clave de firma |
| `JWT_EXPIRATION_MS` | `3600000` | Tiempo de vida del token |

---

