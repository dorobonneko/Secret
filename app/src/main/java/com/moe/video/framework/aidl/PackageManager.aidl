package com.moe.video.framework.aidl;

import com.moe.video.framework.content.Package;
import android.graphics.Bitmap;

interface PackageManager {
oneway void install(String file);
List<Package> queryAll();
Package query(String packageName);
void unInstall(String packageName);
}
