-- ==============================================================
-- SCRIPT DE ACTUALIZACIÓN DE BASE DE DATOS: ComedorEPIS
-- Ejecuta este script en SQL Server para agregar la columna 'apellido'
-- ==============================================================

USE ComedorEPIS;
GO

-- 1. Si la tabla Alumnos ya existe, agregamos la columna 'apellido'
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Alumnos]') AND type in (N'U'))
BEGIN
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'apellido' AND Object_ID = OBJECT_ID(N'[dbo].[Alumnos]'))
    BEGIN
        ALTER TABLE Alumnos ADD apellido VARCHAR(100) NULL;
        PRINT 'Columna "apellido" agregada exitosamente a la tabla Alumnos.';
    END
    ELSE
    BEGIN
        PRINT 'La columna "apellido" ya existe en la tabla Alumnos.';
    END
END
ELSE
-- 2. Si la tabla Alumnos NO existe, la creamos desde cero
BEGIN
    CREATE TABLE Alumnos (
        codigo BIGINT PRIMARY KEY,
        nombre VARCHAR(100) NOT NULL,
        apellido VARCHAR(100) NOT NULL,
        carrera VARCHAR(100) NOT NULL,
        edad INT NOT NULL,
        faltas INT DEFAULT 0,
        horarioAprobado BIT DEFAULT 1
    );
    PRINT 'Tabla Alumnos creada desde cero con la columna "apellido".';
END
GO
