package yate.avatar;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import yate.avatar.provider.Avatar;

/**
 * Created by Lou on 11/2/2014.
 */
public class Point {

    int _id;
    String _name;
    double _lat;
    double _lng;
    double _alt;
    String _upload_date;
    String _link;

    public Point() {

    }

    public Point(int id, String name, LatLng coord, double alt, String upload_date, String link) {
        this._id = id;
        this._name = name;
        this._lat = coord.latitude;
        this._lng = coord.longitude;
        this._alt = alt;
        this._upload_date = upload_date;
        this._link = link;
    }

    public Point(String name, LatLng coord, double alt, String upload_date, String link) {
        this._name = name;
        this._lat = coord.latitude;
        this._lng = coord.longitude;
        this._alt = alt;
        this._upload_date = upload_date;
        this._link = link;
    }

    public int getID() {
        return this._id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public LatLng getCoord() {
        return new LatLng(this._lat, this._lng);
    }

    public void setCoord(LatLng coord) {
        this._lat = coord.latitude;
        this._lng = coord.longitude;
    }

    public void setCoord(double lat, double lng) {
        this._lat = lat;
        this._lng = lng;
    }

    public double getAlt() {
        return this._alt;
    }

    public void setAlt(double alt) {
        this._alt = alt;
    }

    public String getDate() {
        return this._upload_date;
    }

    public void setDate(String date) {
        this._upload_date = date;
    }

    public String getLink() {
        return this._link;
    }

    public void setLink(String link) {
        this._link = link;
    }

    public static Point fromCursor(Cursor c) {
        String name = c.getString(Constants.NAME_INDEX);
        double lat = c.getDouble(Constants.LATITUDE_INDEX);
        double lng = c.getDouble(Constants.LONGITUDE_INDEX);
        double alt = c.getDouble(Constants.ALTITUDE_INDEX);
        String upload_date = c.getString(Constants.UPLOAD_DATE_INDEX);
        String link = c.getString(Constants.LINK_INDEX);

        return new Point(name, new LatLng(lat, lng), alt, upload_date, link);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Avatar.PointContent.COL_NAME, _name);
        values.put(Avatar.PointContent.COL_LAT, _lat);
        values.put(Avatar.PointContent.COL_LONG, _lng);
        values.put(Avatar.PointContent.COL_ALTITUDE, _alt);
        values.put(Avatar.PointContent.COL_UPLOAD_DATE, _upload_date);
        values.put(Avatar.PointContent.COL_LINK, _link);

        return values;
    }

}
