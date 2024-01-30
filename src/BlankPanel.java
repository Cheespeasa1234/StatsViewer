import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
    
    public BlankPanel(FileChosenListener fileChosenListener) {
        this.label = new JLabel("No server opened yet.");
        this.prevButton = new JButton("Previous Servers");
        this.locateButton = new JButton("Locate Server");
        this.fileChosenListener = fileChosenListener;
        this.locateButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.serverDirectory = fc.getSelectedFile();
                this.label.setText("Server directory: " + serverDirectory.getAbsolutePath());
                this.fileChosenListener.onFileChosen(serverDirectory);
            }
        });

        this.buttonGroup = new JPanel();
        this.buttonGroup.add(locateButton);
        this.buttonGroup.add(prevButton);

        this.add(label);
        this.add(buttonGroup);
    }
}
