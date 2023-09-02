import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import java.util.HashMap;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class duplicateFinder {
    //private static HashMap<String, File> map;
    //private static ConcurrentHashMap<String, File> map;
    //static int count = 0;
    private static ConcurrentHashMap<String, File> map;
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException{
        long t2 = (new Date()).getTime();
        FileActions fa = new FileActions();
        duplicateFinder df = new duplicateFinder();
        Scanner sc = new Scanner(System.in);
        

        df.welcomeMessage();

        String path = sc.nextLine();
        
        File directory = new File(path);
        File [] directoryList = directory.listFiles();
        File dupeFolderPath = fa.createDupeFolder(path);
        

        df.doesDirectoryExist(directoryList);

        /* Initialize hashmap capacity because resizing is expensive  */
        int hsSize = (int)(( directoryList.length / 0.75 ) + 1);
        ArrayList<File> duplicateFileList = new ArrayList<File>((int)(( directoryList.length / 0.75 ) + 1));
        map = new ConcurrentHashMap<String, File>(hsSize);

        //directoryList.parallelStream().forEach()
        //directoryList.parallel().forEach();
        Arrays.stream(directoryList).parallel().forEach( (potentialDupe) -> {
            
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                //long t2 = (new Date()).getTime();
                if(!potentialDupe.isDirectory()){
                    //count++;
                    //System.out.println(potentialDupe);
                    //System.out.print("|"+ count++);
                    String hash = df.MD5checksum(md, potentialDupe);
                /* If our hashmap contains that file, move it to the duplicates folder */
                    if(map.containsKey(hash)){
                        if(fa.isSameFileSize(potentialDupe, map.get(hash))){
                            duplicateFileList.add(potentialDupe);
                            //fa.moveFile(potentialDupe, dupeFolderPath);
                        }
                    }
                    else{
                    map.put(hash, potentialDupe);
                    }
                }
                //long t3 = (new Date()).getTime();
                //long executionTime = (t3 - t2);
                //System.out.println("hash time: " + executionTime +" ms");

            } catch(IOException e){
                System.out.println("IOException occured: " + e);
            } catch(NoSuchAlgorithmException e){
                System.out.println("NoSuchAlgorithmException occured: " + e);
            }
        });

        for(File file: duplicateFileList){
            fa.moveFile(file, dupeFolderPath);
        }

        //System.out.println("hashtable size is: " + map.size());

        sc.close();
        System.out.println("Done! Check out the duplicates folder and verify its content.");
        long t3 = (new Date()).getTime();
        long executionTime = (t3 - t2);
        System.out.println("Total execution time: " + executionTime +" ms");
    }
    

    private void doesDirectoryExist(File[] directoryList){
         if(directoryList ==  null){
            System.out.println("The path points to a single file. Exiting program.");
            System.exit(0);
        }
        if(directoryList.length == 0){
            System.out.println("The directory is empty. Exiting program.");
                System.exit(0);
        }
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
