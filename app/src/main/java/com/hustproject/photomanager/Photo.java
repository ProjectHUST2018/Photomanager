package com.hustproject.photomanager;

import android.media.ExifInterface;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;

public class Photo {
    public File thisItem;
    public long  photoSize;                                                   //相片的大小
    public Boolean  isDeleted;                                                //判断相片是否被删除
    public Set<String> Tag;                                                   //标签
    public String Path;                                                       //相片的存储路
    public String photoTime;
    public String photoName;
    public String photoTimeStd;
    public long modifyTime;
    public long countSec;

    private ExifInterface exif;

    public void addTag    (String tag) {
        if(tag == "-1" || tag == null)return;
        Tag.add   (tag);
    }
    public void deleteTag (String tag) {
        if(tag == "-1" || tag == null)return;
        Tag.remove(tag);
    }

    Photo(final File item) throws IOException{

        thisItem    =   item;
        isDeleted   =   false;
        Path        =   item.getAbsolutePath();

        Tag         =   new HashSet<String>();
        exif        =   new ExifInterface(Path);
        photoName   =   Path.substring(Path.lastIndexOf("/")+1);
        photoSize   =   new FileInputStream(item).available();
        photoTime   =   exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);  //拍摄时间
        modifyTime  =   item.lastModified();

        if(photoTime == null) {
             photoTime = exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
            if(photoTime == null)photoTime = "-1";
        }
        if(photoTime != "-1") {
            photoTimeStd = photoTime.substring(0, 4) + "年" + photoTime.substring(5, 7) + "月" + photoTime.substring(8, 10) + "日";
            countSec = getPhotoTime();
        }
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

    public int[]getPhotoStandard(){
        String start[] = new String[2];
        int res[] = new int[2];

        start[0] = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);      //相片的长
        start[1] = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);         //相片的宽

        for(int i = 0;i < 2;i++)
            if(start[i] == null) return new int[]{-1,-1};
            else res[i]=Integer.valueOf(start[i].toString());

        return res;
    }

    public String getPhotoSize(){
        long tmp = photoSize;
        String[] unit = new String[]{"B","KB","MB","GB"};
        DecimalFormat df = new DecimalFormat("#.00");

        int i;
        for(i=0;i<3&&tmp>=1024;i++,tmp/=1024);

        return  df.format((double)photoSize)+unit[i];
    }

    public double[] getPhotoGPS(){
        String tmp[] = new String[3];                              //相片拍摄时的经纬度
        double res[] = new double[3];                          //string转double型

        tmp[0] = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);     //纬度
        tmp[1] = exif.getAttribute(ExifInterface.TAG_GPS_DEST_LATITUDE);   //经度
        tmp[2] = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);

        for(int i = 0; i <= 2; i++)
            if(tmp[i] == null) return new double[] {-1,-1,-1};
            else res[i] = Double.valueOf(tmp[i].toString());

        return res;
    }
}