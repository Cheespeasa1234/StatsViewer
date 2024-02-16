package main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogManager {
    private static JDialog dialog;
    private static JLabel counterLabel;
    private static int count = 0;

    // Function to show a brand new dialog with a counter
    public static void show() {
        dialog = new JDialog();
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        counterLabel = new JLabel("Counter: " + count);
        dialog.add(counterLabel);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                dialog = null;
            }
        });

        dialog.setVisible(true);
    }

    // Function to set the counter
    public static void setCount(int n) {
        count = n;
        if (counterLabel != null) {
            counterLabel.setText("Counter: " + count);
        }
    }

    // Function to close the dialog
    public static void close() {
        if (dialog != null) {
            dialog.dispose();
        }
    }
}