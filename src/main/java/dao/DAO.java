package dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class DAO {
    public static Connection con;

    public DAO() {
        if (con == null) {
            String dbClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            String username = resolveValue("restaurant.db.username", "RESTAURANT_DB_USERNAME", "restaurant_app");
            String password = resolveValue("restaurant.db.password", "RESTAURANT_DB_PASSWORD", "123456");

            try {
                loadSqlServerDriver(dbClass);
                con = connect(username, password);
                ensureManagerTables();
                System.out.println("Ket noi SQL Server thanh cong!");
            } catch (ClassNotFoundException | SQLException e) {
                throw new IllegalStateException("Khong the ket noi SQL Server cho restaurant_db. " + e.getMessage(), e);
            }
        }
    }

    private void loadSqlServerDriver(String driverClassName) throws ClassNotFoundException, SQLException {
        try {
            Class.forName(driverClassName);
            return;
        } catch (ClassNotFoundException ignored) {
        }

        File jdbcJar = resolveJdbcJar();
        if (jdbcJar == null) {
            throw new ClassNotFoundException(driverClassName + " (khong tim thay JDBC jar trong classpath hoac thu muc lib)");
        }

        try {
            URL jarUrl = jdbcJar.toURI().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[] {jarUrl}, DAO.class.getClassLoader());
            Driver driver = (Driver) Class.forName(driverClassName, true, loader).getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(new DriverShim(driver));
        } catch (ReflectiveOperationException | java.io.IOException exception) {
            throw new ClassNotFoundException(driverClassName + " (khong the nap JDBC jar tu " + jdbcJar.getAbsolutePath() + ")", exception);
        }
    }

    private File resolveJdbcJar() {
        String[] candidatePaths = new String[] {
                "lib\\mssql-jdbc-12.8.1.jre11.jar",
                "..\\target\\dependency\\mssql-jdbc-12.8.1.jre11.jar",
                "target\\dependency\\mssql-jdbc-12.8.1.jre11.jar"
        };

        for (String candidatePath : candidatePaths) {
            File file = new File(candidatePath);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    private Connection connect(String username, String password) throws SQLException {
        String overrideUrl = resolveValue("restaurant.db.url", "RESTAURANT_DB_URL", null);
        List<String> candidateUrls = new ArrayList<>();
        if (overrideUrl != null) {
            candidateUrls.add(overrideUrl);
        }
        candidateUrls.add("jdbc:sqlserver://localhost;databaseName=restaurant_db;encrypt=true;trustServerCertificate=true;loginTimeout=2;");
        candidateUrls.add("jdbc:sqlserver://localhost:1433;databaseName=restaurant_db;encrypt=true;trustServerCertificate=true;loginTimeout=2;");
        candidateUrls.add("jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=restaurant_db;encrypt=true;trustServerCertificate=true;loginTimeout=2;");

        try {
            DriverManager.setLoginTimeout(2);
        } catch (Exception ignored) {
        }

        SQLException lastException = null;
        for (String candidateUrl : candidateUrls) {
            try {
                return DriverManager.getConnection(candidateUrl, username, password);
            } catch (SQLException exception) {
                lastException = exception;
            }
        }

        throw new SQLException(buildConnectionError(candidateUrls, username, lastException), lastException);
    }

    private String resolveValue(String systemPropertyName, String environmentVariableName, String defaultValue) {
        String systemValue = System.getProperty(systemPropertyName);
        if (systemValue != null && !systemValue.trim().isEmpty()) {
            return systemValue.trim();
        }

        String environmentValue = System.getenv(environmentVariableName);
        if (environmentValue != null && !environmentValue.trim().isEmpty()) {
            return environmentValue.trim();
        }

        return defaultValue;
    }

    private String buildConnectionError(List<String> candidateUrls, String username, SQLException lastException) {
        return "Da thu cac JDBC URL " + candidateUrls
                + " voi username '" + username + "' nhung deu that bai. "
                + "Kiem tra SQL Server co dang chay, database restaurant_db da ton tai, "
                + "SQL authentication da bat, va mat khau/user SQL dung. "
                + "Loi cuoi cung: " + (lastException == null ? "unknown" : lastException.getMessage());
    }

    private static final class DriverShim implements Driver {
        private final Driver driver;

        private DriverShim(Driver driver) {
            this.driver = driver;
        }

        @Override
        public Connection connect(String url, java.util.Properties info) throws SQLException {
            return driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return driver.acceptsURL(url);
        }

        @Override
        public java.sql.DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) throws SQLException {
            return driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public java.util.logging.Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException {
            return driver.getParentLogger();
        }
    }

    private void ensureManagerTables() throws SQLException {
        try (Statement statement = con.createStatement()) {
            statement.execute("IF OBJECT_ID(N'tblClient', N'U') IS NOT NULL AND COL_LENGTH('tblClient', 'status') IS NULL "
                    + "ALTER TABLE tblClient ADD status NVARCHAR(20) NOT NULL CONSTRAINT DF_tblClient_status DEFAULT N'ACTIVE'");

            statement.execute("IF OBJECT_ID(N'tblUser', N'U') IS NOT NULL AND COL_LENGTH('tblUser', 'status') IS NULL "
                    + "ALTER TABLE tblUser ADD status NVARCHAR(20) NOT NULL CONSTRAINT DF_tblUser_status DEFAULT N'ACTIVE'");

            statement.execute("IF OBJECT_ID(N'tblUser', N'U') IS NOT NULL AND COL_LENGTH('tblUser', 'userCode') IS NULL "
                    + "ALTER TABLE tblUser ADD userCode NVARCHAR(20) NULL");

            statement.execute("IF OBJECT_ID(N'tblOrder', N'U') IS NOT NULL AND COL_LENGTH('tblOrder', 'isPaid') IS NULL "
                    + "ALTER TABLE tblOrder ADD isPaid INT NOT NULL CONSTRAINT DF_tblOrder_isPaid DEFAULT 0");

            statement.execute("IF OBJECT_ID(N'tblTable', N'U') IS NOT NULL AND COL_LENGTH('tblTable', 'name') IS NULL "
                    + "ALTER TABLE tblTable ADD name NVARCHAR(50) NULL");

            statement.execute("IF OBJECT_ID(N'tblBill', N'U') IS NULL "
                    + "BEGIN "
                    + "CREATE TABLE tblBill ("
                    + "id INT IDENTITY(1,1) PRIMARY KEY, "
                    + "createdTime DATETIME NULL, "
                    + "paymentDate DATE NULL, "
                    + "paymentTime VARCHAR(20) NULL, "
                    + "totalAmount FLOAT NOT NULL, "
                    + "paymentMethod NVARCHAR(50) NULL, "
                    + "tblOrderId INT NULL, "
                    + "tblUserId INT NULL, "
                    + "tblBookingId INT NULL, "
                    + "CONSTRAINT FK_tblBill_tblOrder FOREIGN KEY (tblOrderId) REFERENCES tblOrder(id), "
                    + "CONSTRAINT FK_tblBill_tblUser FOREIGN KEY (tblUserId) REFERENCES tblUser(id), "
                    + "CONSTRAINT FK_tblBill_tblBooking FOREIGN KEY (tblBookingId) REFERENCES tblBooking(id)"
                    + ") "
                    + "END");

            statement.execute("IF OBJECT_ID(N'tblUser', N'U') IS NULL "
                    + "BEGIN "
                    + "CREATE TABLE tblUser ("
                    + "id INT IDENTITY(1,1) PRIMARY KEY, "
                    + "userCode NVARCHAR(20) NULL UNIQUE, "
                    + "username NVARCHAR(50) NOT NULL UNIQUE, "
                    + "password NVARCHAR(255) NOT NULL, "
                    + "name NVARCHAR(100) NOT NULL, "
                    + "role NVARCHAR(20) NOT NULL, "
                    + "phone NVARCHAR(20) NULL, "
                    + "email NVARCHAR(100) NULL, "
                    + "status NVARCHAR(20) NOT NULL CONSTRAINT DF_tblUser_status DEFAULT N'ACTIVE'"
                    + ") "
                    + "END");

            statement.execute("IF OBJECT_ID(N'tblDish', N'U') IS NOT NULL AND COL_LENGTH('tblDish', 'description') IS NULL "
                    + "ALTER TABLE tblDish ADD description NVARCHAR(500) NULL");

            statement.execute("IF OBJECT_ID(N'tblDish', N'U') IS NOT NULL AND COL_LENGTH('tblDish', 'status') IS NULL "
                    + "ALTER TABLE tblDish ADD status NVARCHAR(20) NOT NULL CONSTRAINT DF_tblDish_status DEFAULT 'active'");

            statement.execute("IF OBJECT_ID(N'tblTable', N'U') IS NOT NULL AND COL_LENGTH('tblTable', 'isActive') IS NULL "
                    + "ALTER TABLE tblTable ADD isActive BIT NOT NULL CONSTRAINT DF_tblTable_isActive DEFAULT 1");

            statement.execute("IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UX_tblDish_dishCode' AND object_id = OBJECT_ID('dbo.tblDish')) "
                    + "AND NOT EXISTS (SELECT dishCode FROM dbo.tblDish GROUP BY dishCode HAVING COUNT(*) > 1) "
                    + "CREATE UNIQUE INDEX UX_tblDish_dishCode ON dbo.tblDish(dishCode)");

            statement.execute("IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UX_tblTable_tableCode' AND object_id = OBJECT_ID('dbo.tblTable')) "
                    + "AND NOT EXISTS (SELECT tableCode FROM dbo.tblTable GROUP BY tableCode HAVING COUNT(*) > 1) "
                    + "CREATE UNIQUE INDEX UX_tblTable_tableCode ON dbo.tblTable(tableCode)");
        }

        seedDefaultUsers();
        seedMockData();
    }

    private void seedDefaultUsers() throws SQLException {
        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM tblUser")) {
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return;
            }
        }

        String sql = "INSERT INTO tblUser(userCode, username, password, name, role, phone, email, status) "
                + "VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            addUserSeed(statement, "NV001", "admin", "123456", "System Manager", "MANAGER", "0911000001", "admin@restaurant.local");
            addUserSeed(statement, "NV002", "staff01", "123456", "Floor Staff One", "STAFF", "0911000002", "staff01@restaurant.local");
            statement.executeBatch();
        }
    }

    private void addUserSeed(PreparedStatement statement, String userCode, String username, String password,
                             String name, String role, String phone, String email) throws SQLException {
        statement.setString(1, userCode);
        statement.setString(2, username);
        statement.setString(3, password);
        statement.setString(4, name);
        statement.setString(5, role);
        statement.setString(6, phone);
        statement.setString(7, email);
        statement.setString(8, "ACTIVE");
        statement.addBatch();
    }

    private void seedMockData() {
        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM tblBill")) {
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return; // already seeded
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try (Statement stmt = con.createStatement()) {
            // Month 1: Jan 2026
            stmt.execute("INSERT INTO tblBooking(bookDate, bookTime, quantity, status, tblClientId, tblUserId) "
                    + "VALUES('2026-01-15', '12:00', 4, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblClient WHERE phone = '0900000002'), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'))");
            
            stmt.execute("INSERT INTO tblOrder(orderTime, totalAmount, status, tblUserId, tblTableId, isPaid) "
                    + "VALUES('2026-01-15 12:15:00', 145000, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT TOP 1 id FROM tblTable WHERE tableCode = 'T002'), 1)");

            stmt.execute("INSERT INTO tblOrderDish(quantity, currentPrice, tblOrderId, tblDishId) "
                    + "VALUES(2, 60000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D002')), "
                    + "(1, 25000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D004'))");

            stmt.execute("INSERT INTO tblBill(createdTime, paymentDate, paymentTime, totalAmount, paymentMethod, tblOrderId, tblUserId, tblBookingId) "
                    + "VALUES('2026-01-15 13:00:00', '2026-01-15', '13:00:00', 145000, N'Tiền mặt', "
                    + "(SELECT MAX(id) FROM tblOrder), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT MAX(id) FROM tblBooking))");


            // Month 2: Feb 2026
            stmt.execute("INSERT INTO tblBooking(bookDate, bookTime, quantity, status, tblClientId, tblUserId) "
                    + "VALUES('2026-02-14', '18:30', 2, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblClient WHERE phone = '0900000002'), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'))");
            
            stmt.execute("INSERT INTO tblOrder(orderTime, totalAmount, status, tblUserId, tblTableId, isPaid) "
                    + "VALUES('2026-02-14 18:45:00', 110000, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT TOP 1 id FROM tblTable WHERE tableCode = 'T001'), 1)");

            stmt.execute("INSERT INTO tblOrderDish(quantity, currentPrice, tblOrderId, tblDishId) "
                    + "VALUES(2, 45000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D001')), "
                    + "(2, 10000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D003'))");

            stmt.execute("INSERT INTO tblBill(createdTime, paymentDate, paymentTime, totalAmount, paymentMethod, tblOrderId, tblUserId, tblBookingId) "
                    + "VALUES('2026-02-14 19:45:00', '2026-02-14', '19:45:00', 110000, N'Chuyển khoản', "
                    + "(SELECT MAX(id) FROM tblOrder), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT MAX(id) FROM tblBooking))");


            // Month 3: Mar 2026
            stmt.execute("INSERT INTO tblBooking(bookDate, bookTime, quantity, status, tblClientId, tblUserId) "
                    + "VALUES('2026-03-20', '11:30', 6, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblClient WHERE phone = '0900000002'), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'))");
            
            stmt.execute("INSERT INTO tblOrder(orderTime, totalAmount, status, tblUserId, tblTableId, isPaid) "
                    + "VALUES('2026-03-20 11:45:00', 360000, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT TOP 1 id FROM tblTable WHERE tableCode = 'T003'), 1)");

            stmt.execute("INSERT INTO tblOrderDish(quantity, currentPrice, tblOrderId, tblDishId) "
                    + "VALUES(6, 60000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D002'))");

            stmt.execute("INSERT INTO tblBill(createdTime, paymentDate, paymentTime, totalAmount, paymentMethod, tblOrderId, tblUserId, tblBookingId) "
                    + "VALUES('2026-03-20 12:45:00', '2026-03-20', '12:45:00', 360000, N'Thẻ', "
                    + "(SELECT MAX(id) FROM tblOrder), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT MAX(id) FROM tblBooking))");


            // Month 4: Apr 2026
            stmt.execute("INSERT INTO tblBooking(bookDate, bookTime, quantity, status, tblClientId, tblUserId) "
                    + "VALUES('2026-04-10', '19:00', 4, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblClient WHERE phone = '0900000002'), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'))");
            
            stmt.execute("INSERT INTO tblOrder(orderTime, totalAmount, status, tblUserId, tblTableId, isPaid) "
                    + "VALUES('2026-04-10 19:15:00', 210000, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT TOP 1 id FROM tblTable WHERE tableCode = 'T002'), 1)");

            stmt.execute("INSERT INTO tblOrderDish(quantity, currentPrice, tblOrderId, tblDishId) "
                    + "VALUES(3, 60000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D002')), "
                    + "(3, 10000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D003'))");

            stmt.execute("INSERT INTO tblBill(createdTime, paymentDate, paymentTime, totalAmount, paymentMethod, tblOrderId, tblUserId, tblBookingId) "
                    + "VALUES('2026-04-10 20:15:00', '2026-04-10', '20:15:00', 210000, N'Tiền mặt', "
                    + "(SELECT MAX(id) FROM tblOrder), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT MAX(id) FROM tblBooking))");


            // Month 5: May 2026 (Several bills!)
            // Bill 1: Walk-in (bookingId is null, tests LEFT JOIN!)
            stmt.execute("INSERT INTO tblOrder(orderTime, totalAmount, status, tblUserId, tblTableId, isPaid) "
                    + "VALUES('2026-05-12 12:00:00', 115000, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT TOP 1 id FROM tblTable WHERE tableCode = 'T001'), 1)");

            stmt.execute("INSERT INTO tblOrderDish(quantity, currentPrice, tblOrderId, tblDishId) "
                    + "VALUES(2, 45000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D001')), "
                    + "(1, 25000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D004'))");

            stmt.execute("INSERT INTO tblBill(createdTime, paymentDate, paymentTime, totalAmount, paymentMethod, tblOrderId, tblUserId, tblBookingId) "
                    + "VALUES('2026-05-12 13:00:00', '2026-05-12', '13:00:00', 115000, N'Tiền mặt', "
                    + "(SELECT MAX(id) FROM tblOrder), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "NULL)");

            // Bill 2: Walk-in
            stmt.execute("INSERT INTO tblOrder(orderTime, totalAmount, status, tblUserId, tblTableId, isPaid) "
                    + "VALUES('2026-05-25 18:30:00', 205000, N'Đã thanh toán', "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "(SELECT TOP 1 id FROM tblTable WHERE tableCode = 'T002'), 1)");

            stmt.execute("INSERT INTO tblOrderDish(quantity, currentPrice, tblOrderId, tblDishId) "
                    + "VALUES(3, 60000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D002')), "
                    + "(1, 25000, (SELECT MAX(id) FROM tblOrder), (SELECT TOP 1 id FROM tblDish WHERE dishCode = 'D004'))");

            stmt.execute("INSERT INTO tblBill(createdTime, paymentDate, paymentTime, totalAmount, paymentMethod, tblOrderId, tblUserId, tblBookingId) "
                    + "VALUES('2026-05-25 19:30:00', '2026-05-25', '19:30:00', 205000, N'Tiền mặt', "
                    + "(SELECT MAX(id) FROM tblOrder), "
                    + "(SELECT TOP 1 id FROM tblUser WHERE username = 'staff01'), "
                    + "NULL)");
            
            System.out.println("Seed mock data to restaurant_db completed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
