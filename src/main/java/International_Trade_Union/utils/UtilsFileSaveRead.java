package International_Trade_Union.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UtilsFileSaveRead {

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
