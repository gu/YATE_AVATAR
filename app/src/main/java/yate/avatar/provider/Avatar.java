package yate.avatar.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A class that holds the database and ContentProvider constants
 *
 * Created by mohitd2000 on 10/23/14.
 */
public final class Avatar {
    private Avatar() {
    }

    public static final String AUTHORITY = "com.avatar";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Populate it with the a class that has constants for database columns
    public static final class PointContent implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("points").build();

        // Database columns
        public static final String TABLE_NAME = "point";

        public static final String COL_NAME = "name";
        public static final String COL_LAT = "lat";
        public static final String COL_LONG = "lng";
        public static final String COL_ALTITUDE = "altitude";
        public static final String COL_UPLOAD_DATE = "upload_date";
        public static final String COL_LINK = "link";

        public static final String DEFAULT_SORT_ORDER = COL_NAME + " DESC";

        // Solely for the ContentProvider
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.avatar.point";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.avatar.point";
    }
}
