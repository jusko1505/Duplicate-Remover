import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileActions {

    public File createDupeFolder(String path){
        String temp = path +"\\Duplicates";
        try{
            Path newFolder = Paths.get(temp);
            Files.createDirectories(newFolder);
        }
        catch(IOException e){
            System.err.println("Duplicates folder cannot be created! " + e.getMessage());
        }
        File folderPath = new File(temp);
        return folderPath;

    }
    /*moves the file to the duplicates folder*/
    public void moveFile(File file, File destination){
        File appendToDestination = new File(destination.getAbsolutePath() + "\\" + file.getName());
        file.renameTo(appendToDestination);
    }

    public boolean isSameFileSize(File file1, File file2){
        return file1.length() == file2.length();
        /* 
        double bytes1 = file1.length();
        double bytes2 = file2.length();
        if (bytes1 == bytes2){
            return true;
        }
        return false;
        */
    }

}
