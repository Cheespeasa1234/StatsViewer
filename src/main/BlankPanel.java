package main;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import util.Globals;
import util.Utility;

public class BlankPanel extends JPanel {

    @FunctionalInterface public interface FileChosenListener {
        public void onFileChosen(File file);
    }

    private JLabel label;
    private JButton locateButton;
    private JButton prevButton;
    private JPanel buttonGroup;

    public FileChosenListener fileChosenListener;

    public BlankPanel(Font f, FileChosenListener fileChosenListener) {
        this.setPreferredSize(new Dimension(Globals.PREF_W, Globals.PREF_H));
        this.label = new JLabel("No server opened yet.");
        this.prevButton = new JButton("Previous Servers");
        this.locateButton = new JButton("Locate Server");
        this.fileChosenListener = fileChosenListener;
        this.locateButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            Utility.setFontRecursively(fc, f);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Globals.SERVER_DIRECTORY = fc.getSelectedFile();
                this.label.setText("Server directory: " + Globals.SERVER_DIRECTORY.getAbsolutePath());

                // find all subdirectories of the current directory
                File[] allFiles = Globals.SERVER_DIRECTORY.listFiles();
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

                Globals.OPEN_WORLD_NAME = "world";
                if (worldFile != null) {
                    System.out.println("World file: " + worldFile);
                    int lastForwardSlashIndex = worldFile.lastIndexOf('/');
                    int lastBackwardSlashIndex = worldFile.lastIndexOf('\\');
                    int lastSlash = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
                    Globals.OPEN_WORLD_NAME = worldFile.substring(lastSlash + 1);
                }
                System.out.println("World name: " + Globals.OPEN_WORLD_NAME);

                this.fileChosenListener.onFileChosen(Globals.SERVER_DIRECTORY);
                Utility.addRecent(Globals.SERVER_DIRECTORY.getAbsolutePath());
            }

        });
        this.prevButton.addActionListener(e -> {
            String[] recentDirectories = Utility.readRecentDirectories().toArray(new String[0]);
            
            // if there are no recent directories, do not show them
            if (recentDirectories.length == 0) {
                JOptionPane.showMessageDialog(null, "No previous servers to open.");
                return;
            }

            String chosen = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a recent directory",
                    "Recent Directories",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    recentDirectories,
                    recentDirectories[0]);
            if (chosen != null) {
                Globals.SERVER_DIRECTORY = new File(chosen);
                this.label.setText("Server directory: " + Globals.SERVER_DIRECTORY.getAbsolutePath());

                // find all subdirectories of the current directory
                File[] allFiles = Globals.SERVER_DIRECTORY.listFiles();
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

                Globals.OPEN_WORLD_NAME = "world";
                if (worldFile != null) {
                    System.out.println("World file: " + worldFile);
                    int lastForwardSlashIndex = worldFile.lastIndexOf('/');
                    int lastBackwardSlashIndex = worldFile.lastIndexOf('\\');
                    int lastSlash = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
                    Globals.OPEN_WORLD_NAME = worldFile.substring(lastSlash + 1);
                }
                System.out.println("World name: " + Globals.OPEN_WORLD_NAME);

                this.fileChosenListener.onFileChosen(Globals.SERVER_DIRECTORY);
                Utility.addRecent(Globals.SERVER_DIRECTORY.getAbsolutePath());
            }
        });

        this.buttonGroup = new JPanel();
        this.buttonGroup.add(locateButton);
        this.buttonGroup.add(prevButton);

        this.add(label);
        this.add(buttonGroup);
    }
}