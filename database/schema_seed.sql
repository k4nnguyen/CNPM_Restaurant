USE restaurant_db;
GO

IF OBJECT_ID(N'dbo.tblUser', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblUser (
        id INT IDENTITY(1,1) PRIMARY KEY,
        userCode NVARCHAR(20) NULL UNIQUE,
        username NVARCHAR(50) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        name NVARCHAR(100) NOT NULL,
        role NVARCHAR(20) NOT NULL,
        phone NVARCHAR(20) NULL,
        email NVARCHAR(100) NULL,
        status NVARCHAR(20) NOT NULL CONSTRAINT DF_tblUser_status DEFAULT N'ACTIVE'
    );
END;
GO

IF OBJECT_ID(N'dbo.tblClient', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblClient (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        phone NVARCHAR(20) NULL,
        email NVARCHAR(100) NULL,
        address NVARCHAR(255) NULL,
        status NVARCHAR(20) NOT NULL CONSTRAINT DF_tblClient_status DEFAULT N'ACTIVE'
    );
END;
GO

IF COL_LENGTH('dbo.tblClient', 'status') IS NULL
BEGIN
    ALTER TABLE dbo.tblClient
    ADD status NVARCHAR(20) NOT NULL CONSTRAINT DF_tblClient_status DEFAULT N'ACTIVE';
END;
GO

IF OBJECT_ID(N'dbo.tblTable', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblTable (
        id INT IDENTITY(1,1) PRIMARY KEY,
        tableCode NVARCHAR(20) NOT NULL UNIQUE,
        capacity INT NOT NULL,
        description NVARCHAR(255) NULL,
        status NVARCHAR(50) NOT NULL
    );
END;
GO

IF OBJECT_ID(N'dbo.tblDish', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblDish (
        id INT IDENTITY(1,1) PRIMARY KEY,
        dishCode NVARCHAR(20) NOT NULL UNIQUE,
        category NVARCHAR(100) NULL,
        name NVARCHAR(100) NOT NULL,
        price FLOAT NOT NULL
    );
END;
GO

IF OBJECT_ID(N'dbo.tblBooking', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblBooking (
        id INT IDENTITY(1,1) PRIMARY KEY,
        bookDate DATE NOT NULL,
        bookTime NVARCHAR(20) NOT NULL,
        quantity INT NOT NULL,
        status NVARCHAR(50) NOT NULL,
        tblClientId INT NOT NULL,
        tblUserId INT NOT NULL,
        CONSTRAINT FK_tblBooking_tblClient FOREIGN KEY (tblClientId) REFERENCES dbo.tblClient(id),
        CONSTRAINT FK_tblBooking_tblUser FOREIGN KEY (tblUserId) REFERENCES dbo.tblUser(id)
    );
END;
GO

IF OBJECT_ID(N'dbo.tblBookedTable', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblBookedTable (
        id INT IDENTITY(1,1) PRIMARY KEY,
        checkIn DATETIME NULL,
        checkOut DATETIME NULL,
        price FLOAT NULL,
        isCheckedIn BIT NOT NULL CONSTRAINT DF_tblBookedTable_isCheckedIn DEFAULT 0,
        tblBookingId INT NOT NULL,
        tblTableId INT NOT NULL,
        CONSTRAINT FK_tblBookedTable_tblBooking FOREIGN KEY (tblBookingId) REFERENCES dbo.tblBooking(id),
        CONSTRAINT FK_tblBookedTable_tblTable FOREIGN KEY (tblTableId) REFERENCES dbo.tblTable(id)
    );
END;
GO

IF OBJECT_ID(N'dbo.tblOrder', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblOrder (
        id INT IDENTITY(1,1) PRIMARY KEY,
        orderTime DATETIME NOT NULL,
        totalAmount FLOAT NOT NULL,
        status NVARCHAR(50) NOT NULL,
        tblUserId INT NOT NULL,
        tblTableId INT NOT NULL,
        CONSTRAINT FK_tblOrder_tblUser FOREIGN KEY (tblUserId) REFERENCES dbo.tblUser(id),
        CONSTRAINT FK_tblOrder_tblTable FOREIGN KEY (tblTableId) REFERENCES dbo.tblTable(id)
    );
END;
GO

IF OBJECT_ID(N'dbo.tblOrderDish', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblOrderDish (
        id INT IDENTITY(1,1) PRIMARY KEY,
        quantity INT NOT NULL,
        currentPrice FLOAT NOT NULL,
        tblOrderId INT NOT NULL,
        tblDishId INT NOT NULL,
        CONSTRAINT FK_tblOrderDish_tblOrder FOREIGN KEY (tblOrderId) REFERENCES dbo.tblOrder(id),
        CONSTRAINT FK_tblOrderDish_tblDish FOREIGN KEY (tblDishId) REFERENCES dbo.tblDish(id)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.tblUser WHERE username = N'admin')
BEGIN
    INSERT INTO dbo.tblUser (userCode, username, password, name, role, phone, email, status)
    VALUES (N'NV001', N'admin', N'123456', N'System Manager', N'MANAGER', N'0911000001', N'admin@restaurant.local', N'ACTIVE');
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.tblUser WHERE username = N'staff01')
BEGIN
    INSERT INTO dbo.tblUser (userCode, username, password, name, role, phone, email, status)
    VALUES (N'NV002', N'staff01', N'123456', N'Floor Staff One', N'STAFF', N'0911000002', N'staff01@restaurant.local', N'ACTIVE');
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.tblClient WHERE phone = N'0900000001')
BEGIN
    INSERT INTO dbo.tblClient (name, phone, email, address, status)
    VALUES (N'Walk-in Customer', N'0900000001', N'walkin@example.com', N'Restaurant counter', N'ACTIVE');
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.tblClient WHERE phone = N'0900000002')
BEGIN
    INSERT INTO dbo.tblClient (name, phone, email, address, status)
    VALUES (N'Nguyen Van An', N'0900000002', N'an.nguyen@example.com', N'12 Le Loi, District 1', N'ACTIVE');
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.tblTable WHERE tableCode = N'T001')
BEGIN
    INSERT INTO dbo.tblTable (tableCode, capacity, description, status)
    VALUES
        (N'T001', 2, N'Window table', N'Trống'),
        (N'T002', 4, N'Family table', N'Trống'),
        (N'T003', 6, N'Large table', N'Trống'),
        (N'T004', 4, N'Occupied demo table', N'Đang phục vụ');
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.tblDish WHERE dishCode = N'D001')
BEGIN
    INSERT INTO dbo.tblDish (dishCode, category, name, price)
    VALUES
        (N'D001', N'Main', N'Fried rice', 45000),
        (N'D002', N'Main', N'Beef noodle', 60000),
        (N'D003', N'Drink', N'Iced tea', 10000),
        (N'D004', N'Dessert', N'Flan', 25000);
END;
GO
