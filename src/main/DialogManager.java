package main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogManager {
    
    private static JDialog dialog; // The dialog to show
    private static JProgressBar progressBar; // The progress bar to show
    private static int count = 0; // The current count
    private static int max = 0; // The maximum count

    /**
     * Function to execute a task in a separate thread
     * 
     * @param task the task to execute
     */
    private static void executeInSeparateThread(Runnable task) {
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Show the dialog with the given maximum value
     * Begins the progress bar movement
     * 
     * @param maxVal the maximum value
     */
    public static void show(int maxVal) {

        if (maxVal < 0) {
            throw new IllegalArgumentException("Max value cannot be negative");
        } else if (dialog != null) {
            throw new IllegalStateException("Dialog is already open");
        }

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

    /**
     * Set the count of the progress bar
     * 
     * @param n the count
     * @throws IllegalArgumentException if the count is greater than the maximum
     */
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

    /**
     * Close the dialog
     */
    public static void close() {
        executeInSeparateThread(() -> {
            if (dialog != null) {
                dialog.dispose();
                dialog = null;
            }
        });
    }
}