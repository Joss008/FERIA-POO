-- ==============================================================
-- SCRIPT DE MIGRACIÓN v2 — ComedorEPIS
-- Ejecuta este script ANTES de correr el programa actualizado
-- ==============================================================

USE ComedorEPIS;
GO

-- ============================================================
-- 1. Renombrar columna 'carrera' → 'ciclo' en tabla Alumnos
-- ============================================================
IF EXISTS (
    SELECT * FROM sys.columns
    WHERE Name = N'carrera' AND Object_ID = OBJECT_ID(N'[dbo].[Alumnos]')
)
BEGIN
    EXEC sp_rename 'Alumnos.carrera', 'ciclo', 'COLUMN';
    PRINT 'Columna "carrera" renombrada a "ciclo" exitosamente.';
END
ELSE
BEGIN
    PRINT 'La columna "carrera" no existe (ya fue renombrada o no aplica).';
END
GO

-- ============================================================
-- 2. Agregar columna 'diasSolicitados' a tabla Alumnos
-- ============================================================
IF NOT EXISTS (
    SELECT * FROM sys.columns
    WHERE Name = N'diasSolicitados' AND Object_ID = OBJECT_ID(N'[dbo].[Alumnos]')
)
BEGIN
    ALTER TABLE Alumnos ADD diasSolicitados VARCHAR(100) NULL;
    PRINT 'Columna "diasSolicitados" agregada exitosamente.';
END
ELSE
BEGIN
    PRINT 'La columna "diasSolicitados" ya existe.';
END
GO

-- ============================================================
-- 3. Crear tabla ReportesFalta (para justificaciones)
-- ============================================================
IF OBJECT_ID('ReportesFalta', 'U') IS NULL
BEGIN
    CREATE TABLE ReportesFalta (
        id          INT IDENTITY(1,1) PRIMARY KEY,
        codigo      BIGINT NOT NULL,
        justificacion VARCHAR(500) NOT NULL,
        fecha       DATETIME DEFAULT GETDATE()
    );
    PRINT 'Tabla "ReportesFalta" creada exitosamente.';
END
ELSE
BEGIN
    PRINT 'La tabla "ReportesFalta" ya existe.';
END
GO

-- ============================================================
-- 4. Verificación final
-- ============================================================
SELECT 'Alumnos' AS Tabla, COLUMN_NAME, DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Alumnos'
UNION ALL
SELECT 'ReportesFalta', COLUMN_NAME, DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'ReportesFalta';
GO
