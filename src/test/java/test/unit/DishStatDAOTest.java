package test.unit;

import dao.DishStatDAO;
import model.DishStat;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử đơn vị cho DishStatDAO.
 * Mục tiêu: Đảm bảo getBestSellingDish() trả về đúng danh sách món ăn,
 * tính toán chính xác tổng số lượng và doanh thu trong khoảng thời gian.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DishStatDAOTest {

    private static DishStatDAO dishStatDAO;

    // Khoảng thời gian có dữ liệu trong CSDL test
    private static final String VALID_START = "2026-01-01";
    private static final String VALID_END   = "2026-12-31";

    // Khoảng thời gian không có dữ liệu
    private static final String EMPTY_START = "2020-01-01";
    private static final String EMPTY_END   = "2020-01-31";

    @BeforeAll
    static void setUp() {
        dishStatDAO = new DishStatDAO();
    }

    /**
     * TC-STAT-01: getBestSellingDish() với khoảng thời gian hợp lệ phải trả về danh sách không rỗng
     * và sắp xếp giảm dần theo totalRevenue.
     */
    @Test
    @Order(1)
    @DisplayName("TC-STAT-01: Thống kê hợp lệ - danh sách không rỗng, sắp xếp đúng")
    void testGetBestSellingDish_ValidDateRange() {
        ArrayList<DishStat> result = dishStatDAO.getBestSellingDish(VALID_START, VALID_END);

        // Bỏ qua nếu CSDL chưa có dữ liệu
        Assumptions.assumeTrue(!result.isEmpty(), "Bỏ qua test: CSDL không có dữ liệu trong khoảng " + VALID_START + " - " + VALID_END);

        assertNotNull(result, "Kết quả không được null");
        assertFalse(result.isEmpty(), "Danh sách phải có ít nhất 1 món");

        // Kiểm tra thứ tự sắp xếp giảm dần theo doanh thu
        for (int i = 0; i < result.size() - 1; i++) {
            double rev1 = result.get(i).getTotalRevenue();
            double rev2 = result.get(i + 1).getTotalRevenue();
            assertTrue(rev1 >= rev2,
                    "Doanh thu phải giảm dần: vị trí " + i + " (" + rev1 + ") >= vị trí " + (i+1) + " (" + rev2 + ")");
        }

        // Kiểm tra mỗi DishStat có dữ liệu hợp lệ
        for (DishStat ds : result) {
            assertTrue(ds.getTotalQuantity() > 0, "Tổng số lượng phải > 0: " + ds.getName());
            assertTrue(ds.getTotalRevenue() > 0, "Tổng doanh thu phải > 0: " + ds.getName());
            assertNotNull(ds.getName(), "Tên món không được null");
            assertNotNull(ds.getDishCode(), "Mã món không được null");
        }

        System.out.println("✅ TC-STAT-01 PASSED: " + result.size() + " món được thống kê, sắp xếp đúng");
    }

    /**
     * TC-STAT-02: getBestSellingDish() với khoảng thời gian không có dữ liệu phải trả về danh sách rỗng.
     */
    @Test
    @Order(2)
    @DisplayName("TC-STAT-02: Khoảng thời gian không có dữ liệu - danh sách rỗng")
    void testGetBestSellingDish_EmptyDateRange() {
        ArrayList<DishStat> result = dishStatDAO.getBestSellingDish(EMPTY_START, EMPTY_END);

        assertNotNull(result, "Kết quả không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng khi không có dữ liệu bán hàng");
        System.out.println("✅ TC-STAT-02 PASSED: Trả về danh sách rỗng đúng");
    }

    /**
     * TC-STAT-03: getTotalRevenue() phải trả về tổng doanh thu chính xác bằng tổng các totalRevenue.
     */
    @Test
    @Order(3)
    @DisplayName("TC-STAT-03: Tổng doanh thu tính toán chính xác")
    void testGetTotalRevenue_MatchesSumOfDishStats() {
        ArrayList<DishStat> list = dishStatDAO.getBestSellingDish(VALID_START, VALID_END);
        Assumptions.assumeTrue(!list.isEmpty(), "Bỏ qua test: không có dữ liệu trong CSDL");

        double sumFromList = list.stream().mapToDouble(DishStat::getTotalRevenue).sum();
        double totalFromDAO = dishStatDAO.getTotalRevenue(VALID_START, VALID_END);

        assertEquals(sumFromList, totalFromDAO, 1.0,
                "Tổng doanh thu từ getTotalRevenue() phải bằng tổng từ getBestSellingDish()");
        System.out.printf("✅ TC-STAT-03 PASSED: Tổng doanh thu = %,.0f VNĐ%n", totalFromDAO);
    }

    /**
     * TC-STAT-04: getTopSellingDish() với topN=3 phải trả về tối đa 3 món đầu tiên.
     */
    @Test
    @Order(4)
    @DisplayName("TC-STAT-04: getTopSellingDish() giới hạn đúng số lượng kết quả")
    void testGetTopSellingDish_LimitsResults() {
        int topN = 3;
        ArrayList<DishStat> topList = dishStatDAO.getTopSellingDish(VALID_START, VALID_END, topN);
        ArrayList<DishStat> fullList = dishStatDAO.getBestSellingDish(VALID_START, VALID_END);

        assertNotNull(topList, "Kết quả không được null");
        assertTrue(topList.size() <= topN, "Kết quả phải có tối đa " + topN + " phần tử");

        // Nếu có đủ dữ liệu, đảm bảo topList là top đầu của fullList
        if (fullList.size() >= topN) {
            assertEquals(topN, topList.size(), "Phải trả về đúng " + topN + " món khi có đủ dữ liệu");
            for (int i = 0; i < topN; i++) {
                assertEquals(fullList.get(i).getId(), topList.get(i).getId(),
                        "Phần tử thứ " + i + " phải khớp với danh sách đầy đủ");
            }
        }
        System.out.println("✅ TC-STAT-04 PASSED: Top " + topN + " = " + topList.size() + " kết quả");
    }
}
