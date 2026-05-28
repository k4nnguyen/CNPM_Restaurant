/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author annguyen
 */
public class DAO {
    public static Connection con;

    public DAO() {
        if (con == null) {
            // Cấu hình chuỗi kết nối sử dụng instance SQLEXPRESS và cơ sở dữ liệu nhà hàng
            String dbUrl = "jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=restaurant_db;encrypt=true;trustServerCertificate=true;";
            
            // Driver kết nối CSDL SQL Server
            String dbClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; 
            
            // Tài khoản và mật khẩu hệ thống
            String username = "sa";                    
            String password = "123456"; 

            try {
                Class.forName(dbClass);
                con = DriverManager.getConnection(dbUrl, username, password);
                System.out.println("Kết nối SQL Server (SQLEXPRESS) thành công!");
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Lỗi kết nối CSDL: " + e.getMessage());
            }
        }
    }
}