package yate.avatar.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * The main ContentProvider for AVATAR
 *
 * Created by mohitd2000 on 10/27/14.
 *
 */
public class AvatarProvider extends ContentProvider {

    private AvatarDatabaseHelper dbHelper;

    // For the UriMatcher
    private static final int POINTS = 100;
    private static final int POINT_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // To map our constants to the column names in the database
    private static final HashMap<String, String> pointsProjectionMap;

    static {
        // Add matchable uris
        uriMatcher.addURI(Avatar.AUTHORITY, "points", POINTS);      // For all points
        uriMatcher.addURI(Avatar.AUTHORITY, "points/*", POINT_ID);  // For a specific point

        pointsProjectionMap = new HashMap<String, String>();
        pointsProjectionMap.put(Avatar.Point._ID, Avatar.Point._ID);
        pointsProjectionMap.put(Avatar.Point.COL_NAME, Avatar.Point.COL_NAME);
        pointsProjectionMap.put(Avatar.Point.COL_LAT, Avatar.Point.COL_LAT);
        pointsProjectionMap.put(Avatar.Point.COL_LONG, Avatar.Point.COL_LONG);
        pointsProjectionMap.put(Avatar.Point.COL_ALTITUDE, Avatar.Point.COL_ALTITUDE);
        pointsProjectionMap.put(Avatar.Point.COL_UPLOAD_DATE, Avatar.Point.COL_UPLOAD_DATE);
        pointsProjectionMap.put(Avatar.Point.COL_LINK, Avatar.Point.COL_LINK);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new AvatarDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Avatar.Point.TABLE_NAME);
        builder.setProjectionMap(pointsProjectionMap);

        final int match = uriMatcher.match(uri);
        switch (match) {
            case POINTS:
                // Do nothing, just getting all of the points
                break;
            case POINT_ID:
                // Get the point in question via its unique id
                builder.appendWhere(Avatar.Point._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Avatar.Point.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = builder.query(db, projection, selection, selectionArgs, null, null, orderBy);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = 0;
        Uri result = null;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case POINTS:
                id = db.insertOrThrow(Avatar.Point.TABLE_NAME, null, contentValues);
                result = ContentUris.withAppendedId(Avatar.Point.CONTENT_URI, id);
                break;
        }

        if (id > 0 && result != null) {
            getContext().getContentResolver().notifyChange(uri, null);
            return result;
        }

        throw new SQLiteException("Unable to insert row into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = 0;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case POINTS:
                rows = db.update(Avatar.Point.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case POINT_ID:
                String pointId = uri.getLastPathSegment();

                // In case there's a selection AND a point id, might as well use them both
                rows = db.update(Avatar.Point.TABLE_NAME, contentValues,
                        Avatar.Point._ID + "=" + pointId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = 0;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case POINTS:
                rows = db.delete(Avatar.Point.TABLE_NAME, selection, selectionArgs);
                break;
            case POINT_ID:
                String pointId = uri.getLastPathSegment();

                // In case there's a selection AND a point id, might as well use them both
                rows = db.delete(Avatar.Point.TABLE_NAME,
                        Avatar.Point._ID + "=" + pointId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case POINTS:
                return Avatar.Point.CONTENT_TYPE;
            case POINT_ID:
                return Avatar.Point.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

}
