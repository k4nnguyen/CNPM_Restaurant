/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package dao;

import java.util.ArrayList;
import model.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author annguyen
 */
public class TableDAOTest {
    
    public TableDAOTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of searchFreeTable method, of class TableDAO.
     */
    @Test
    public void testSearchFreeTable() {
        System.out.println("searchFreeTable");
        String date = "";
        String time = "";
        int quantity = 0;
        TableDAO instance = new TableDAO();
        ArrayList<Table> expResult = null;
        ArrayList<Table> result = instance.searchFreeTable(date, time, quantity);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
