package model;

import java.io.Serializable;
import java.util.Date;

public class Bill implements Serializable {
    private int id;
    private Date paymentDate;
    private String paymentTime;
    private double totalAmount;
    private Booking booking;

    public Bill() {
        super();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentTime() { return paymentTime; }
    public void setPaymentTime(String paymentTime) { this.paymentTime = paymentTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
}
