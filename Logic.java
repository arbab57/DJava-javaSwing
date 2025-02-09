import java.awt.TextArea;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

class MyFile{
public String name = "";
public String path = "";
public Boolean isSelcted = false;
public File file;
public MyFile(String name, String path, File file, Boolean isSelcted){
    this.name = name;
    this.path = path;
    this.file = file;
    this.isSelcted = isSelcted;
}
}

public class Logic {
    private int currentIndex = -1;
    private ArrayList<MyFile> files = new ArrayList<>();

    public Logic() {}

    public String openFile(String path) {
       try {
      File myObj = new File(path);
      Scanner myReader = new Scanner(myObj);
      String data = "";
      while (myReader.hasNextLine()) {
        data += myReader.nextLine();
        data += System.lineSeparator();
      }
      myReader.close();
      return data;
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
      return "";
    }
    }

    public void saveCurrentFile(TextArea textArea) {
        if (currentIndex < 0 || currentIndex >= files.size()) {
            System.out.println("No file selected for saving.");
            return;
        }

        try (FileWriter myWriter = new FileWriter(files.get(currentIndex).path)) {
            myWriter.write(textArea.getText());
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving the file.");
            e.printStackTrace();
        }
    }

    public void closeCurrentFile(TextArea textArea) {
        System.out.println(currentIndex);
        if (currentIndex < 0 || currentIndex >= files.size()) {
            System.out.println("No file selected for closing.");
            return;
        }
        saveCurrentFile(textArea);
        files.remove(currentIndex);
        currentIndex--;
    }

    public void choosefile() {

                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                    InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

               JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Directory to Create a New .java File");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = chooser.getSelectedFile();

            String fileName = JOptionPane.showInputDialog(
                    null,
                    "Enter new file name (must end with .java):",
                    "New Java File",
                    JOptionPane.PLAIN_MESSAGE
            );
            
            if (fileName == null || fileName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No file name provided. Operation cancelled.");
                return;
            }
            
            if (!fileName.toLowerCase().endsWith(".java")) {
                fileName += ".java";
            }
            
            File newFile = new File(selectedDirectory, fileName);

            if (newFile.exists()) {
                JOptionPane.showMessageDialog(null, "File already exists:\n" + newFile.getAbsolutePath());
            } else {
                try {
                    if (newFile.createNewFile()) {
                        JOptionPane.showMessageDialog(null, "File created successfully:\n" + newFile.getAbsolutePath());
                        MyFile newFile2 = new MyFile(fileName, newFile.getAbsolutePath(), newFile, false);
                        files.add(newFile2);
                        currentIndex++;
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to create file.");
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error creating file:\n" + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "No directory selected.");
        }

    }

    public ArrayList<MyFile> getFilesArray() {
        return files;
    }

    public int getCurrentFileIndex() {
        return currentIndex;
    }

    public void incrementCurrentIndex() {
        currentIndex++;
    }

    public void setCurrentIndex(int i) {
        currentIndex = i;
    }

    public void decrementCurrentIndex() {
        currentIndex--;
    }


    public StringBuilder readFile(File selectedFile) {
        StringBuilder fileContent = new StringBuilder();
        try (Scanner scanner = new Scanner(selectedFile)) {
            while (scanner.hasNextLine()) {
                fileContent.append(scanner.nextLine()).append(System.lineSeparator());
            }
            return fileContent;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error opening file: " + e.getMessage());
            return fileContent;
        }
    }

    public String open() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                 InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Java File to Open");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // Restrict to only .java files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".java");
            }
    
            @Override
            public String getDescription() {
                return "Java Files (*.java)";
            }
        });
    
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            MyFile newFile = new MyFile(selectedFile.getName(), selectedFile.getAbsolutePath(), selectedFile, false);
            files.add(newFile);
            currentIndex++;
    
            StringBuilder fileContent = readFile(selectedFile);
            String openedFileContent = fileContent.toString();

            return openedFileContent;
            
        } else {
            JOptionPane.showMessageDialog(null, "No file selected.");
            return "";

        }
    }

    public static void runCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", "cmd", "/k", command);
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runJavaFile(TextArea textArea) {
        saveCurrentFile(textArea);
        
        String filePath = files.get(currentIndex).path;
        String fileDir = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
        String fileName = files.get(currentIndex).name.replace(".java", "");
        
        Path classFilePath = Paths.get(fileDir + fileName + ".class");
        try {
            Files.deleteIfExists(classFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String command = "cd /d \"" + fileDir + "\" && javac \"" + fileName + ".java\" && java " + fileName;
        runCommand(command);
    }
    


}
