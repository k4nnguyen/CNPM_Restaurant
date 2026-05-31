package dao;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class BillDAOTest {
    private BillDAO billDAO;

    @BeforeEach
    public void setUp() {
        billDAO = new BillDAO();
    }

    @Test
    public void testGetBillsByDateRange() {
        ArrayList<Bill> list = billDAO.getBillsByDateRange("2026-01-01", "2026-05-31");
        assertNotNull(list, "Danh sách hóa đơn không được null");
    }

    @Test
    public void testGetBillsByTimeFrame() {
        ArrayList<Bill> list = billDAO.getBillsByTimeFrame("18:00-20:00", "2026-05-01", "2026-05-31");
        assertNotNull(list, "Danh sách hóa đơn theo khung giờ không được null");
    }

    @Test
    public void testGetBillsByMonth() {
        ArrayList<Bill> list = billDAO.getBillsByMonth(5, 2026);
        assertNotNull(list, "Danh sách hóa đơn theo tháng không được null");
    }
}
