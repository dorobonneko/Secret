package com.moe.video.framework.provider;
import android.content.ContentProvider;
import android.net.Uri;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.AbstractCursor;
import android.os.Bundle;
import com.moe.video.framework.content.PackageManager;

public class PackageProvider extends ContentProvider {
    private PackageManager pml;
    @Override
    public boolean onCreate() {
        pml=new PackageManager(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri p1, String[] p2, String p3, String[] p4, String p5) {
        PackageCursor pc=new PackageCursor();
        Bundle b=new Bundle();
        pc.setExtras(b);
        b.putBinder("packageManager",pml.asBinder());
        return pc;
    }

    @Override
    public String getType(Uri p1) {
        return null;
    }

    @Override
    public Uri insert(Uri p1, ContentValues p2) {
        return null;
    }

    @Override
    public int delete(Uri p1, String p2, String[] p3) {
        return 0;
    }

    @Override
    public int update(Uri p1, ContentValues p2, String p3, String[] p4) {
        return 0;
    }
    
    
    public class PackageCursor extends AbstractCursor {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public String[] getColumnNames() {
            return null;
        }

        @Override
        public String getString(int p1) {
            return null;
        }

        @Override
        public short getShort(int p1) {
            return 0;
        }

        @Override
        public int getInt(int p1) {
            return 0;
        }

        @Override
        public long getLong(int p1) {
            return 0;
        }

        @Override
        public float getFloat(int p1) {
            return 0;
        }

        @Override
        public double getDouble(int p1) {
            return 0;
        }

        @Override
        public boolean isNull(int p1) {
            return false;
        }
    }
    
}
