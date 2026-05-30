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
        }

        seedDefaultUsers();
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
}
