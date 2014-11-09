package yate.avatar.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * An intermediary class between the raw SQLite database
 * and the Content Provider.
 *
 * DO NOT CHANGE THIS FILE THIS UNLESS YOU KNOW SOMETHING ABOUT DATABASES!!!
 *
 * Created by mohitd2000 on 10/23/14.
 *
 */
public class AvatarDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "avatar.db";
    private static final int DATABASE_VERSION = 1;

    public AvatarDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Avatar.PointContent.TABLE_NAME + "("
            + Avatar.PointContent._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"  // Unique id
            + Avatar.PointContent.COL_NAME + " TEXT NOT NULL,"                 // Name
            + Avatar.PointContent.COL_LAT + " REAL,"                           // Latitude
            + Avatar.PointContent.COL_LONG + " REAL,"                          // Longitude
            + Avatar.PointContent.COL_ALTITUDE + " REAL,"                      // Altitude
            + Avatar.PointContent.COL_UPLOAD_DATE + " INTEGER,"                // Upload date (as ms since 1970)
            + Avatar.PointContent.COL_LINK + " TEXT"                           // Link
            + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Don't do anything yet since any upgrades will be made manually at the moment.
    }
}
