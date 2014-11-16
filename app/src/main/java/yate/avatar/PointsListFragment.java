package yate.avatar;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;

import yate.avatar.adapter.PointsAdapter;
import yate.avatar.contentprovider.Avatar;

/**
 * Created by mohitd2000 on 11/15/14.
 */
public class PointsListFragment extends ListFragment {
    private static final String[] PROJECTION = {
            Avatar.PointContent._ID,
            Avatar.PointContent.COL_NAME,
            Avatar.PointContent.COL_LAT,
            Avatar.PointContent.COL_LONG,
    };

    private PointsAdapter adapter;

    public PointsListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor c = getActivity().getContentResolver().query(Avatar.PointContent.CONTENT_URI, PROJECTION, null, null, null);
        adapter = new PointsAdapter(getActivity(), c, 0);
        setListAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // To refresh the data
        Cursor c = getActivity().getContentResolver().query(Avatar.PointContent.CONTENT_URI, PROJECTION, null, null, null);
        adapter.changeCursor(c);
    }
}
