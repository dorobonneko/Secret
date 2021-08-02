package com.moe.video.framework.Engine;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.Map;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;

public class Database
{
	private Context context;
	private String packageName;
	private SharedPreferences mShared;
	private SQLiteDatabase sql;
	public Database(Context context,String packageName){
		this.context=context;
		this.packageName=packageName;
		sql=context.openOrCreateDatabase(packageName,context.MODE_PRIVATE,null);
	}
	public void put(String key,String value){
		if(mShared==null)
			mShared=context.getSharedPreferences(packageName,0);
			mShared.edit().putString(key,value).commit();
	}
	public String get(String key,String defaultValue){
		if(mShared==null)
			mShared=context.getSharedPreferences(packageName,0);
			return mShared.getString(key,defaultValue);
	}
	public void delete(String key){
		mShared.edit().remove(key).commit();
	}
	public DatabaseEditer getEditor(String table){
		return new DatabaseEditer(sql,table);
	}
	public void close(){
		sql.close();
	}
	public static class DatabaseEditer{
		private SQLiteDatabase sql;
		private String table;
		public DatabaseEditer(SQLiteDatabase sql,String table){
			this.sql=sql;
			this.table=table;
		}
		public boolean isExists(){
			Cursor cursor=sql.rawQuery("SELECT * FROM sqlite_master where type='table' and name='"+table+"'",null);
			boolean exists=cursor.getCount()>0;
			cursor.close();
			return exists;
		}
		public void create(String keys){
			StringBuilder sb=new StringBuilder("create table ");
			sb.append(table);
			sb.append("(");
			for(String key:keys.split(",")){
				sb.append(key).append(" TEXT,");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(");");
			sql.execSQL(sb.toString());
		}
        public void drop(){
            sql.execSQL("drop table "+table);
        }
		public List<Map<String,String>> query(String where)
		{
			Cursor cursor=sql.rawQuery(where==null?String.format("select * from %s",table):String.format("select * from %s where %s",new String[]{table,where}),null);
			List<Map<String,String>> list=new ArrayList<>();
			while(cursor.moveToNext()){
				Map<String,String> map=new HashMap<>();
				list.add(map);
				for(int i=0;i<cursor.getColumnCount();i++){
					map.put(cursor.getColumnName(i),cursor.getString(i));
				}
			}
			cursor.close();
			return list;
		}
        public List<Map<String,String>> query(String key,String value){
            return query(key+"='"+value+"'");
        }
		public void delete(String where){
			sql.execSQL(String.format("delete from %s where %s",table,where));
		}
		public void insert(NativeObject obj){
			StringBuilder sb=new StringBuilder("insert into ");
			sb.append(table);
			sb.append("(");
			String value="";
			for(Map.Entry<Object,Object> line:obj.entrySet()){
				sb.append(ScriptRuntime.toString(line.getKey())).append(",");
				value+="'"+ScriptRuntime.toString(line.getValue())+"',";
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(") values(");
			sb.append(value,0,value.length()-1);
			sb.append(")");
			sql.execSQL(sb.toString());
		}
	}
}
