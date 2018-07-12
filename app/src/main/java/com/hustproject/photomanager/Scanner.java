package com.hustproject.photomanager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

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
import java.util.Collection;
import java.util.Collections;
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
    private Map<String,Integer> allTag;
    public List<Photo> delPhoto;
    public List<Photo> allPhoto;

    public String[] getAllTag() {
        int sz = 0, cnt = 0;

        for (Map.Entry<String, Integer> entry : allTag.entrySet())
            if(entry.getValue() > 0)cnt++;

        String[] res;
        if(cnt == 0)res = new String[1];
        else res = new String[cnt];

        for (Map.Entry<String, Integer> entry : allTag.entrySet())
            if(entry.getValue() > 0)res[sz++]=entry.getKey();

        return res;
    }

    public List<Photo> getAllPhoto(String a) {
        List<Photo>res = new LinkedList<>();
        for(Photo item :allPhoto)
            if(item.Tag.contains(a))
                res.add(item);
        for(Photo item :allPhoto)
            if(!item.Tag.contains(a)){
                boolean f = false;
                for(String tp : item.Tag)
                    if(tp.indexOf(a) != -1)
                        {f = true;break;}
                if(f)res.add(item);
            }
        return res;
    }

    public List<Photo> getAllPhoto(int mode) {
        List<Photo>res = new LinkedList<>();
        if(mode == 1) {
            for(Photo item : allPhoto)
                res.add(item);
        }
        else {
            for(Photo item : delPhoto)
                res.add(item);
            Collections.reverse(res);
        }
        return res;
    }

    public void addTag(String tag) {
        if(allTag.get(tag) == null)allTag.put(tag,1);
        else allTag.put(tag,allTag.get(tag)+1);
    }

    public void deleteTag(String tag) {
        allTag.put(tag,allTag.get(tag)-1);
    }

    private void addPhoto(Photo New) {
        for(String tag :New.Tag)
            addTag(tag);
        allPhoto.add(New);
    }

    public void fileScan(ContentResolver root)throws Exception {
        Set<String>file = new HashSet<>();
        Cursor place = root.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        while(place.moveToNext()){
            byte[] tmp = place.getBlob(place.getColumnIndex(MediaStore.Images.Media.DATA));
            file.add(new String(tmp,0,tmp.length-1));
        }

        for(Photo item : allPhoto)
            file.remove(item.Path);
        for(Photo item : delPhoto)
            file.remove(item.Path);

        for(String path: file) {
            File tmp = new File(path);
            if(tmp.exists() && path.indexOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()) != -1) {
                Photo newone = new Photo(tmp);
                if(!newone.photoTime.equals("-1")){
                    newone.addTag(newone.photoTimeStd);
                    addTag(newone.photoTimeStd);
                }
                addPhoto(new Photo(tmp));
            }
        }
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
                if (thisPhoto != null)
                    thisPhoto.addTag(tt);
            }
            if(thisPhoto != null)
                addPhoto(thisPhoto);
        }

        ft = in.readLine();
        if(ft == null)return;
        size = Integer.valueOf(ft);
        for(int i = 0; i < size; i++) {
            File tmp = new File(in.readLine());
            Photo thisPhoto;
            if(tmp.exists())thisPhoto = new Photo(tmp);
            else thisPhoto = null;

            int tp = Integer.valueOf(in.readLine());
            for(int j = 0; j < tp; j++) {
                String tt = in.readLine();
                if (thisPhoto != null)
                    thisPhoto.addTag(tt);
            }
            if(thisPhoto != null) {
                thisPhoto.isDeleted = true;
                delPhoto.add(thisPhoto);
            }
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

        out.write((delPhoto.size()+"\n").getBytes());
        for(Photo photo : delPhoto){
            out.write((photo.Path+"\n").getBytes());
            out.write((photo.Tag.size()+"\n").getBytes());
            for(String item : photo.Tag)
                out.write((item+"\n").getBytes());
        }

        out.close();
    }

    public void remove(Photo item){
        item.thisItem.delete();
        for(Iterator<Photo>it = delPhoto.iterator();it.hasNext();)
        {
            Photo thisone = it.next();
            if(thisone.Path.equals(item.Path)){
                it.remove();
                return;
            }
        }
    }

    public void recover(Photo item) {
        item.isDeleted = false;
        for(String tag:item.Tag)
            addTag(tag);
        allPhoto.add(item);
        for(Iterator<Photo>it = delPhoto.iterator();it.hasNext();)
        {
            Photo tp = it.next();
            if(tp.Path.equals(item.Path)) {
                it.remove();
                return;
            }
        }
    }

    public void delete(Photo item) {
        item.isDeleted = true;
        for(String tag:item.Tag)
            deleteTag(tag);
        delPhoto.add(item);
        for(Iterator<Photo>it = allPhoto.iterator();it.hasNext();)
        {
            Photo tp = it.next();
            if(tp.photoName.equals(item.photoName)) {
                it.remove();
                return;
            }
        }
    }

    Scanner() throws IOException{
        String dirPath = "/data/data/com.hustproject.photomanager/files/";
        File dir = new File(dirPath);

        allPhoto  = new LinkedList<>();
        delPhoto = new LinkedList<Photo>();
        allTag  = new HashMap<String,Integer>();
        file = new File(dirPath+"TagSave");

        if(!dir.exists())dir.mkdir();
        if(!file.exists()) {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(("0"+"\n"+"0").getBytes());
            out.close();
        }
    }
}