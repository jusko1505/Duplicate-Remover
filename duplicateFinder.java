import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;
// TO DO: DECLARE THE VARIABLE TYPES OF K,V IN THE HASH MAP, AND FIX ANY COMPILE ERRORS
// REMEMBER TO COMPILE WITH javac -Xlint:unchecked fileHashTest.java

public class fileHashTest {

    static HashMap<String, File> map = new HashMap<String, File>();
    //create a static hashmap here?
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException{
        Scanner sc = new Scanner(System.in);
        MessageDigest md = MessageDigest.getInstance("MD5");
        System.out.println("Please make sure that you do not have a folder called \"Duplicates\"");
        System.out.println("Enter the directory in which you want to find duplicates:");
        String path = sc.nextLine();
        File directory = new File(path);
        File [] directoryList = directory.listFiles();
        File dupeFolderPath = createDupeFolder(path);
        if (directoryList != null){
            if(directoryList.length != 0){
                for (File potentialDupe: directoryList){
                    if (!potentialDupe.isDirectory()){
                        String hash = checksum(md, potentialDupe);
                        if (map.containsKey(hash)){
                            if(compareFileSize(potentialDupe, map.get(hash))){
                                cutNPasteFile(potentialDupe, dupeFolderPath);
                            }
                        }
                        else{
                            map.put(hash, potentialDupe);
                        }
                    }
                }
            }
            else{
                System.out.println("The directory is empty. Exiting program.");
                System.exit(0);
            }
        }
        else{
            System.out.println("The path points to a single file. Exiting program.");
            System.exit(0);
        }
        sc.close();
        System.out.println("Done! Check out the duplicates folder and verify its content.");
    }
    private static String checksum(MessageDigest digest, File file) throws IOException{
        //reads bytes from a file
        FileInputStream fis = new FileInputStream(file);
        //byte array reads data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount =  0;

        while ((bytesCount = fis.read(byteArray)) != -1){
            digest.update(byteArray, 0, bytesCount);
        };

        fis.close();

        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        //converting a byte array into hexadecimal. Maybe can find a method online to do this
        //ex datatypeconverter in java ---> the method is called .printHexbinary(bytes)
        for (int i = 0; i < bytes.length; i++){
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
    //creates the duplicates folder
    public static File createDupeFolder(String path){
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
    //moves the file to the duplicates folder
    public static void cutNPasteFile(File file, File destination){
        File appendToDestination = new File(destination.getAbsolutePath() + "\\" + file.getName());
        file.renameTo(appendToDestination);
    }
    //returns true if the two files have the same size
    public static boolean compareFileSize(File file1, File file2){
        double bytes1 = file1.length();
        double bytes2 = file2.length();
        if (bytes1 == bytes2){
            return true;
        }
        else{
            return false;
        }
        
    }
}
