package yate.avatar.adapter;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import yate.avatar.contentprovider.Avatar;

/**
 * Created by mohitd2000 on 11/15/14.
 */
public class PointsAdapter extends CursorAdapter {
    public PointsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndexOrThrow(Avatar.PointContent.COL_NAME));
        double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(Avatar.PointContent.COL_LAT));
        double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(Avatar.PointContent.COL_LONG));

        TextView titleTextView = (TextView) view.findViewById(android.R.id.text1);
        TextView addressTextView = (TextView) view.findViewById(android.R.id.text2);

        titleTextView.setText(title);

        // Get an address from a lat, long coordinate
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            String addressText = String.format(
                    "%s, %s, %s",
                    // If there's a street address, add it
                    address.getMaxAddressLineIndex() > 0 ?
                            address.getAddressLine(0) : "",
                    // Locality is usually a city
                    address.getLocality() != null ? address.getLocality() : "",
                    // The country of the address
                    address.getCountryName());
            addressTextView.setText(addressText);
        }
    }
}
