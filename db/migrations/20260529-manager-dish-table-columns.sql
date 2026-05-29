IF COL_LENGTH('dbo.tblDish', 'description') IS NULL
BEGIN
    ALTER TABLE dbo.tblDish ADD description NVARCHAR(500) NULL;
END;

IF COL_LENGTH('dbo.tblDish', 'status') IS NULL
BEGIN
    ALTER TABLE dbo.tblDish ADD status NVARCHAR(20) NOT NULL
        CONSTRAINT DF_tblDish_status DEFAULT 'active';
END;

IF COL_LENGTH('dbo.tblTable', 'name') IS NULL
BEGIN
    ALTER TABLE dbo.tblTable ADD name NVARCHAR(100) NULL;
    EXEC('UPDATE dbo.tblTable SET name = tableCode WHERE name IS NULL');
    ALTER TABLE dbo.tblTable ALTER COLUMN name NVARCHAR(100) NOT NULL;
END;

IF COL_LENGTH('dbo.tblTable', 'isActive') IS NULL
BEGIN
    ALTER TABLE dbo.tblTable ADD isActive BIT NOT NULL
        CONSTRAINT DF_tblTable_isActive DEFAULT 1;
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'UX_tblDish_dishCode' AND object_id = OBJECT_ID('dbo.tblDish')
)
AND NOT EXISTS (
    SELECT dishCode FROM dbo.tblDish
    GROUP BY dishCode HAVING COUNT(*) > 1
)
BEGIN
    CREATE UNIQUE INDEX UX_tblDish_dishCode ON dbo.tblDish(dishCode);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'UX_tblTable_tableCode' AND object_id = OBJECT_ID('dbo.tblTable')
)
AND NOT EXISTS (
    SELECT tableCode FROM dbo.tblTable
    GROUP BY tableCode HAVING COUNT(*) > 1
)
BEGIN
    CREATE UNIQUE INDEX UX_tblTable_tableCode ON dbo.tblTable(tableCode);
END;
