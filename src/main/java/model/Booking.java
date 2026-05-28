/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author annguyen
 */
public class Booking implements Serializable {
    public static final String STATUS_PENDING = "Chờ nhận bàn";
    public static final String STATUS_CHECKED_IN = "Đã nhận bàn";
    public static final String STATUS_CANCELLED = "Đã hủy";
    
    private int id;
    private Date bookDate;
    private String bookTime;
    private int quantity;
    private String status;
    private Client client;
    private User user;
    private ArrayList<BookedTable> bookedTables;

    public Booking() {
        super();
        this.bookedTables = new ArrayList<BookedTable>();
    }
    
    public void addBookedTable(BookedTable bt) {
        this.bookedTables.add(bt);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getBookDate() { return bookDate; }
    public void setBookDate(Date bookDate) { this.bookDate = bookDate; }

    public String getBookTime() { return bookTime; }
    public void setBookTime(String bookTime) { this.bookTime = bookTime; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ArrayList<BookedTable> getBookedTables() { return bookedTables; }
    public void setBookedTables(ArrayList<BookedTable> bookedTables) { this.bookedTables = bookedTables; }
}
