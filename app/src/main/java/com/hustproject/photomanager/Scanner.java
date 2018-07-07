package com.hustproject.photomanager;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Scanner{
    public Set<Photo> allPhoto;
    private Map<String,Integer> checkTag;

    public void addTag(String tag) {
        if(checkTag.get(tag) == null)checkTag.put(tag,1);
        else checkTag.put(tag,checkTag.get(tag)+1);
    }

    public void deleteTag(String tag) {
        checkTag.put(tag,checkTag.get(tag)-1);
    }

   /* public String[] getTag() {
        Array<String> ret = new String[]{};
        for(item in checkTag)
            ret.
    }*/

    public void fileScan(File file)throws Exception{
        Set<File> allScanFile = new HashSet<File>();                //存储所有现存图片
        Queue<File> Directory = new LinkedList<File>();             //广搜所用队列

        Directory.offer(file);                                      //广搜
        while(!Directory.isEmpty()) {
            file = Directory.poll();
            File[] allFile = file.listFiles();
            if(allFile == null) continue;

            for(File item : allFile) {
                if (item.isDirectory() && !item.isHidden())
                    Directory.offer(item);
                else if (!item.isHidden() && (item.getName().endsWith(".jpeg") || item.getName().endsWith(".png") || item.getName().endsWith(".jpg") || item.getName().endsWith(".bmp") || item.getName().endsWith(".wmf") || item.getName().endsWith(".ico")))
                    allScanFile.add(item);
            }
        }

        for(Iterator<Photo> it = allPhoto.iterator(); it.hasNext();) {
            Photo tmp = it.next();
            if(allScanFile.contains(tmp.thisItem))
                allScanFile.remove(tmp.thisItem);
            else it.remove();
        }

        for(File item: allScanFile)
            allPhoto.add(new Photo(item));
    }

    Scanner() {
        allPhoto = new HashSet<Photo>();
        checkTag = new HashMap<String,Integer>();
    }
}
