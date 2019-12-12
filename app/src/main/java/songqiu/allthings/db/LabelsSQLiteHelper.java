package songqiu.allthings.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/16
 *
 *类描述：
 *
 ********/

public class LabelsSQLiteHelper extends SQLiteOpenHelper {
    private static String name = "Labels.db";
    private static Integer version = 1;

    public LabelsSQLiteHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table labels(id integer primary key autoincrement,name varchar(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
