package pages;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.Lib;
import main.Globals;

public class BlankPanel extends JPanel {

    @FunctionalInterface
    public interface FileChosenListener {
        public void onFileChosen(File file);
    }

    public File serverDirectory = null;
    public JLabel label;
    public JButton locateButton;
    public JButton prevButton;
    public JPanel buttonGroup;

    public FileChosenListener fileChosenListener;

    public BlankPanel(Font f, FileChosenListener fileChosenListener) {
        this.setPreferredSize(new Dimension(Globals.PREF_W, Globals.PREF_H));
        this.label = new JLabel("No server opened yet.");
        this.prevButton = new JButton("Previous Servers");
        this.locateButton = new JButton("Locate Server");
        this.fileChosenListener = fileChosenListener;
        this.locateButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            Lib.setFontRecursively(fc, f);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.serverDirectory = fc.getSelectedFile();
                this.label.setText("Server directory: " + serverDirectory.getAbsolutePath());

                // find all subdirectories of the current directory
                File[] allFiles = this.serverDirectory.listFiles();
                ArrayList<String> worldCandidates = new ArrayList<String>();

                for (File file : allFiles) {
                    // if it is a dir and doesn't start with a .
                    if (file.isDirectory() && !file.getName().startsWith(".")) {
                        // if it has a level.dat
                        if (new File(file.getAbsolutePath() + "/level.dat").exists()) {
                            worldCandidates.add(file.getAbsolutePath());
                        }
                    }
                }
                // make a JOptionPane to select one of the worldCandidates
                String worldFile = (String) JOptionPane.showInputDialog(
                        this,
                        "Select a world",
                        "World Selection",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        worldCandidates.toArray(),
                        worldCandidates.get(0));

                Globals.worldName = "world";
                if (worldFile != null) {
                    System.out.println("World file: " + worldFile);
                    int lastForwardSlashIndex = worldFile.lastIndexOf('/');
                    int lastBackwardSlashIndex = worldFile.lastIndexOf('\\');
                    int lastSlash = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
                    Globals.worldName = worldFile.substring(lastSlash + 1);
                }
                System.out.println("World name: " + Globals.worldName);

                this.fileChosenListener.onFileChosen(serverDirectory);
                Lib.addRecent(serverDirectory.getAbsolutePath());
            }

        });
        this.prevButton.addActionListener(e -> {
            String[] recentDirectories = Lib.readRecentDirectories().toArray(new String[0]);
            String chosen = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a recent directory",
                    "Recent Directories",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    recentDirectories,
                    recentDirectories[0]);
            if (chosen != null) {
                this.serverDirectory = new File(chosen);
                this.label.setText("Server directory: " + serverDirectory.getAbsolutePath());

                // find all subdirectories of the current directory
                File[] allFiles = this.serverDirectory.listFiles();
                ArrayList<String> worldCandidates = new ArrayList<String>();

                for (File file : allFiles) {
                    // if it is a dir and doesn't start with a .
                    if (file.isDirectory() && !file.getName().startsWith(".")) {
                        // if it has a level.dat
                        if (new File(file.getAbsolutePath() + "/level.dat").exists()) {
                            worldCandidates.add(file.getAbsolutePath());
                        }
                    }
                }
                // make a JOptionPane to select one of the worldCandidates
                String worldFile = (String) JOptionPane.showInputDialog(
                        this,
                        "Select a world",
                        "World Selection",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        worldCandidates.toArray(),
                        worldCandidates.get(0));

                Globals.worldName = "world";
                if (worldFile != null) {
                    System.out.println("World file: " + worldFile);
                    int lastForwardSlashIndex = worldFile.lastIndexOf('/');
                    int lastBackwardSlashIndex = worldFile.lastIndexOf('\\');
                    int lastSlash = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
                    Globals.worldName = worldFile.substring(lastSlash + 1);
                }
                System.out.println("World name: " + Globals.worldName);

                this.fileChosenListener.onFileChosen(serverDirectory);
                Lib.addRecent(serverDirectory.getAbsolutePath());
            }
        });

        this.buttonGroup = new JPanel();
        this.buttonGroup.add(locateButton);
        this.buttonGroup.add(prevButton);

        this.add(label);
        this.add(buttonGroup);
    }
}