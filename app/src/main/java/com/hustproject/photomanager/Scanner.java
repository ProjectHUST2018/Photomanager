package com.hustproject.photomanager;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Scanner{
    File file;
    public Set<Photo> allPhoto;
    private Map<String,Integer> allTag;

    public void addTag(String tag) {
        if(tag == "@string/errorString" || tag == null)return;
        if(allTag.get(tag) == null)allTag.put(tag,1);
        else allTag.put(tag,allTag.get(tag)+1);
    }

    public void deleteTag(String tag) {
        if(tag == "@string/errorString" || tag == null)return;
        allTag.put(tag,allTag.get(tag)-1);
    }

    private void addPhoto(Photo New) {
        String date = New.photoTime.substring(0,4)+"年"+New.photoTime.substring(5,7)+"月"+New.photoTime.substring(8,10)+"日";
        if(!New.Tag.contains(date)) {
            addTag(date);
            New.addTag(date);
        }
        allPhoto.add(New);
    }

    public void fileScan(File file)throws Exception {
        Set<File> allScanFile = new HashSet<File>();                //存储所有现存图片
        Queue<File> Directory = new LinkedList<File>();             //广搜所用队列

        Directory.offer(file);                                      //广搜
        while (!Directory.isEmpty()) {
            file = Directory.poll();
            File[] allFile = file.listFiles();
            if (allFile == null) continue;

            for (File item : allFile) {
                if (item.isDirectory() && !item.isHidden())
                    Directory.offer(item);
                else if (!item.isHidden() && (item.getName().endsWith(".jpeg") || item.getName().endsWith(".png") || item.getName().endsWith(".jpg") || item.getName().endsWith(".bmp") || item.getName().endsWith(".wmf") || item.getName().endsWith(".ico")))
                    allScanFile.add(item);
            }
        }

        for (Iterator<Photo> it = allPhoto.iterator(); it.hasNext(); ) {
            Photo tmp = it.next();
            if (allScanFile.contains(tmp.thisItem))
                allScanFile.remove(tmp.thisItem);
            else {
                deleteTag(tmp.photoTime);
                for (String tag : tmp.Tag)
                    deleteTag(tag);
                it.remove();
            }
        }

        for (File item : allScanFile)
            addPhoto(new Photo(item));
    }

    public void TagLoad() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        String ft = in.readLine();
        if(ft == null)return;
        int size = Integer.valueOf(ft);
        for(int i = 0; i < size; i++) {
            File tmp = new File(in.readLine());
            Photo thisPhoto;
            if(tmp.exists())thisPhoto = new Photo(tmp);
            else thisPhoto = null;

            int tp = Integer.valueOf(in.readLine());
            for(int j = 0; j < tp; j++) {
                String tt = in.readLine();
                if (thisPhoto != null) {
                    thisPhoto.addTag(tt);
                    addTag(tt);
                }
            }
            if(thisPhoto != null)
                addPhoto(thisPhoto);
        }

        in.close();
    }

    public void TagSave() throws IOException {
        file.delete();
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);

        out.write((allPhoto.size()+"\n").getBytes());
        for(Photo photo : allPhoto){
            out.write((photo.Path+"\n").getBytes());
            out.write((photo.Tag.size()+"\n").getBytes());
            for(String item : photo.Tag)
                out.write((item+"\n").getBytes());
        }

        out.close();
    }

    Scanner() throws IOException{
        String dirPath = "/data/data/com.hustproject.photomanager/files/";
        File dir = new File(dirPath);

        allPhoto  = new HashSet<Photo>();
        allTag  = new HashMap<String,Integer>();
        file = new File(dirPath+"TagSave");

        if(!dir.exists())dir.mkdir();
        if(!file.exists()) {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write("0".getBytes());
            out.close();
        }
    }
}