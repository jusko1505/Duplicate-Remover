import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.CRC32;

public class duplicateFinder {
    /*private HashMap<Long, File> map;*/
    private static HashMap<Long, File> map;
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException{

        duplicateFinder df = new duplicateFinder();
        
        Scanner sc = new Scanner(System.in);
        /*
        MessageDigest md = MessageDigest.getInstance("MD5");
        */
        df.welcomeMessage();

        String path = sc.nextLine();
        long t2 = (new Date()).getTime();
        File directory = new File(path);
        File [] directoryList = directory.listFiles();
        File dupeFolderPath = df.createDupeFolder(path);
        int count =0;

        /* If the directory list is a folder */
        if (directoryList != null){
            /* If that folder contains files */
            if(directoryList.length != 0){
                /* Initialize hashmap capacity because resizing is expensive  */
                int hsSize = (int)((directoryList.length+1)/0.75);
                map = new HashMap<Long, File>(hsSize);
                for (File potentialDupe: directoryList){
                    /* For each file, if it is not a folder, find its checksum */
                    if (!potentialDupe.isDirectory()){
                        System.out.print("|"+count++);
                        /*String hash = df.MD5checksum(md, potentialDupe);*/
                        long hash = df.CRC32Checksum(potentialDupe);
                        /* If our hashmap contains that file, move it to the duplicates folder */
                        if (map.containsKey(hash)){
                            if(df.compareFileSize(potentialDupe, map.get(hash))){
                                df.cutNPasteFile(potentialDupe, dupeFolderPath);
                            }
                        }
                        else{
                            map.put(hash, potentialDupe);
                        }
                    }
                }
            }
            /* The directory list does not contain any files */
            else{
                System.out.println("The directory is empty. Exiting program.");
                System.exit(0);
            }
        }
        /* The directory list is not a folder; it is a single file */
        else{
            System.out.println("The path points to a single file. Exiting program.");
            System.exit(0);
        }
        sc.close();
        System.out.println("Done! Check out the duplicates folder and verify its content.");
        long t3 = (new Date()).getTime();
        long executionTime = (t3 - t2);
        System.out.println("Total execution time: " + executionTime +" ms");
    }
    /* creates the duplicates folder */
    private File createDupeFolder(String path){
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
    private void cutNPasteFile(File file, File destination){
        File appendToDestination = new File(destination.getAbsolutePath() + "\\" + file.getName());
        file.renameTo(appendToDestination);
    }
    /* returns true if the two files have the same size
    used in combination with checksum to find if two files are the same */
    private boolean compareFileSize(File file1, File file2){
        double bytes1 = file1.length();
        double bytes2 = file2.length();
        if (bytes1 == bytes2){
            return true;
        }
        return false;
    }

    private long CRC32Checksum(File file) throws IOException{
        FileInputStream fis = new FileInputStream(file);
        CRC32 crc = new CRC32();
        byte[] byteArray = new byte[32];
        int bytesCount =  0;

        while ((bytesCount = fis.read(byteArray)) != -1){
            crc.update(byteArray, 0, bytesCount);
        }
        fis.close();

        return crc.getValue();
    }

    private void welcomeMessage(){
        System.out.println("Please make sure that you do not have a folder called \"Duplicates\"");
        System.out.println("Enter the directory in which you want to find duplicates:");
    }

    /* 
    private String MD5checksum(MessageDigest digest, File file) throws IOException{
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
        
        for (int i = 0; i < bytes.length; i++){
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
    */
}
