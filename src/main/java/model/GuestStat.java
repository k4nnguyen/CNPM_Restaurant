package model;

import java.io.Serializable;

public class GuestStat implements Serializable {
    private String timeFrame;
    private int avgCustomers;
    private double avgRevenuePerHead;
    private double totalRevenue;

    public GuestStat() {
        super();
    }

    public GuestStat(String timeFrame, int avgCustomers, double avgRevenuePerHead, double totalRevenue) {
        this.timeFrame = timeFrame;
        this.avgCustomers = avgCustomers;
        this.avgRevenuePerHead = avgRevenuePerHead;
        this.totalRevenue = totalRevenue;
    }

    public String getTimeFrame() { return timeFrame; }
    public void setTimeFrame(String timeFrame) { this.timeFrame = timeFrame; }

    public int getAvgCustomers() { return avgCustomers; }
    public void setAvgCustomers(int avgCustomers) { this.avgCustomers = avgCustomers; }

    public double getAvgRevenuePerHead() { return avgRevenuePerHead; }
    public void setAvgRevenuePerHead(double avgRevenuePerHead) { this.avgRevenuePerHead = avgRevenuePerHead; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
}
