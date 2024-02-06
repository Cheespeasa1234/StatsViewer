package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class DependencyChecker {
    public static boolean isPythonAvailable() {
        boolean isAvailable = false;
        try {
            // Attempt to run the "python --version" command
            Process process = Runtime.getRuntime().exec("python --version");

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // If the exit code is 0 and the output contains "Python 3", it means Python 3
            // is available
            if (exitCode == 0 && line != null && line.contains("Python 3")) {
                isAvailable = true;
            }

        } catch (IOException | InterruptedException e) {
            // Handle exceptions if the command couldn't be executed or if waiting for the
            // process fails
            System.out.println("Did not find python");
            return false;
        }
        return isAvailable;
    }

    public static boolean isPython3Available() {
        boolean isAvailable = false;
        try {
            // Attempt to run the "python --version" command
            Process process = Runtime.getRuntime().exec("python3 --version");

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // If the exit code is 0 and the output contains "Python 3", it means Python 3
            // is available
            if (exitCode == 0 && line != null && line.contains("Python 3")) {
                isAvailable = true;
            }

        } catch (IOException | InterruptedException e) {
            // Handle exceptions if the command couldn't be executed or if waiting for the
            // process fails
            System.out.println("Did not find python3");
            return false;
        }
        return isAvailable;
    }

    public static boolean isNbtlibPackageAvailable() {
        boolean isAvailable = false;
        try {
            // Attempt to run the "pip show nbtlib" command
            Process process = Runtime.getRuntime().exec("pip show nbtlib");

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Check if the output contains information about the package
            while ((line = reader.readLine()) != null) {
                if (line.contains("Name: nbtlib")) {
                    isAvailable = true;
                    break;
                }
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // If the exit code is 0 and the package information is found, it means nbtlib
            // is available
            isAvailable = (exitCode == 0 && isAvailable);

        } catch (IOException | InterruptedException e) {
            // Handle exceptions if the command couldn't be executed or if waiting for the
            // process fails
            System.out.println("Did not find nbtlib");
            return false;
        }
        return isAvailable;
    }

    public static boolean isNbtlib3PackageAvailable() {
        boolean isAvailable = false;
        try {
            // Attempt to run the "pip show nbtlib" command
            Process process = Runtime.getRuntime().exec("pip3 show nbtlib");

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Check if the output contains information about the package
            while ((line = reader.readLine()) != null) {
                if (line.contains("Name: nbtlib")) {
                    isAvailable = true;
                    break;
                }
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // If the exit code is 0 and the package information is found, it means nbtlib
            // is available
            isAvailable = (exitCode == 0 && isAvailable);

        } catch (IOException | InterruptedException e) {
            // Handle exceptions if the command couldn't be executed or if waiting for the
            // process fails
            System.out.println("Did not find nbtlib3");
            return false;
        }
        return isAvailable;
    }

    /**
     * Exits the application if any of the required dependencies are not available
     * Requires:
     * - Python / Python 3
     * - pip / pip3
     * - nbtlib
     */
    public static void checkDependencies() {
        boolean pythonAvailable = isPythonAvailable();
        boolean python3Available = isPython3Available();

        if (pythonAvailable) {
            Globals.PYTHON_INSTANCE = "python";
        } else if (python3Available) {
            Globals.PYTHON_INSTANCE = "python3";
        } else {
            JOptionPane.showMessageDialog(null,
                    "Python 3 is required to run this application. Please install Python 3 and try again.",
                    "Dependency Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        boolean nbtlibAvailable = isNbtlibPackageAvailable();
        boolean nbtlib3Available = isNbtlib3PackageAvailable();

        if (!nbtlibAvailable && !nbtlib3Available) {
            JOptionPane.showMessageDialog(null,
                    "The nbtlib package is required to run this application. Please install nbtlib and try again.",
                    "Dependency Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // check system OS is only mac or windows
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("mac") < 0 && os.indexOf("win") < 0) {
            JOptionPane.showMessageDialog(null, "This application is only supported on Windows and macOS.", "OS Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}