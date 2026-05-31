-- =============================================
-- 1. TẠO CƠ SỞ DỮ LIỆU
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'restaurant_db')
BEGIN
    CREATE DATABASE restaurant_db;
END;
GO

USE restaurant_db;
GO

-- =============================================
-- 2. TẠO CÁC BẢNG (TABLES) ĐỘC LẬP TRƯỚC
-- =============================================

-- Bảng Khách hàng
IF OBJECT_ID(N'dbo.tblClient', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblClient (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        phone NVARCHAR(20) NOT NULL,
        email NVARCHAR(100) NULL,
        address NVARCHAR(255) NULL,
        status NVARCHAR(20) NOT NULL CONSTRAINT DF_tblClient_status DEFAULT N'ACTIVE'
    );
END;
GO

-- Bảng Nhân viên / Người dùng
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

-- Bảng Bàn ăn
IF OBJECT_ID(N'dbo.tblTable', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblTable (
        id INT IDENTITY(1,1) PRIMARY KEY,
        tableCode NVARCHAR(20) NOT NULL UNIQUE,
        name NVARCHAR(100) NOT NULL,
        capacity INT NOT NULL,
        description NVARCHAR(255) NULL,
        status NVARCHAR(50) NOT NULL,
        isActive BIT NOT NULL CONSTRAINT DF_tblTable_isActive DEFAULT 1
    );
END;
GO

-- Bảng Món ăn / Thực đơn
IF OBJECT_ID(N'dbo.tblDish', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblDish (
        id INT IDENTITY(1,1) PRIMARY KEY,
        dishCode NVARCHAR(20) NOT NULL UNIQUE,
        category NVARCHAR(100) NULL,
        name NVARCHAR(100) NOT NULL,
        description NVARCHAR(500) NULL,
        price FLOAT NOT NULL,
        status NVARCHAR(20) NOT NULL CONSTRAINT DF_tblDish_status DEFAULT 'active'
    );
END;
GO

-- =============================================
-- 3. TẠO CÁC BẢNG PHỤ THUỘC (CÓ KHÓA NGOẠI - FK)
-- =============================================

-- Bảng Phiếu đặt bàn
IF OBJECT_ID(N'dbo.tblBooking', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblBooking (
        id INT IDENTITY(1,1) PRIMARY KEY,
        bookDate DATE NOT NULL,
        bookTime NVARCHAR(20) NOT NULL,
        quantity INT NOT NULL,
        status NVARCHAR(50) NOT NULL,
        tblClientId INT FOREIGN KEY REFERENCES dbo.tblClient(id),
        tblUserId INT FOREIGN KEY REFERENCES dbo.tblUser(id)
    );
END;
GO

-- Bảng Chi tiết Bàn đã đặt
IF OBJECT_ID(N'dbo.tblBookedTable', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblBookedTable (
        id INT IDENTITY(1,1) PRIMARY KEY,
        checkIn DATETIME NULL,
        checkOut DATETIME NULL,
        price FLOAT NULL,
        isCheckedIn BIT NOT NULL CONSTRAINT DF_tblBookedTable_isCheckedIn DEFAULT 0,
        tblBookingId INT FOREIGN KEY REFERENCES dbo.tblBooking(id),
        tblTableId INT FOREIGN KEY REFERENCES dbo.tblTable(id)
    );
END;
GO

-- Bảng Hóa đơn Gọi món
IF OBJECT_ID(N'dbo.tblOrder', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblOrder (
        id INT IDENTITY(1,1) PRIMARY KEY,
        orderTime DATETIME NOT NULL,
        totalAmount FLOAT NOT NULL CONSTRAINT DF_tblOrder_totalAmount DEFAULT 0,
        status NVARCHAR(50) NOT NULL,
        tblUserId INT FOREIGN KEY REFERENCES dbo.tblUser(id),
        tblTableId INT FOREIGN KEY REFERENCES dbo.tblTable(id),
        isPaid INT NOT NULL CONSTRAINT DF_tblOrder_isPaid DEFAULT 0
    );
END;
GO

-- Bảng Chi tiết Món đã gọi
IF OBJECT_ID(N'dbo.tblOrderDish', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblOrderDish (
        id INT IDENTITY(1,1) PRIMARY KEY,
        quantity INT NOT NULL,
        currentPrice FLOAT NOT NULL,
        tblOrderId INT FOREIGN KEY REFERENCES dbo.tblOrder(id),
        tblDishId INT FOREIGN KEY REFERENCES dbo.tblDish(id)
    );
END;
GO

-- Bảng Hóa đơn Thanh toán
IF OBJECT_ID(N'dbo.tblBill', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tblBill (
        id INT IDENTITY(1,1) PRIMARY KEY,
        createdTime DATETIME NULL,
        paymentDate DATE NULL,
        paymentTime VARCHAR(20) NULL,
        totalAmount FLOAT NOT NULL,
        paymentMethod NVARCHAR(50) NULL,
        tblOrderId INT FOREIGN KEY REFERENCES dbo.tblOrder(id),
        tblUserId INT FOREIGN KEY REFERENCES dbo.tblUser(id),
        tblBookingId INT FOREIGN KEY REFERENCES dbo.tblBooking(id)
    );
END;
GO

-- =============================================
-- 4. INSERT DỮ LIỆU MẪU (DUMMY DATA)
-- =============================================

-- Thêm Nhân viên
IF NOT EXISTS (SELECT 1 FROM dbo.tblUser WHERE username = N'admin')
BEGIN
    INSERT INTO dbo.tblUser (userCode, username, password, name, role, phone, email, status)
    VALUES (N'NV000', N'admin', N'123456', N'System Manager', N'MANAGER', N'0911000001', N'admin@restaurant.local', N'ACTIVE');
END;

IF NOT EXISTS (SELECT 1 FROM dbo.tblUser WHERE username = N'staff01')
BEGIN
    INSERT INTO dbo.tblUser (userCode, username, password, name, role, phone, email, status)
    VALUES (N'NV001', N'staff01', N'123', N'Nguyễn Kim An', N'STAFF', N'0123456789', N'annguyen@gmail.com', N'ACTIVE');
END;

IF NOT EXISTS (SELECT 1 FROM dbo.tblUser WHERE username = N'staff02')
BEGIN
    INSERT INTO dbo.tblUser (userCode, username, password, name, role, phone, email, status)
    VALUES (N'NV002', N'staff02', N'123', N'Trần Minh Tuấn', N'STAFF', N'0987654321', N'tuan.tran@gmail.com', N'ACTIVE');
END;

IF NOT EXISTS (SELECT 1 FROM dbo.tblUser WHERE username = N'staff03')
BEGIN
    INSERT INTO dbo.tblUser (userCode, username, password, name, role, phone, email, status)
    VALUES (N'NV003', N'staff03', N'123', N'Lê Hải Yến', N'STAFF', N'0911222333', N'yen.le@gmail.com', N'ACTIVE');
END;
GO

-- Thêm Khách hàng
IF NOT EXISTS (SELECT 1 FROM dbo.tblClient WHERE phone = N'0916385989')
BEGIN
    INSERT INTO dbo.tblClient (name, phone, email, address, status)
    VALUES 
    (N'An', N'0916385989', N'annguyen@gmail.com', N'Hoàng Mai, Hà Nội', N'ACTIVE'),
    (N'Nguyễn Văn Bình', N'0901234567', N'binh.nv@gmail.com', N'Cầu Giấy, Hà Nội', N'ACTIVE'),
    (N'Trần Thị Mai', N'0988777666', N'mai.tran@yahoo.com', N'Thanh Xuân, Hà Nội', N'ACTIVE'),
    (N'Lê Hoàng Long', N'0912345678', N'long.le@gmail.com', N'Đống Đa, Hà Nội', N'ACTIVE'),
    (N'Phạm Thu Thủy', N'0944555666', N'thuy.pham@hotmail.com', N'Hai Bà Trưng, Hà Nội', N'ACTIVE'),
    (N'Hoàng Vĩnh Khang', N'0977888999', N'khang.hoang@gmail.com', N'Ba Đình, Hà Nội', N'ACTIVE'),
    (N'Vũ Trọng Phụng', N'0933222111', N'phung.vu@gmail.com', N'Tây Hồ, Hà Nội', N'ACTIVE');
END;
GO

-- Thêm Bàn
IF NOT EXISTS (SELECT 1 FROM dbo.tblTable WHERE tableCode = N'T001')
BEGIN
    INSERT INTO dbo.tblTable (tableCode, name, capacity, description, status, isActive)
    VALUES 
    (N'T001', N'Bàn số 1', 4, N'Gần cửa sổ', N'Trống', 1),
    (N'T002', N'Bàn số 2', 6, N'Góc trong', N'Trống', 1),
    (N'T003', N'Bàn VIP 1', 10, N'Phòng lạnh', N'Trống', 1),
    (N'T004', N'Bàn số 4', 2, N'Bàn cặp đôi', N'Trống', 1),
    (N'T005', N'Bàn số 5', 2, N'Bàn cặp đôi', N'Trống', 1),
    (N'T006', N'Bàn số 6', 4, N'Giữa sảnh', N'Trống', 1),
    (N'T007', N'Bàn số 7', 4, N'Giữa sảnh', N'Trống', 1),
    (N'T008', N'Bàn số 8', 6, N'Gần quầy bar', N'Trống', 1),
    (N'T009', N'Bàn số 9', 6, N'Gần quầy bar', N'Trống', 1),
    (N'T010', N'Bàn VIP 2', 10, N'Phòng riêng cách âm', N'Trống', 1),
    (N'T011', N'Bàn VIP 3', 12, N'Phòng tiệc gia đình', N'Trống', 1),
    (N'T012', N'Bàn Ngoài Trời 1', 4, N'Ban công', N'Trống', 1),
    (N'T013', N'Bàn Ngoài Trời 2', 4, N'Ban công', N'Trống', 1);
END;
GO

-- Thêm Món ăn
IF NOT EXISTS (SELECT 1 FROM dbo.tblDish WHERE dishCode = N'D001')
BEGIN
    INSERT INTO dbo.tblDish (dishCode, category, name, price, status)
    VALUES 
    (N'D001', N'Món chính', N'Pho bo', 45000, N'active'),
    (N'D002', N'Món chính', N'Pho ga', 40000, N'active'),
    (N'D003', N'Món chính', N'Com rang', 50000, N'active'),
    (N'D004', N'Đồ uống', N'Trà đá', 5000, N'active'),
    (N'KV01', N'Khai vị', N'Salad cá hồi', 85000, N'active'),
    (N'KV02', N'Khai vị', N'Súp nấm hải sản', 65000, N'active'),
    (N'KV03', N'Khai vị', N'Nem rán Hà Nội', 55000, N'active'),
    (N'KV04', N'Khai vị', N'Khoai tây chiên', 40000, N'active'),
    (N'MC01', N'Món chính', N'Bò lúc lắc khoai tây', 120000, N'active'),
    (N'MC02', N'Món chính', N'Cá chép om dưa', 150000, N'active'),
    (N'MC03', N'Món chính', N'Gà nướng mật ong', 130000, N'active'),
    (N'MC04', N'Món chính', N'Lẩu Thái Tomyum', 250000, N'active'),
    (N'MC05', N'Món chính', N'Mực hấp gừng', 110000, N'active'),
    (N'MC06', N'Món chính', N'Sườn xào chua ngọt', 95000, N'active'),
    (N'DU01', N'Đồ uống', N'Nước ép cam tươi', 45000, N'active'),
    (N'DU02', N'Đồ uống', N'Sinh tố xoài', 50000, N'active'),
    (N'DU03', N'Đồ uống', N'Bia Heineken', 25000, N'active'),
    (N'DU04', N'Đồ uống', N'Nước suối', 15000, N'active'),
    (N'TM01', N'Tráng miệng', N'Chè hạt sen nhãn lồng', 35000, N'active'),
    (N'TM02', N'Tráng miệng', N'Kem tươi Vanilla', 30000, N'active'),
    (N'TM03', N'Tráng miệng', N'Trái cây theo mùa', 60000, N'active');
END;
GO
