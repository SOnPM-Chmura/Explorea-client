package com.sonpm_cloud.explorea.data_classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Arrays;

public class CachedDirectionsDAO {

    private CachedDirectionsDbHelper dbHelper;

    public CachedDirectionsDAO(Context context) { dbHelper = new CachedDirectionsDbHelper(context); }

    public void insertCR(final Route route,
                         final String encodedDirectionsByFoot,
                         final String encodedDirectionsByBike) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CachedDirectionsDbHelper.Structure.COLUMNS.ENCODED_ROUTE,
                route.encodedRoute);
        contentValues.put(CachedDirectionsDbHelper.Structure.COLUMNS.CACHING_TIME,
                U.getCurrentMillis());
        contentValues.put(CachedDirectionsDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS_FOOT,
                encodedDirectionsByFoot);
        contentValues.put(CachedDirectionsDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS_BIKE,
                encodedDirectionsByBike);
        contentValues.put(CachedDirectionsDbHelper.Structure.COLUMNS.LENGTH_BY_FOOT,
                route.lengthByFoot);
        contentValues.put(CachedDirectionsDbHelper.Structure.COLUMNS.LENGTH_BY_BIKE,
                route.lengthByBike);
        contentValues.put(CachedDirectionsDbHelper.Structure.COLUMNS.TIME_BY_FOOT,
                route.timeByFoot);
        contentValues.put(CachedDirectionsDbHelper.Structure.COLUMNS.TIME_BY_BIKE,
                route.timeByBike);

        dbHelper.getWritableDatabase().insert(CachedDirectionsDbHelper.Structure.NAME,
                null, contentValues);
    }

    public void insertCR(final DirectionsRoute directionsRoute) {
        insertCR(directionsRoute,
                directionsRoute.encodedDirectionsByFoot,
                directionsRoute.encodedDirectionsByBike);
    }

    public void removeCR(final Route route) { removeCR(route.encodedRoute); }

    public void removeCR(final String encodedRoute) {
        dbHelper.getWritableDatabase().delete(CachedDirectionsDbHelper.Structure.NAME,
                CachedDirectionsDbHelper.Structure.COLUMNS.ENCODED_ROUTE + " = ?",
                new String[]{ encodedRoute });
    }

    @Nullable
    public DirectionsRoute getCDRorNull(final Route route) {
        return getCDRorNull(route.encodedRoute, route.id, route.avgRating, route.city);
    }

    @Nullable
    public DirectionsRoute getCDRorNull(final LatLng[] points) {
        String encodedRoute;
        try {
            encodedRoute = Route.tryEncode(Arrays.asList(points));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
        return getCDRorNull(encodedRoute, null, null, null);
    }

    private DirectionsRoute getCDRorNull(@NonNull final String encodedRoute,
                                         @Nullable Long id,
                                         @Nullable Float avgRating,
                                         @Nullable String city) {

        Cursor cursor = dbHelper.getReadableDatabase().query(
                CachedDirectionsDbHelper.Structure.NAME,
                null,
                CachedDirectionsDbHelper.Structure.COLUMNS.ENCODED_ROUTE + " = ?",
                new String[]{ encodedRoute },
                null,
                null,
                CachedDirectionsDbHelper.Structure.COLUMNS.CACHING_TIME + " DESC");

        DirectionsRoute directionsRoute = mapCursor(cursor);
        if(U.hasHourPassed(directionsRoute.queryTime)) {
            removeCR(encodedRoute);
            return null;
        }
        cursor.close();
        return new DirectionsRoute(
                id == null ? directionsRoute.id : id,
                encodedRoute,
                directionsRoute.queryTime,
                directionsRoute.encodedDirectionsByFoot,
                directionsRoute.encodedDirectionsByBike,
                avgRating == null ? directionsRoute.avgRating : avgRating,
                directionsRoute.lengthByFoot,
                directionsRoute.lengthByBike,
                directionsRoute.timeByFoot,
                directionsRoute.timeByBike,
                city == null ? directionsRoute.city : city,
                directionsRoute.bounds);
    }

    public void finish() { dbHelper.close(); }

    private DirectionsRoute mapCursor(final Cursor cursor) {
        int ID_ER = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.ENCODED_ROUTE);
        int ID_CT = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.CACHING_TIME);
        int ID_EDF = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS_FOOT);
        int ID_EDB = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS_BIKE);
        int ID_LBF = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.LENGTH_BY_FOOT);
        int ID_LBB = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.LENGTH_BY_BIKE);
        int ID_TBF = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.TIME_BY_FOOT);
        int ID_TBB = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.TIME_BY_BIKE);
        int ID_BNELT = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.BOUNDS_NE_LAT);
        int ID_BNELG = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.BOUNDS_NE_LNG);
        int ID_BSWLT = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.BOUNDS_SW_LAT);
        int ID_BSWLG = cursor.getColumnIndex(CachedDirectionsDbHelper.Structure.COLUMNS.BOUNDS_SW_LNG);

        String ER = cursor.getString(ID_ER);
        long CT = cursor.getLong(ID_CT);
        String EDF = cursor.getString(ID_EDF);
        String EDB = cursor.getString(ID_EDB);
        int LBF = cursor.getInt(ID_LBF);
        int LBB = cursor.getInt(ID_LBB);
        int TBF = cursor.getInt(ID_TBF);
        int TBB = cursor.getInt(ID_TBB);
        double BNELT = cursor.getDouble(ID_BNELT);
        double BNELG = cursor.getDouble(ID_BNELG);
        double BSWLT = cursor.getDouble(ID_BSWLT);
        double BSWLG = cursor.getDouble(ID_BSWLG);

        return new DirectionsRoute(-1, ER, CT, EDF, EDB, 0, LBF, LBB, TBF, TBB, "",
                                   new LatLngBounds(new LatLng(BNELT, BNELG), new LatLng(BSWLT, BSWLG)));
    }
}
