# Restaurant Module

README này dùng để clone project, tạo CSDL, chạy app, và hiểu rõ những thay đổi đã được tích hợp thêm vào project gốc.

## 1. Mục tiêu hiện tại

Project hiện tại gồm 2 nhóm chức năng:

- Staff module của project nhóm gốc:
  - đặt bàn
  - tìm bàn trống
  - tìm và thêm khách hàng cho booking
  - sửa booking
  - gọi món
  - xác nhận order
- Manager module được tích hợp thêm:
  - login
  - phân quyền `MANAGER` / `STAFF`
  - quản lý khách hàng
  - quản lý nhân viên

Luồng chạy hiện tại:

- `MANAGER` đăng nhập -> vào màn hình manager
- `STAFF` đăng nhập -> vào `StaffHomeFrm`

## 2. Yêu cầu môi trường

Cần có các thành phần sau:

- Windows
- JDK 17 hoặc mới hơn
- SQL Server đang chạy local
- SSMS hoặc công cụ SQL tương đương

Project đã kèm sẵn JDBC driver trong thư mục `lib`, nên không cần cài thêm thư viện JDBC bằng tay để chạy app.

## 3. Cấu hình CSDL

Project đang dùng database:

- `restaurant_db`

Project đang kết nối SQL Server local theo thứ tự ưu tiên sau:

1. JDBC URL được override bằng system property hoặc environment variable
2. `jdbc:sqlserver://localhost;databaseName=restaurant_db;encrypt=true;trustServerCertificate=true;`
3. `jdbc:sqlserver://localhost:1433;databaseName=restaurant_db;encrypt=true;trustServerCertificate=true;`
4. `jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=restaurant_db;encrypt=true;trustServerCertificate=true;`

Mặc định hiện tại cho SQL login trong app:

- username: `restaurant_app`
- password: `123456`

Nếu máy khác không có login này, có 2 cách:

1. Tạo login `restaurant_app`
2. Hoặc override login của app bằng thông tin SQL login khác

## 4. Tạo database trên máy mới

Mở SSMS và chạy:

```sql
CREATE DATABASE restaurant_db;
```

Nếu database đã tồn tại thì bỏ qua bước này.

## 5. Tạo SQL login cho app

Bước này quan trọng. `schema_seed.sql` chỉ tạo bảng và seed dữ liệu trong database, nhưng không tạo SQL login cấp server.

Nếu máy đã có login `restaurant_app` thì bỏ qua.

Nếu chưa có, đăng nhập SSMS bằng Windows Authentication rồi chạy lần lượt:

```sql
USE master;
GO

IF NOT EXISTS (SELECT 1 FROM sys.sql_logins WHERE name = 'restaurant_app')
BEGIN
    CREATE LOGIN restaurant_app
    WITH PASSWORD = '123456',
         CHECK_POLICY = OFF,
         CHECK_EXPIRATION = OFF;
END;
GO
```

Sau đó map login vào database:

```sql
USE restaurant_db;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'restaurant_app')
BEGIN
    CREATE USER restaurant_app FOR LOGIN restaurant_app;
END;
GO

ALTER ROLE db_owner ADD MEMBER restaurant_app;
GO
```

Nếu không muốn dùng `restaurant_app`, có thể giữ login SQL khác và override trong lúc chạy app. Cách override nằm ở mục 9.

## 6. Tạo bảng và seed dữ liệu

Trong SSMS:

1. Chọn database `restaurant_db`
2. Mở file `database/schema_seed.sql`
3. Bấm `Execute`

File này sẽ tạo hoặc cập nhật các bảng:

- `tblUser`
- `tblClient`
- `tblTable`
- `tblDish`
- `tblBooking`
- `tblBookedTable`
- `tblOrder`
- `tblOrderDish`

File này cũng seed dữ liệu demo:

- user `admin / 123456` với role `MANAGER`
- user `staff01 / 123456` với role `STAFF`
- một số client mẫu
- một số table mẫu
- một số dish mẫu

Lưu ý:

- `tblClient` đã có thêm cột `status` để phục vụ xóa mềm trong manager module
- `tblUser` là bảng mới được thêm để phục vụ login và quản lý nhân viên

## 7. Cách chạy app

Khuyến nghị dùng script thay vì bấm Run trực tiếp từ IDE.

### Cách 1: file batch

Chạy:

```bat
run-app.bat
```

### Cách 2: PowerShell

Chạy:

```powershell
powershell -ExecutionPolicy Bypass -File .\run-app.ps1
```

Script sẽ tự động:

1. xóa class cũ trong `target/classes`
2. compile lại toàn bộ source bằng `javac`
3. nạp JDBC driver từ thư mục `lib`
4. mở app

Nếu chỉ bấm Run trong IDE, có thể gặp lỗi classpath JDBC hoặc build class cũ. Nếu muốn an toàn, dùng script.

## 8. Tài khoản demo và kết quả mong đợi

Tài khoản demo:

- `admin / 123456`
- `staff01 / 123456`

Kết quả mong đợi:

- `admin / 123456` -> vào màn hình manager
- `staff01 / 123456` -> vào màn hình staff

Nếu login thất bại, kiểm tra theo thứ tự:

1. SQL Server service đã chạy chưa
2. database `restaurant_db` đã tạo chưa
3. đã chạy `schema_seed.sql` chưa
4. SQL login `restaurant_app` đã được tạo chưa
5. app đang chạy bằng `run-app.bat` hoặc `run-app.ps1` chưa

## 9. Override thông tin kết nối

App hỗ trợ override bằng system property hoặc environment variable.

Override JDBC URL:

- system property: `restaurant.db.url`
- environment variable: `RESTAURANT_DB_URL`

Override username:

- system property: `restaurant.db.username`
- environment variable: `RESTAURANT_DB_USERNAME`

Override password:

- system property: `restaurant.db.password`
- environment variable: `RESTAURANT_DB_PASSWORD`

Ví dụ PowerShell:

```powershell
$env:RESTAURANT_DB_USERNAME="sa"
$env:RESTAURANT_DB_PASSWORD="your_password"
powershell -ExecutionPolicy Bypass -File .\run-app.ps1
```

Nếu muốn override URL:

```powershell
$env:RESTAURANT_DB_URL="jdbc:sqlserver://localhost;databaseName=restaurant_db;encrypt=true;trustServerCertificate=true;"
powershell -ExecutionPolicy Bypass -File .\run-app.ps1
```

## 10. Những thay đổi đã được tích hợp so với project nhóm ban đầu

### 10.1. Phần đã có sẵn từ project nhóm gốc

Project nhóm ban đầu đã có staff UI và một số DAO/model cho:

- đặt bàn
- tìm bàn trống
- tìm khách hàng
- thêm khách hàng cho booking
- sửa booking
- gọi món
- xác nhận order

Nhưng project gốc chưa có:

- login thật
- manager home
- quản lý khách hàng
- quản lý nhân viên
- schema SQL đầy đủ kèm theo
- cách chạy ổn định với JDBC driver

### 10.2. Phần được thêm vào

Đã thêm manager module:

- login
- phân quyền role
- manager home
- manage clients
- manage staff

Đã thêm service layer:

- `AuthService`
- `ClientService`
- `UserService`

Đã thêm DAO:

- `UserDAO`

Đã mở rộng DAO sẵn có:

- `ClientDAO` giữ chức năng staff cũ và thêm CRUD/search/soft delete cho manager

Đã mở rộng model dùng chung:

- `User`
- `Client`

Đã bổ sung schema:

- thêm `tblUser`
- thêm `status` vào `tblClient`

Đã đổi entry point:

- trước đây main chỉ in `Hello World!`
- hiện tại main mở màn hình login

Đã đổi luồng điều hướng:

- `MANAGER` -> manager module
- `STAFF` -> staff module

### 10.3. Phần đã sửa để app chạy được trên máy thật

Đã sửa kết nối SQL Server:

- ưu tiên kết nối `localhost` default instance
- thử thêm `localhost:1433`
- thử thêm `SQLEXPRESS`
- hỗ trợ override bằng env var / system property

Đã thêm cơ chế tự nạp JDBC driver nếu IDE không cấp classpath đúng.

Đã thêm script chạy project để tránh tình trạng build xong nhưng vẫn nạp class cũ.

## 11. Các luồng cần test sau khi clone

Cần test tối thiểu:

1. login bằng `admin`
2. login bằng `staff01`
3. manager -> quản lý khách hàng
4. manager -> quản lý nhân viên
5. staff -> đặt bàn
6. staff -> sửa booking
7. staff -> gọi món

## 12. Các lưu ý cho thành viên khác

- Không xóa `tblUser`
- Không bỏ cột `status` của `tblClient`
- Không sửa `run-app.ps1` nếu chưa hiểu rõ cách build và classpath hiện tại
- Nếu thay đổi role/login flow thì phải sửa đồng bộ login + manager/staff navigation
- Nếu đổi tên bảng trong SQL thì phải sửa toàn bộ DAO liên quan

## 13. Cách xử lý khi máy khác vẫn không chạy

Nếu máy khác vẫn lỗi, cần gửi lại đầy đủ các thông tin sau:

1. Lỗi hiện trên hộp thoại hoặc stack trace
2. Đã chạy `schema_seed.sql` hay chưa
3. Có tạo SQL login `restaurant_app` hay không
4. SQL Server đang là default instance hay named instance
5. Đang chạy bằng `run-app.bat` hay bấm Run trong IDE

Chỉ cần 5 thông tin đó là đủ để debug nhanh.
