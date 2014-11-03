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
    AvatarProvider ap = new AvatarProvider();

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

//    public void addPoint(Point point) {
//
//
//        ContentValues pointValues = new ContentValues();
//
//        pointValues.put(Avatar.Point.COL_NAME, point.getName());
//        pointValues.put(Avatar.Point.COL_LAT, point.getCoord().latitude);
//        pointValues.put(Avatar.Point.COL_LONG, point.getCoord().longitude);
//        pointValues.put(Avatar.Point.COL_ALTITUDE, point.getAlt());
//        pointValues.put(Avatar.Point.COL_UPLOAD_DATE, point.getDate());
//        pointValues.put(Avatar.Point.COL_LINK, point.getLink());
//
//        ap.insert(Avatar.AUTHORITY, pointValues);
//    }
//
//    public List getAllPoints() {
//        List<Point> pointList = new ArrayList<Point>();
//
//        String selectQuery = "SELECT * FROM " + Avatar.Point.TABLE_NAME;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                Point point = new Point();
//                point.setId(Integer.parseInt(cursor.getString(0)));
//                point.setName(cursor.getString(1));
//                point.setCoord(Double.parseDouble(cursor.getString(2)), Double.parseDouble(cursor.getString(3)));
//                point.setAlt(Double.parseDouble(cursor.getString(4)));
//                point.setDate(cursor.getString(5));
//                point.setLink(cursor.getString(6));
//
//                pointList.add(point);
//            } while(cursor.moveToNext());
//        }
//
//        return pointList;
//    }
}
