package com.moe.video.framework;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.view.View;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class SettingActivity extends PreferenceActivity implements Handler.Callback,Preference.OnPreferenceClickListener{
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler=new Handler(this);
        getPreferenceManager().setSharedPreferencesName("setting");
        addPreferencesFromResource(R.xml.setting);
        Preference background=findPreference("background");
        background.setSummary(getPreferenceScreen().getSharedPreferences().getString("background","无"));
        background.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference p1) {
        switch(p1.getKey()){
            case "background":
                if(mHandler.hasMessages(0))
                {
                    mHandler.removeMessages(0);
                    mHandler.sendEmptyMessage(1);
                }else
                mHandler.sendEmptyMessageDelayed(0,240);
                break;
        }
        return true;
    }

    @Override
    public boolean handleMessage(Message p1) {
        switch(p1.what){
            case 0:
                startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*"),322);
                break;
            case 1:
                new AlertDialog.Builder(this).setTitle("清除背景").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface p1, int p2) {
                            getPreferenceScreen().getEditor().remove("background").commit();
                        }
                    }).show();
                break;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 322:
                if(resultCode==RESULT_OK){
                    getContentResolver().takePersistableUriPermission(data.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    findPreference("background").setSummary(data.getDataString());
                    getPreferenceScreen().getEditor().putString("background",data.getDataString()).commit();
                }
                break;
        }
    }


    
    
    
}
