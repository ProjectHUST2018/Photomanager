package com.hustproject.photomanager;
import android.media.ExifInterface;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.FileOutputStream;
import java.io.IOException;
class Photo {
    public File thisItem;
    public int[] delete;                                                      //相片的删除时间
    public long  photoSize;                                                   //相片的大小
    public Boolean  isDeleted;                                                //判断相片是否被删除
    public Set<String> Tag;                                                   //标签
    public String Path;                                                       //相片的存储路
    public String photoTime;

    private ExifInterface exif;

    public void addTag    (String tag) {
        Tag.add   (tag);
    }
    public void deleteTag (String tag) {
        Tag.remove(tag);
    }

    Photo(final File item) throws Exception{

        thisItem    =   item;
        isDeleted   =   false;
        Path        =   item.getAbsolutePath();

        delete      =   new int[6];
        Tag         =   new HashSet<String>();
        exif        =   new ExifInterface(Path);
        photoSize   =   new FileInputStream(item).available();
        photoTime   =   exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);  //拍摄时间

        Tag.add(photoTime);
    }

    private long getTime(int[] res) {
        long a,b,c,d;
        int mont1[] = {0,31,60,91,121,152,182,213,244,274,305};
        int mont2[] = {0,31,59,90,120,151,181,212,243,274,304};

        a=(res[0]-2000)/4;
        b= res[0]-2000 -a;
        c=a*31622400+b*31536000;//年

        if((res[0]%4==0&&res[0]%100!=0)||res[0]%400==0)d=mont1[res[1]-1];
        else d=mont2[res[1]-1];

        return c+(d+res[2]-1)*86400+res[3]*3600+res[4]*60+res[5];
    }

    public int[]getPhotostandard(){
        String start[] = new String[2];
        int res[] = new int[2];

        start[0] = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);      //相片的长
        start[1] = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);         //相片的宽

        for(int i = 0;i < 2;i++)
            if(start[i] == null) return new int[]{-1,-1};
            else res[i]=Integer.valueOf(start[i].toString());

        return res;
    }

    public String getPhotoName(){
        return Path.substring(Path.lastIndexOf("/")+1,Path.lastIndexOf("."));                                  //相片的名字
    }

    public String getPhotoSize(){
        if(photoSize == -1)return "-1";

        long tmp = photoSize;
        String[] unit = new String[]{"B","KB","MB","GB"};
        DecimalFormat df = new DecimalFormat("#.00");

        int i;
        for(i=0;i<3&&tmp>=1024;i++,tmp/=1024);

        return  df.format((double)photoSize)+unit[i];
    }

    public long getPhotoTime() {
        int res[] = new int[6];
        String tmp[] = new String[6];

        tmp[0] = photoTime.substring(0,3);
        tmp[1] = photoTime.substring(5,6);
        tmp[2] = photoTime.substring(8,9);
        tmp[3] = photoTime.substring(11,12);
        tmp[4] = photoTime.substring(14,15);
        tmp[5] = photoTime.substring(17,18);

        for(int i = 0; i <= 5; i++)
            res[i] = Integer.parseInt(tmp[i]);

        return getTime(res);
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

    public void remove(){
        thisItem.delete();
    }

    public void delete(){
        isDeleted    =   true;
        Calendar c   =   Calendar.getInstance();
        delete[0]    =   c.get(Calendar.YEAR);
        delete[1]    =   c.get(Calendar.MONTH);
        delete[2]    =   c.get(Calendar.DAY_OF_MONTH);
        delete[3]    =   c.get(Calendar.HOUR_OF_DAY);
        delete[4]    =   c.get(Calendar.MINUTE);
        delete[5]    =   c.get(Calendar.SECOND);
    }
}