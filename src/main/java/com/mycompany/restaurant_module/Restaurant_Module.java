/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.restaurant_module;

import model.User;
import view.manager.ManagerHomeFrm;
import javax.swing.SwingUtilities;

/**
 *
 * @author annguyen
 */
public class Restaurant_Module {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User manager = new User();
            manager.setName("Manager");
            manager.setRole("Manager");
            new ManagerHomeFrm(manager).setVisible(true);
        });
    }
}
