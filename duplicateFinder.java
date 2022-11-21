import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.HashMap;
import java.util.Scanner;

public class duplicateFinder {
    private static HashMap<String, File> map;
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException{

        FileActions fa = new FileActions();
        duplicateFinder df = new duplicateFinder();
        Scanner sc = new Scanner(System.in);
        MessageDigest md = MessageDigest.getInstance("MD5");

        df.welcomeMessage();

        String path = sc.nextLine();
        long t2 = (new Date()).getTime();
        File directory = new File(path);
        File [] directoryList = directory.listFiles();
        File dupeFolderPath = fa.createDupeFolder(path);
        int count = 0;

        if(directoryList ==  null){
            System.out.println("The path points to a single file. Exiting program.");
            System.exit(0);
        }
        if(directoryList.length == 0){
            System.out.println("The directory is empty. Exiting program.");
                System.exit(0);
        }
        /* Initialize hashmap capacity because resizing is expensive  */
        int hsSize = (int)((directoryList.length+1)/0.75);
        map = new HashMap<String, File>(hsSize);
        for (File potentialDupe: directoryList){
            /* For each file, if it is not a folder, find its checksum */
            if(!potentialDupe.isDirectory()){
                System.out.print("|"+count++);
                String hash = df.MD5checksum(md, potentialDupe);
                /* If our hashmap contains that file, move it to the duplicates folder */
                if(map.containsKey(hash)){
                    if(fa.isSameFileSize(potentialDupe, map.get(hash))){
                        fa.moveFile(directory, dupeFolderPath);
                    }
                }
                else{
                    map.put(hash, potentialDupe);
                }
             }
        }
        sc.close();
        System.out.println("Done! Check out the duplicates folder and verify its content.");
        long t3 = (new Date()).getTime();
        long executionTime = (t3 - t2);
        System.out.println("Total execution time: " + executionTime +" ms");
    }

    private void welcomeMessage(){
        System.out.println("Please make sure that you do not have a folder called \"Duplicates\"");
        System.out.println("Enter the directory in which you want to find duplicates:");
    }

   
    private String MD5checksum(MessageDigest digest, File file) throws IOException{
        //reads bytes from a file
        FileInputStream fis = new FileInputStream(file);
        FileChannel channel = fis.getChannel();
        ByteBuffer buff = ByteBuffer.allocate(8192);
        
        //byte array reads data in chunks
        /* 
        byte[] byteArray = new byte[1024];
        int bytesCount =  0;
        */
        while ((channel.read(buff)) != -1){
            buff.flip();
            digest.update(buff);
            buff.clear();
        };
        byte[] hashValue = digest.digest();
        fis.close();
        return new String(hashValue);
    }

    
}
