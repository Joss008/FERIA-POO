USE ComedorEPIS;
GO

-- 1. Si la tabla Alumnos ya existe, la borramos para crearla limpia
IF OBJECT_ID('Alumnos','U') IS NOT NULL
    DROP TABLE Alumnos;
GO

-- 2. Creamos la tabla con la estructura exacta que pide tu clase GestorAlumno.java
CREATE TABLE Alumnos
(
    codigo BIGINT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    carrera VARCHAR(50) NOT NULL,
    edad INT,
    faltas INT,
    horarioAprobado BIT -- Usamos BIT para el boolean (1 = true, 0 = false)
);
GO

-- 3. Insertamos a todos tus compañeros de la imagen
INSERT INTO Alumnos(codigo, nombre, apellido, carrera, edad, faltas, horarioAprobado)
VALUES
-- GRUPO J1
(2511130064, 'Jeff', 'Carbajal Vicente', 'Ing Sistemas', 19, 0, 1),
(2511020365, 'Nestor', 'Villalba Luyo', 'Ing Sistemas', 18, 0, 1),
(2511110362, 'Kevin', 'Vicente Vallejos', 'Ing Sistemas', 19, 0, 1),

-- GRUPO J2
(2511130208, 'Juan Esteban', 'Mendoza Atuncar', 'Ing Sistemas', 18, 0, 1),
(2411010196, 'Harold Axel', 'Salas Quispe', 'Ing Sistemas', 19, 0, 1),
(2511080284, 'Moisés', 'Rodríguez Gatica', 'Ing Sistemas', 18, 0, 1),

-- GRUPO J3
(2511130117, 'Yofree', 'Esteban Napa', 'Ing Sistemas', 19, 0, 1),
(2511080307, 'Javier Luis', 'Salazar', 'Ing Sistemas', 18, 0, 1),
(2511130151, 'Josie', 'Gomez Changanaqui', 'Ing Sistemas', 19, 0, 1),

-- GRUPO J4
(2511130240, 'Javier Alejandro', 'Paucar Ojeda', 'Ing Sistemas', 18, 0, 1),
(2511130319, 'Fabian Steven', 'Santibañez Mendoza', 'Ing Sistemas', 19, 0, 1),
(2511130219, 'Alexandra Nicole', 'Napa Cueva', 'Ing Sistemas', 18, 0, 1),

-- GRUPO J5
(2511110330, 'Valery', 'Soto Atauje', 'Ing Sistemas', 19, 0, 1),
(2511140401, 'Kevin', 'Vicente Castillo', 'Ing Sistemas', 18, 0, 1),
(2511130121, 'Eliana', 'Faustino Aldazabal', 'Ing Sistemas', 19, 0, 1),

-- GRUPO J6
(2511130022, 'Bryan Alessandro', 'Armas Mendoza', 'Ing Sistemas', 18, 0, 1),
(2511120167, 'Steven Daniel', 'Huacahuasi Vicente', 'Ing Sistemas', 19, 0, 1),

-- GRUPO J7
(2511080180, 'Dayra Shantall', 'Hurtado Quispe', 'Ing Sistemas', 18, 0, 1),
(2511110131, 'Jhon Kevin', 'Flores Vilcapuma', 'Ing Sistemas', 19, 0, 1),

-- GRUPO J8
(2511080011, 'Juan Braulio', 'Almeyda Peña', 'Ing Sistemas', 18, 0, 1),
(2511100325, 'Christian Diego', 'Sence Renojo', 'Ing Sistemas', 19, 0, 1),
(2511120216, 'Fabricio Joel', 'Miranda Quezada', 'Ing Sistemas', 18, 0, 1);
GO

-- 4. Consultas de prueba (Equivalentes a las de tu ejemplo original)

-- A) Ver toda la tabla completa
SELECT * FROM Alumnos;

-- B) Ver un listado con código, nombre completo y carrera
SELECT codigo, nombre, apellido, carrera FROM Alumnos;

-- C) Contar cuántos alumnos hay registrados por carrera
SELECT carrera, COUNT(*) as TotalAlumnos FROM Alumnos GROUP BY carrera;

-- D) Ver cuántos alumnos tienen 18 años y cuántos 19
SELECT edad, COUNT(*) as CantidadPorEdad FROM Alumnos GROUP BY edad;