package yate.avatar.provider;

import android.net.Uri;

/**
 * Created by mohitd2000 on 10/23/14.
 */
public final class AVATAR {
    private AVATAR() {
    }

    public static final String AUTHORITY = "yate.avatar.provider.AVATARProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Populate it with the a class that has contants for database columns
}
