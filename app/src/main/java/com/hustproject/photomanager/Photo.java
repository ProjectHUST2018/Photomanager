package com.hustproject.photomanager;

import android.media.ExifInterface;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.io.IOException;

public class Photo {
    public File thisItem;
    public long  photoSize;                                                   //相片的大小
    public Boolean  isDeleted;                                                //判断相片是否被删除
    public List<String> Tag;                                                   //标签
    public String Path;                                                       //相片的存储路
    public String photoTime;
    public String photoName;
    public String photoTimeStd;
    public long modifyTime;
    public long countSec;
    public int width,height;

    private ExifInterface exif;

    Photo(final File item) throws IOException{

        thisItem    =   item;
        isDeleted   =   false;
        Path        =   item.getAbsolutePath();

        Tag         =   new LinkedList<>();
        exif        =   new ExifInterface(Path);
        photoName   =   thisItem.getName();
        photoSize   =   new FileInputStream(item).available();
        photoTime   =   exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);  //拍摄时间
        modifyTime  =   item.lastModified();

        height = Integer.valueOf(exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));      //相片的高
        width  = Integer.valueOf(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));       //相片的宽

        if(photoTime == null) {
             photoTime = exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
            if(photoTime == null)photoTime = "-1";
        }

        if(!photoTime.equals("-1")) {
            photoTimeStd = photoTime.substring(0, 4) + "年" + photoTime.substring(5, 7) + "月" + photoTime.substring(8, 10) + "日";
            countSec = getPhotoTime();
        }
    }

    public void delTag(String tag){
        for(Iterator<String>it = Tag.iterator();it.hasNext();){
            String tmp = it.next();
            if(tmp.equals(tag)){
                it.remove();
                break;
            }
        }
    }

    public void addTag(String tag){
        delTag(tag);
        Tag.add(tag);
    }

    private long calc(int[] res) {
        long a,b,c,d;
        int mont1[] = {0,31,60,91,121,152,182,213,244,274,305,335};
        int mont2[] = {0,31,59,90,120,151,181,212,243,274,304,334};

        a=(long)(res[0]-1950)/4;
        b=(long) res[0]-1950 -a;
        c=a*(long)31622400+b*(long)31536000;//年

        if((res[0]%4==0&&res[0]%100!=0)||res[0]%400==0)d=mont1[res[1]-1];
        else d=mont2[res[1]-1];

        return c+(d+res[2]-1)*(long)86400+res[3]*(long)3600+res[4]*(long)60+(long)res[5];
    }

    private long getPhotoTime() {
        int res[] = new int[6];
        String tmp[] = new String[6];

        tmp[0] = photoTime.substring(0,4);
        tmp[1] = photoTime.substring(5,7);
        tmp[2] = photoTime.substring(8,10);
        tmp[3] = photoTime.substring(11,13);
        tmp[4] = photoTime.substring(14,16);
        tmp[5] = photoTime.substring(17,19);

        for(int i = 0; i <= 5; i++)
            res[i] = Integer.parseInt(tmp[i]);

        return calc(res);
    }

    public String getPhotoSize() {
        double tmp = photoSize;
        String[] unit = new String[]{"B", "KB", "MB", "GB"};
        DecimalFormat df = new DecimalFormat("#.00");

        int i;
        for (i = 0; i < 3 && tmp >= 1024; i++, tmp /= 1024) ;

        return df.format(tmp) + unit[i];
    }
}