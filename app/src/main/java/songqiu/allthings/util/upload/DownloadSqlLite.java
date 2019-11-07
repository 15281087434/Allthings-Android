package songqiu.allthings.util.upload;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/15.
 */

public class DownloadSqlLite extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "download.db";
    public static final String TABLE_NAME  = "download";
    static DownloadSqlLite downloadSqlLite;
    private DownloadSqlLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private DownloadSqlLite(Context context){
        this(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    public synchronized static DownloadSqlLite getInstance(Context context){

        if(downloadSqlLite==null){
            synchronized (DownloadSqlLite.class){
                if(downloadSqlLite ==null){
                    downloadSqlLite = new DownloadSqlLite(context);
                }
            }
        }
        return downloadSqlLite;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME +"(threadId INTEGER,downloadUrl TEXT,startPos INTEGER,endPos INTEGER ,downBlock INTEGER ,blockSize INTEGER ,saveFilePath TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private SQLiteDatabase db;
    public synchronized boolean insertDownloadInfo(DownloadInfo downloadInfo){
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("threadId",downloadInfo.getThreadId());
        cv.put("downloadUrl",downloadInfo.getDownloadUrl());
        cv.put("startPos",downloadInfo.getStartPos());
        cv.put("endPos",downloadInfo.getEndPos());
        cv.put("downBlock",downloadInfo.getDownBlock());
        cv.put("blockSize",downloadInfo.getBlockSize());
        cv.put("saveFilePath",downloadInfo.getSaveFilePath());
        long over = db.insert(TABLE_NAME,null,cv);
        db.close();
        return over != -1;
    }

    public synchronized boolean deleteDownloadInfo(String downloadUrl){
        db = getWritableDatabase();
        int over = db.delete(TABLE_NAME,"downloadUrl = ?",new String[]{downloadUrl});
        db.close();
        return over != 0;
    }

    public synchronized boolean updateDownpos(DownloadInfo downloadInfo){
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("downBlock",downloadInfo.getDownBlock());
        int over = db.update(TABLE_NAME,cv,"threadId = ? and downloadUrl = ?",new String[]{downloadInfo.getThreadId()+"",downloadInfo.getDownloadUrl()});
        db.close();
        return over > 0;
    }

    public List<DownloadInfo> queryDownloadInfo(String downloadUrl){
        List<DownloadInfo> list = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,new String[]{"threadId","downloadUrl","startPos","endPos","downBlock","blockSize","saveFilePath"},"downloadUrl = ?",new String[]{downloadUrl},null,null,null);
        while(cursor.moveToNext()){
            DownloadInfo info = new DownloadInfo();
            info.setThreadId(cursor.getInt(cursor.getColumnIndex("threadId")));
            info.setDownloadUrl(cursor.getString(cursor.getColumnIndex("downloadUrl")));
            info.setStartPos(cursor.getLong(cursor.getColumnIndex("startPos")));
            info.setEndPos(cursor.getLong(cursor.getColumnIndex("endPos")));
            info.setDownBlock(cursor.getLong(cursor.getColumnIndex("downBlock")));
            info.setBlockSize(cursor.getLong(cursor.getColumnIndex("blockSize")));
            info.setSaveFilePath(cursor.getString(cursor.getColumnIndex("saveFilePath")));
            list.add(info);
        }
        db.close();
        cursor.close();
        return list;
    }



}
