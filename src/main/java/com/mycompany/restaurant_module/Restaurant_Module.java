package com.mycompany.restaurant_module;

import javax.swing.SwingUtilities;

import view.manager.LoginFrame;

public class Restaurant_Module {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
