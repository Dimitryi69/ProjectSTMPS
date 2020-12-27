package dca.bstu.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Project.db";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON");
        db.execSQL("CREATE TABLE Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "login TEXT unique NOT NULL, "+
                "password TEXT NOT NULL);");
        // добавление начальных данных
        db.execSQL("CREATE TABLE Recipies (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "allcomponents TEXT NOT NULL, "+
                "name TEXT NOT NULL, "+
                "leftcomponents TEXT NOT NULL, "+
                "user_creator INTEGER NOT NULL, "+
                "ready INTEGER not null, " +
                "Image BLOB not null, " +
                "SubscribedUsers TEXT not null, " +
                "Adress TEXT not null, " +
                "Time TEXT not null, " +
                "FOREIGN KEY(user_creator) REFERENCES Users(id) ON DELETE CASCADE);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Recipies");
        onCreate(db);
    }
}
