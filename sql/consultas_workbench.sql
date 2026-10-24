-- ==========================================================
--  VetCitas - Consultas para MySQL Workbench
--  Las tablas las crea Spring Boot automáticamente al iniciar.
--  Ejecuta este script DESPUÉS de levantar la API una vez.
-- ==========================================================

USE veterinaria_db;

-- 1. Ver las tablas creadas por JPA
SHOW TABLES;

-- 2. Ver la estructura de cada tabla
DESCRIBE usuarios;
DESCRIBE citas;

-- 3. Comprobar que la contraseña está CIFRADA con BCrypt
--    (debe verse algo como $2a$10$...., nunca "admin123")
SELECT id, username, password, nombre_completo, rol FROM usuarios;

-- 4. Insertar citas de ejemplo (opcional, para la demostración)
INSERT INTO citas (nombre_mascota, especie, nombre_dueno, telefono_dueno, fecha_hora, motivo, veterinario, estado, fecha_registro)
VALUES
 ('Firulais', 'Perro', 'Juan Pérez',  '987654321', '2026-12-01 09:00:00', 'Vacuna antirrábica',  'Dra. Ana Torres', 'PENDIENTE', NOW()),
 ('Michi',    'Gato',  'María López', '912345678', '2026-12-01 10:30:00', 'Consulta general',    'Dr. Carlos Ruiz', 'PENDIENTE', NOW()),
 ('Rocky',    'Perro', 'Luis Ramos',  '955555555', '2026-12-02 16:00:00', 'Baño y corte',        'Dra. Ana Torres', 'PENDIENTE', NOW()),
 ('Nube',     'Conejo','Rosa Díaz',   '944444444', '2026-12-03 11:00:00', 'Control de peso',     'Dr. Carlos Ruiz', 'PENDIENTE', NOW());

-- 5. Listar todas las citas ordenadas por fecha
SELECT * FROM citas ORDER BY fecha_hora;

-- 6. Citas por estado
SELECT estado, COUNT(*) AS total FROM citas GROUP BY estado;

-- 7. Citas de un día específico
SELECT nombre_mascota, nombre_dueno, TIME(fecha_hora) AS hora, motivo
FROM citas
WHERE DATE(fecha_hora) = '2026-12-01'
ORDER BY fecha_hora;
