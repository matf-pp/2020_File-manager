package com.matf.filemanager.UtilClasses;

import android.util.Log;

import com.matf.filemanager.Versions.JStateSaver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class JTextEditor extends JStateSaver<String> {

    private boolean upToDate;
    private String filepath;

    private void closeReader(BufferedReader reader){
        try{
            reader.close();
        }catch(IOException e){
            Log.d("ERROR", "reader.close()");
        }
    }

    private void closeWriter(FileWriter writer){
        try{
            writer.close();
        }catch(IOException e){
            Log.d("ERROR", "writer.close()");
        }
    }


    public JTextEditor(String filepath) {
        super("");
        this.upToDate = false;
        this.filepath = filepath;

        refreshFile();
    }

    private String readAllFromFile() {
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(filepath));
        }catch(FileNotFoundException e){
            Log.d("ERROR", "new FileReader()");
            return "";
        }

        ArrayList<String> lines = new ArrayList<>();
        String line;
        try{
            while(null != (line = reader.readLine())){
                lines.add(line);
            }
        }catch(IOException e){
            Log.d("ERROR", "reader.readLine();");
            closeReader(reader);
            return "";
        }
        try{
            reader.close();
        }catch(IOException e){
            Log.d("ERROR", "reader.close()");
        }

        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < lines.size(); i++){
            sb.append(lines.get(i));
            if(i != lines.size() - 1){
                sb.append("\n");
            }
        }
        closeReader(reader);
        return sb.toString();

    }

    private boolean refreshFile() {
        if(upToDate) return false;
        String content = readAllFromFile();
        if(content.equals(getCurrentInstance())){
            return false;
        }
        goTo(content);
        upToDate = true;
        return true;
    }

    public SaveStatus saveChanges() {
        if(upToDate) return SaveStatus.FILENOTCHANGED;
        FileWriter writer;
        try{
            writer = new FileWriter(filepath);
            writer.write(getCurrentInstance());

            closeWriter(writer);
            upToDate = true;
            return SaveStatus.FILESAVED;
        }catch(IOException e){
            Log.d("ERROR", "new FileWriter()");
            return SaveStatus.ERRORSAVING;
        }
    }

    @Override
    public boolean goTo(String newElement) {
        upToDate = false;
        return super.goTo(newElement);
    }

    @Override
    public boolean goBack() {
        upToDate = false;
        return super.goBack();
    }

    @Override
    public boolean goForward() {
        upToDate = false;
        return super.goForward();
    }

    public static void main(String[] args) {
        JTextEditor textEditor = new JTextEditor("~/Desktop/test.txt");
    }
}
