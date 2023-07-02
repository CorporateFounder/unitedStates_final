package International_Trade_Union.utils;

import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UtilsFileSaveRead {
    public static void moveFile(String src, String dest ) {
        File folder = new File(src);
        Mining.deleteFiles(dest);
        for (File file : folder.listFiles()) {
            if(!file.isDirectory()){
                Path result = null;
                try {

                    result =  Files.move(Paths.get(file.getAbsolutePath()),
                            Paths.get(dest + file.getName()));
                } catch (IOException e) {
                    System.out.println("Exception while moving file: " + e.getMessage());
                }
                if(result != null) {
                    System.out.println("File moved successfully.");
                }else{
                    System.out.println("File movement failed.");
                }
            }
        }
        Mining.deleteFiles(src);
    }
    public static void write(MultipartFile file, Path dir) {
        Path filepath = Paths.get(dir.toString(), file.getOriginalFilename());

        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean deleted( String fileName, int index) throws IOException {
        File inputFile = new File(fileName);

        File tempFile = new File(Seting.ORIGINAL_BLOCKCHAIN_FILE +"myTempFile.txt");
        boolean deleted = false;

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        int innerIndex = 0;

        String currentLine;

        while((currentLine = reader.readLine()) != null) {

            if(innerIndex == index){
                System.out.println("deleted: " + index);
                deleted = true;
                return deleted;
            }
            writer.write(currentLine + System.getProperty("line.separator"));
            innerIndex++;
        }
        writer.close();
        reader.close();
        deleteFile(fileName);
        boolean successful = tempFile.renameTo(inputFile);
        return deleted;
    }
    public static void save(String object, String fileName) throws IOException {
       save(object, fileName, true);
    }
    public static void save(String object, String fileName, boolean save){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, save))) {
            writer.write(object);
            writer.flush();

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    public static void saves(List<String> objects, String fileName, boolean save){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, save))) {

            for (String s : objects) {
                writer.write(s + "\n");
            }
            writer.flush();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String read(String file) throws FileNotFoundException {
        String text = "";
        File file1 = new File(file);
        if(!file1.exists()){
            System.out.println("file dosn't have");
            return text;
        }
        try( BufferedReader reader = new BufferedReader(new FileReader(file))){
            while (reader.ready()){
                text += reader.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return text;
    }
    public static Set<String> readSet(String file){
        Set<String> list = new HashSet<>();

        try( BufferedReader reader = new BufferedReader(new FileReader(file))){
            while (reader.ready()){
                list.add(reader.readLine());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }
    public static List<String> reads(String file){
        List<String> list = new ArrayList<>();

        try( BufferedReader reader = new BufferedReader(new FileReader(file))){
            while (reader.ready()){
                list.add(reader.readLine());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }
   
    public static void deleteAllFiles(String path){
        File folder = new File(path);
        for (File file : folder.listFiles()) {
            if(!file.isDirectory()){
                file.delete();
            }
        }
    }

    public static void deleteFile(String path){
        File file = new File(path);
        file.delete();
    }

}
