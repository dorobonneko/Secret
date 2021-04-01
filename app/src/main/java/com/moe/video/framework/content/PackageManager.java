package com.moe.video.framework.content;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.content.Context;
import android.graphics.BitmapFactory;
import java.io.File;
import java.util.List;
import java.io.FileFilter;
import java.util.ArrayList;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.UUID;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import android.content.Intent;
import com.moe.video.framework.pkg.Packet;
import com.moe.video.framework.activity.ActivityTask;
import com.moe.video.framework.util.FileUtil;
import android.net.Uri;
import com.moe.video.framework.content.Package;

public class PackageManager extends com.moe.video.framework.aidl.PackageManager.Stub implements FileFilter {

   public static final String ACTION_ADD="Secret.PackageManager_ADD";
    public static final String ACTION_REMOVE="Secret.PackageManager_REMOVE";
    public static final String ACTION_UPDATE="Secret.PackageManager_UPDATE";
    public static final Uri AUTH=Uri.parse("content://Secret.PackageManager/");
   private Context context;
   private File work;
    public PackageManager(Context context){
        this.context=context;
        work=context.getFileStreamPath("app");
        if(!work.exists())
            work.mkdirs();
    }

    @Override
    public Package query(String packageName) throws RemoteException {
        if(packageName==null)return null;
        try {
            return new Package(new File(work, packageName));
        } catch (IOException e) {}
        return null;
    }

    @Override
    public void unInstall(String packageName) throws RemoteException {
        ActivityTask.stopTask(context,packageName);
        context.deleteDatabase(packageName);
        context.getSharedPreferences(packageName,0).edit().clear().commit();
        FileUtil.delete(new File(work,packageName));
        context.sendBroadcast(new Intent(ACTION_REMOVE).putExtra(Intent.EXTRA_PACKAGE_NAME,packageName));
        
    }


  
    @Override
    public void install(String file) throws RemoteException {
        String packageName;
        try {
            Packet packet=new Packet(file);
            packageName=packet.packageName;
        } catch (IOException e) {
            throw new RemoteException(e.getMessage());
        }
        Package pkg=query(packageName);
        File path=new File(work,packageName);
        
        if(pkg!=null){
            ActivityTask.stopTask(context,packageName);
            FileUtil.delete(path);
             }
             if(path.isFile())
                 path.delete();
        try {
            ZipFile zf=new ZipFile(file);
            path.mkdirs();
             Enumeration<ZipEntry> each=(Enumeration<ZipEntry>) zf.entries();
            while(each.hasMoreElements()){
                ZipEntry ze=each.nextElement();
                if(ze.isDirectory()){
                    new File(path,ze.getName()).mkdir();
                }else{
                    File dst=new File(path,ze.getName());
                    File parent=dst.getParentFile();
                    if(!parent.exists())
                        parent.mkdirs();
                        InputStream input=zf.getInputStream(ze);
                        OutputStream output=new FileOutputStream(dst);
                        byte[] buff=new byte[1280];
                        int len=-1;
                        while((len=input.read(buff))!=-1)
                            output.write(buff,0,len);
                            output.flush();
                            output.close();
                            input.close();
                }
            }
            zf.close();
            context.sendBroadcast(new Intent(pkg==null?ACTION_ADD:ACTION_UPDATE).putExtra(Intent.EXTRA_PACKAGE_NAME,packageName));
        } catch (IOException e) {
            
        }
    }

    @Override
    public List<Package> queryAll() throws RemoteException {
        List<Package> list=new ArrayList<>();
        try {
            for (File dir:work.listFiles(this))
                list.add(new Package(dir));
        } catch (IOException e) {}
        return list;
    }
    @Override
    public boolean accept(File p1) {
        return p1.isDirectory();
    }
    
    
}
