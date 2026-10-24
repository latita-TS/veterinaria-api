# VetCitas API – Gestión de citas para una clínica veterinaria

API REST hecha con **Spring Boot 3.5 + Spring Data JPA + Spring Security (JWT + BCrypt) + Lombok + MySQL**, con pruebas automáticas, colección de Postman y despliegue con Docker en la nube (Render + Aiven MySQL).

---

## 1. ¿Qué hace el proyecto?

| Método | Ruta | ¿Necesita token? | Descripción |
|---|---|---|---|
| GET | `/` | No | Comprueba que la API está en línea |
| GET | `/actuator/health` | No | Monitoreo (DevOps): estado de la API y de la base de datos |
| POST | `/api/auth/login` | No | Login: valida usuario y contraseña (BCrypt) y devuelve un token JWT |
| POST | `/api/auth/registro` | No | Registra un usuario con contraseña cifrada |
| GET | `/api/citas` | Sí | Lista todas las citas |
| GET | `/api/citas/{id}` | Sí | Busca una cita por id |
| POST | `/api/citas` | Sí | Registra una cita |
| PUT | `/api/citas/{id}` | Sí | Actualiza una cita |
| DELETE | `/api/citas/{id}` | Sí | Elimina una cita |

Usuario inicial (se crea solo al arrancar): **admin / admin123**

**Formatos de intercambio:** la API responde en **JSON** por defecto. Si envías la cabecera `Accept: application/xml`, responde en **XML**.

---

## 2. Estructura en capas (MVC)

```
src/main/java/com/veterinaria/api
├── controller   → Recibe las peticiones HTTP (AuthController, CitaController, HomeController)
├── service      → Lógica del negocio (AuthService, CitaService y sus Impl)
├── repository   → Acceso a datos con Spring Data JPA (CitaRepository, UsuarioRepository)
├── entity       → Tablas de la BD (Cita, Usuario, EstadoCita)
├── dto          → Objetos de entrada/salida con validaciones
├── security     → JWT, filtro y configuración de Spring Security
├── exception    → Manejo centralizado de errores (400, 401, 404, 500)
└── config       → DataInitializer (crea el usuario admin)
```

---

## 3. Paso a paso para correrlo en tu PC

### Paso 1 – Levantar MySQL con Docker
1. Abre **Docker Desktop** y espera que diga **Engine running**.
2. Copia la carpeta `veterinaria-api` a `C:\proyectos\veterinaria-api`.
3. Abre la carpeta, haz clic en la barra de direcciones, escribe `powershell` y presiona Enter.
4. Ejecuta:
   ```
   docker compose up -d
   ```
5. Verifica que esté corriendo:
   ```
   docker ps
   ```
   Debe aparecer `vetcitas-mysql` con estado **Up**.

### Paso 2 – Conectar MySQL Workbench
1. Abre Workbench y haz clic en el **+** junto a "MySQL Connections".
2. Llena así:
   - Connection Name: `VetCitas Docker`
   - Hostname: `127.0.0.1`
   - Port: `3306`
   - Username: `vet_user`
   - Password: clic en **Store in Vault...** → `vet_pass123`
3. Clic en **Test Connection** → debe decir "Successfully made the MySQL connection".
4. Clic en **OK** y luego doble clic en la conexión.

### Paso 3 – Abrir el proyecto en Spring Tools
1. Abre Spring Tools.
2. Menú **File → Import... → Maven → Existing Maven Projects → Next**.
3. En **Root Directory** elige `C:\proyectos\veterinaria-api` → **Finish**.
4. Espera a que termine de descargar las librerías (barra de progreso abajo a la derecha). Necesitas internet.
5. Si aparece algún error rojo: clic derecho en el proyecto → **Maven → Update Project... → OK**.

### Paso 4 – Ejecutar la API
1. Clic derecho en el proyecto → **Run As → Spring Boot App**.
2. En la consola debe aparecer: `Started VeterinariaApiApplication`.
3. Abre en el navegador `http://localhost:8080/` → verás el mensaje "En línea".
4. En Workbench, actualiza el esquema `veterinaria_db`: ya aparecen las tablas `usuarios` y `citas`.

### Paso 5 – Ejecutar las pruebas (capa de acceso a datos)
1. Clic derecho en el proyecto → **Run As → JUnit Test**.
2. Debe salir la barra **verde** con todas las pruebas correctas:
   - `CitaRepositoryTest`: insertar, listar, actualizar, eliminar y buscar por estado.
   - `UsuarioRepositoryTest`: la contraseña se guarda cifrada con BCrypt.
   - `ApiIntegracionTest`: login correcto/incorrecto, validaciones y CRUD completo.
3. Las pruebas usan una base de datos H2 en memoria, así que no borran tus datos de MySQL.

### Paso 6 – Probar con Postman
1. Abre Postman → **Import** → elige `postman/VetCitas.postman_collection.json`.
2. Ejecuta primero **Autenticación → 1. Login (admin)**. El token se guarda solo.
3. Luego ejecuta en orden: **POST → GET → GET por id → PUT → DELETE**.
4. También están las pruebas de error: 401, 400 y 404.
5. Para correr todas juntas: clic derecho en la colección → **Run collection**.

### Paso 7 – Revisar los datos en Workbench
1. Abre el archivo `sql/consultas_workbench.sql` en Workbench (**File → Open SQL Script**).
2. Ejecuta las consultas con el rayo ⚡.
3. Muestra en la exposición que la contraseña del admin se ve como `$2a$10$...` (cifrada).

---

## 4. Publicar en la nube (Render + Aiven MySQL)

> Render ofrece plan gratuito para web services con Docker, pero su base de datos gratuita es PostgreSQL y expira a los 30 días. Por eso la base MySQL se aloja gratis en **Aiven**.

### Paso 8 – Subir el código a GitHub
1. Crea una cuenta en `https://github.com`.
2. Clic en **New repository** → nombre `veterinaria-api` → **Public** → **Create repository**.
3. Clic en **uploading an existing file** y arrastra TODO el contenido de la carpeta `veterinaria-api` (menos la carpeta `target`, si existe).
4. Clic en **Commit changes**.

> **Pipeline DevOps:** una vez conectado, cada vez que subas un cambio a GitHub, Render vuelve a construir la imagen Docker y publica la nueva versión automáticamente (integración y despliegue continuo).

### Paso 9 – Crear la base MySQL gratis en Aiven
1. Crea una cuenta en `https://aiven.io` (no pide tarjeta).
2. **Create service → MySQL → Free plan** → elige una región → **Create**.
3. Espera que el estado diga **Running**.
4. En **Connection information** copia: **Host**, **Port**, **User** (`avnadmin`), **Password** y **Database** (`defaultdb`).

### Paso 10 – Publicar la API en Render
1. Crea una cuenta en `https://render.com` (puedes entrar con GitHub).
2. **New + → Web Service** → conecta tu repositorio `veterinaria-api`.
3. Configura:
   - Language/Runtime: **Docker**
   - Instance Type: **Free**
   - Health Check Path (en "Advanced"): `/actuator/health`
4. En **Environment Variables** agrega:

   | Key | Value |
   |---|---|
   | `DB_URL` | `jdbc:mysql://HOST:PUERTO/defaultdb?sslMode=REQUIRED` |
   | `DB_USER` | `avnadmin` |
   | `DB_PASSWORD` | (la contraseña de Aiven) |
   | `JWT_SECRET` | una clave larga en Base64 (ver nota abajo) |
   | `ADMIN_PASSWORD` | la contraseña que quieras para el admin en la nube |

   Para generar el `JWT_SECRET`, abre PowerShell y ejecuta:
   ```
   [Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }))
   ```
   Copia el texto que aparece y pégalo como valor.

5. Clic en **Deploy Web Service** y espera (la primera vez tarda 5–10 minutos).
6. Cuando diga **Live**, abre la URL `https://veterinaria-api-xxxx.onrender.com/`.
7. En Postman, cambia la variable `baseUrl` de la colección por esa URL y repite las pruebas.

> En el plan gratis, si nadie usa la API por 15 minutos se "duerme" y la primera petición tarda cerca de 1 minuto. **Antes de exponer, abre la URL una vez para despertarla.**

---

## 5. Problemas comunes

| Problema | Solución |
|---|---|
| `Communications link failure` al iniciar | MySQL no está corriendo: abre Docker Desktop y ejecuta `docker compose up -d` |
| `Port 3306 already in use` | Tienes otro MySQL instalado. Detenlo o cambia `"3306:3306"` por `"3307:3306"` en `docker-compose.yml` y usa el puerto 3307 en `application.properties` y Workbench |
| `Port 8080 already in use` | Detén la otra instancia de la app (botón rojo ■ en la consola de Spring Tools) |
| Errores rojos en Spring Tools | Clic derecho → **Maven → Update Project** con "Force Update" marcado |
| Postman responde 401 | Vuelve a ejecutar el Login (el token dura 2 horas) |
