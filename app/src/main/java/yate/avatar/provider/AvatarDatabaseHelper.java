package yate.avatar.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * An intermediary class between the raw SQLite database
 * and the Content Provider.
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
        db.execSQL("CREATE TABLE IF NOT EXISTS + " + Avatar.Point.TABLE_NAME + "("
            + Avatar.Point._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"  // Unique id
            + Avatar.Point.COL_NAME + " TEXT NOT NULL,"                 // Name
            + Avatar.Point.COL_LAT + " REAL,"                           // Latitude
            + Avatar.Point.COL_LONG + " REAL,"                          // Longitude
            + Avatar.Point.COL_ALTITUDE + " REAL,"                      // Altitude
            + Avatar.Point.COL_UPLOAD_DATE + " INTEGER,"                // Upload date (as ms since 1970)
            + Avatar.Point.COL_LINK + " TEXT"                           // Link
            + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Don't do anything yet since any upgrades will be made manually at the moment.
    }
}
