package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;

public class BillDAO extends DAO {
    public BillDAO() {
        super();
    }

    public ArrayList<Bill> getBillsByDateRange(String startDate, String endDate) {
        ArrayList<Bill> list = new ArrayList<>();
        String sql = "SELECT bl.id, bl.paymentDate, bl.paymentTime, bl.totalAmount, " +
                     "b.id AS bid, b.bookDate, b.bookTime, b.quantity, b.status " +
                     "FROM tblBill bl " +
                     "JOIN tblBooking b ON bl.tblBookingId = b.id " +
                     "WHERE bl.paymentDate BETWEEN ? AND ? ORDER BY bl.paymentDate ASC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bill bl = new Bill();
                bl.setId(rs.getInt("id"));
                bl.setPaymentDate(rs.getDate("paymentDate"));
                bl.setPaymentTime(rs.getString("paymentTime"));
                bl.setTotalAmount(rs.getDouble("totalAmount"));

                Booking b = new Booking();
                b.setId(rs.getInt("bid"));
                b.setBookDate(rs.getDate("bookDate"));
                b.setBookTime(rs.getString("bookTime"));
                b.setQuantity(rs.getInt("quantity"));
                b.setStatus(rs.getString("status"));
                bl.setBooking(b);

                list.add(bl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Bill> getBillsByTimeFrame(String timeFrame, String startDate, String endDate) {
        ArrayList<Bill> list = new ArrayList<>();
        String sql = "SELECT bl.id, bl.paymentDate, bl.paymentTime, bl.totalAmount, " +
                     "b.id AS bid, b.bookDate, b.bookTime, b.quantity, b.status " +
                     "FROM tblBill bl " +
                     "JOIN tblBooking b ON bl.tblBookingId = b.id " +
                     "WHERE bl.paymentDate BETWEEN ? AND ? ";
        
        if (timeFrame.equals("11:00-13:00")) {
            sql += "AND bl.paymentTime BETWEEN '11:00:00' AND '13:00:00' ";
        } else if (timeFrame.equals("18:00-20:00")) {
            sql += "AND bl.paymentTime BETWEEN '18:00:00' AND '20:00:00' ";
        } else {
            sql += "AND bl.paymentTime NOT BETWEEN '11:00:00' AND '13:00:00' AND bl.paymentTime NOT BETWEEN '18:00:00' AND '20:00:00' ";
        }
        sql += "ORDER BY bl.paymentDate ASC, bl.paymentTime ASC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bill bl = new Bill();
                bl.setId(rs.getInt("id"));
                bl.setPaymentDate(rs.getDate("paymentDate"));
                bl.setPaymentTime(rs.getString("paymentTime"));
                bl.setTotalAmount(rs.getDouble("totalAmount"));

                Booking b = new Booking();
                b.setId(rs.getInt("bid"));
                b.setBookDate(rs.getDate("bookDate"));
                b.setBookTime(rs.getString("bookTime"));
                b.setQuantity(rs.getInt("quantity"));
                b.setStatus(rs.getString("status"));
                bl.setBooking(b);

                list.add(bl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Bill> getBillsByMonth(int month, int year) {
        ArrayList<Bill> list = new ArrayList<>();
        String sql = "SELECT bl.id, bl.paymentDate, bl.paymentTime, bl.totalAmount, " +
                     "b.id AS bid, b.bookDate, b.bookTime, b.quantity, b.status " +
                     "FROM tblBill bl " +
                     "JOIN tblBooking b ON bl.tblBookingId = b.id " +
                     "WHERE MONTH(bl.paymentDate) = ? AND YEAR(bl.paymentDate) = ? " +
                     "ORDER BY bl.paymentDate ASC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bill bl = new Bill();
                bl.setId(rs.getInt("id"));
                bl.setPaymentDate(rs.getDate("paymentDate"));
                bl.setPaymentTime(rs.getString("paymentTime"));
                bl.setTotalAmount(rs.getDouble("totalAmount"));

                Booking b = new Booking();
                b.setId(rs.getInt("bid"));
                b.setBookDate(rs.getDate("bookDate"));
                b.setBookTime(rs.getString("bookTime"));
                b.setQuantity(rs.getInt("quantity"));
                b.setStatus(rs.getString("status"));
                bl.setBooking(b);

                list.add(bl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
