package com.moe.video.framework.content;
import android.os.Parcelable;
import android.os.Parcel;
import java.io.File;
import android.graphics.Bitmap;
import java.util.zip.ZipFile;
import java.io.IOException;
import java.util.zip.ZipEntry;
import com.moe.video.framework.util.StringUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import android.os.RemoteException;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Package implements Parcelable {
    public String packageName;
    public String src;
    public String title,main,versionName;
    public int version;
    public String logo;
    public Package(Parcel p){
        packageName=p.readString();
        src=p.readString();
        title=p.readString();
        main=p.readString();
        versionName=p.readString();
        version=p.readInt();
        logo=p.readString();
    }
    //程序目录
    public Package(File file) throws IOException{
        src=file.getAbsolutePath();
        
        BufferedReader br=new BufferedReader(new FileReader(new File(src,"info.ini")));
        String line;
        while((line=br.readLine())!=null){
            line=line.trim();
            if(line.startsWith("#"))
                continue;
            int index=line.indexOf("=");
            switch(line.substring(0,index).toLowerCase()){
                case "packagename":
                    packageName=line.substring(index+1);
                    break;
                case "title":
                    title=line.substring(index+1);
                    break;
                case "main":
                case "exe":
                case "index":
                    main=line.substring(index+1);
                    break;
                case "versionname":
                    versionName=line.substring(index+1);
                    break;
                case "version":
                    version=Integer.parseInt(line.substring(index+1));
                    break;
                case "logo":
                    logo=line.substring(index+1);
                    break;
            }
        }
    }

    public InputStream getFile(String file) throws FileNotFoundException {
        return new FileInputStream(new File(src,file));
    }
    public Bitmap loadLogo(){
        return BitmapFactory.decodeFile(new File(src,logo).getAbsolutePath());
        
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p1, int p2) {
        p1.writeString(packageName);
        p1.writeString(src);
        p1.writeString(title);
        p1.writeString(main);
        p1.writeString(versionName);
        p1.writeInt(version);
        p1.writeString(logo);
    }
    
    public static Creator<Package> CREATOR=new Creator<Package>(){

        @Override
        public Package createFromParcel(Parcel p1) {
            return new Package(p1);
        }

        @Override
        public Package[] newArray(int p1) {
            return new Package[p1];
        }
    };
    
    
}
