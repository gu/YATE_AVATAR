package yate.avatar.syncadapter;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import yate.avatar.contentprovider.Avatar;

/**
 *
 * Just a DAO to represent a point
 *
 * Created by Mohit on 11/8/2014.
 */
public class Point {

    private int id;
    private String name;
    private double lat;
    private double lng;
    private double alt;
    private String uploadDate;
    private String link;

    public Point() {

    }

    public Point(String name, double lat, double lng, double alt, String upload_date, String link) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
        this.uploadDate = upload_date;
        this.link = link;
    }

    public Point(String name, LatLng coord, double alt, String upload_date, String link) {
        this.name = name;
        this.lat = coord.latitude;
        this.lng = coord.longitude;
        this.alt = alt;
        this.uploadDate = upload_date;
        this.link = link;
    }

    public int getID() {
        return this.id;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getCoord() {
        return new LatLng(this.lat, this.lng);
    }

    public void setCoord(LatLng coord) {
        this.lat = coord.latitude;
        this.lng = coord.longitude;
    }

    public void setCoord(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getAlt() {
        return this.alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public String getDate() {
        return this.uploadDate;
    }

    public void setDate(String date) {
        this.uploadDate = date;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public static Point fromCursor(Cursor c) {
        if (c.getCount() == 0 || !c.moveToFirst()) return null;
        String name = c.getString(c.getColumnIndexOrThrow(Avatar.PointContent.COL_NAME));
        double lat = c.getDouble(c.getColumnIndexOrThrow(Avatar.PointContent.COL_LAT));
        double lng = c.getDouble(c.getColumnIndexOrThrow(Avatar.PointContent.COL_LONG));
        double alt = c.getDouble(c.getColumnIndexOrThrow(Avatar.PointContent.COL_ALTITUDE));
        String upload_date = c.getString(c.getColumnIndexOrThrow(Avatar.PointContent.COL_UPLOAD_DATE));
        String link = c.getString(c.getColumnIndexOrThrow(Avatar.PointContent.COL_LINK));

        return new Point(name, lat, lng, alt, upload_date, link);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Avatar.PointContent.COL_NAME, name);
        values.put(Avatar.PointContent.COL_LAT, lat);
        values.put(Avatar.PointContent.COL_LONG, lng);
        values.put(Avatar.PointContent.COL_ALTITUDE, alt);
        values.put(Avatar.PointContent.COL_UPLOAD_DATE, uploadDate);
        values.put(Avatar.PointContent.COL_LINK, link);

        return values;
    }

}
