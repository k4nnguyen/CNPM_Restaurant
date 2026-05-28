package model;

import java.io.Serializable;

/**
 * Lớp thực thể mở rộng từ Dish, dùng cho thống kê món ăn bán chạy.
 * Chứa thêm thông tin tổng số lượng bán và tổng doanh thu trong khoảng thời gian.
 */
public class DishStat extends Dish implements Serializable {
    private int totalQuantity;
    private double totalRevenue;

    public DishStat() {
        super();
    }

    public DishStat(int totalQuantity, double totalRevenue) {
        super();
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    @Override
    public String toString() {
        return "DishStat{dish=" + getName() + " (" + getDishCode() + ")"
                + ", totalQuantity=" + totalQuantity
                + ", totalRevenue=" + String.format("%,.0f VNĐ", totalRevenue) + "}";
    }
}
