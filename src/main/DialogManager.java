package main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogManager {
    private static JDialog dialog;
    private static JProgressBar progressBar;
    private static int count = 0;
    private static int max = 0;

    public static void executeInSeparateThread(Runnable task) {
        Thread thread = new Thread(task);
        thread.start();
    }

    // Function to show a brand new dialog with a counter
    public static void show(int maxVal) {
        DialogManager.max = maxVal;
        
        executeInSeparateThread(() -> {
            dialog = new JDialog();
            dialog.setTitle("Loading");
            dialog.setSize(200, 100);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            dialog.add(new JLabel("Loading region files..."));
    
            progressBar = new JProgressBar(0, max);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            dialog.add(progressBar);
    
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    dialog = null;
                }
            });
    
            dialog.setVisible(true);
        });
    }

    // Function to set the counter
    public static void setCount(int n) {
        count = n;
        if (count > max) {
            throw new IllegalArgumentException("Count (" + count + ") cannot be greater than max (" + max + ")");
        }

        if (dialog != null && progressBar != null) {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(count);
            });
        }
    }

    // Function to close the dialog
    public static void close() {
        executeInSeparateThread(() -> {
            if (dialog != null) {
                dialog.dispose();
            }
        });
    }
}