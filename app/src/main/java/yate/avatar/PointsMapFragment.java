package yate.avatar;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import yate.avatar.provider.Avatar;

/**
 * Created by mohitd2000 on 11/7/14.
 */
public class PointsMapFragment extends MapFragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private static final String TAG = PointsMapFragment.class.getSimpleName();

    private LocationClient mLocationClient;
    private GoogleMap mMap;

    public PointsMapFragment() {
        super();
    }

    /**
     * @see com.google.android.gms.maps.MapFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @see com.google.android.gms.maps.MapFragment#onCreateView(android.view.LayoutInflater,
     *      android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mMap = getMap();
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * @see android.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        mLocationClient = new LocationClient(getActivity(), this, this);
        mLocationClient.connect();
        getMap().setMyLocationEnabled(true);
        getMap().getUiSettings().setMyLocationButtonEnabled(true);

        setUpMap();
    }

    private void setUpMap() {
        Cursor cursor = getActivity().getContentResolver().query(Avatar.PointContent.CONTENT_URI, null, null, null, null);

        if(cursor.moveToNext()) {
            Log.d(Constants.LOG_ID, TAG + ">" + cursor.getCount());
            do {
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(cursor.getDouble(Constants.LATITUDE_INDEX),
                                cursor.getDouble(Constants.LONGITUDE_INDEX)))
                        .title(cursor.getString(Constants.NAME_INDEX)));
            } while (cursor.moveToNext());
        }
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(40.0369697, -82.8894337)).title("Home"));
    }

    /**
     * @see com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener#onConnectionFailed(com.google.android.gms.common.ConnectionResult)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    /**
     * @see com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks#onConnected(android.os.Bundle)
     */
    @Override
    public void onConnected(Bundle args) {
        Location loc = mLocationClient.getLastLocation();
        LatLng latlon = new LatLng(loc.getLatitude(), loc.getLongitude());

        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latlon, 16.0f));
    }

    /**
     * @see com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks#onDisconnected()
     */
    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub

    }

}
