package com.sonpm_cloud.explorea.data_classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

public class CachedRoutesDAO {

    private CachedRoutesDbHelper dbHelper;

    public CachedRoutesDAO(Context context) { dbHelper = new CachedRoutesDbHelper(context); }

    public void insertCR(final Route route,
                         final String encodedDirectionsByFoot,
                         final String encodedDirectionsByBike) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_ROUTE,
                route.encodedRoute);
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.CACHING_TIME,
                U.getCurrentMillis());
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS_FOOT,
                encodedDirectionsByFoot);
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS_BIKE,
                encodedDirectionsByBike);
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.LENGTH_BY_FOOT,
                route.lengthByFoot);
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.LENGTH_BY_BIKE,
                route.lengthByBike);
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.TIME_BY_FOOT,
                route.timeByFoot);
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.TIME_BY_BIKE,
                route.timeByBike);

        dbHelper.getWritableDatabase().insert(CachedRoutesDbHelper.Structure.NAME,
                null, contentValues);
    }

    public void insertCR(final DirectionsRoute directionsRoute) {
        insertCR(directionsRoute,
                directionsRoute.encodedDirectionsByFoot,
                directionsRoute.encodedDirectionsByBike);
    }

    public void removeCR(final Route route) {
        removeCR(route.encodedRoute);
    }

    public void removeCR(final String encodedRoute) {
        dbHelper.getWritableDatabase().delete(CachedRoutesDbHelper.Structure.NAME,
                CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_ROUTE + " = ?",
                new String[]{ encodedRoute });
    }

    @Nullable
    public DirectionsRoute getCRorNull(final Route route) {
        Cursor cursor = dbHelper.getReadableDatabase().query(
                CachedRoutesDbHelper.Structure.NAME,
                null,
                CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_ROUTE + " = ?",
                new String[]{ route.encodedRoute },
                null,
                null,
                CachedRoutesDbHelper.Structure.COLUMNS.CACHING_TIME + " DESC");

        DirectionsRoute directionsRoute = mapCursor(cursor);
        if(U.hasHourPassed(directionsRoute.queryTime)) {
            removeCR(route);
            return null;
        }
        cursor.close();
        return new DirectionsRoute(
                route.id,
                route.encodedRoute,
                directionsRoute.queryTime,
                directionsRoute.encodedDirectionsByFoot,
                directionsRoute.encodedDirectionsByBike,
                route.avgRating,
                directionsRoute.lengthByFoot,
                directionsRoute.lengthByBike,
                directionsRoute.timeByFoot,
                directionsRoute.timeByBike,
                route.city
        );
    }

    @Nullable
    public DirectionsRoute getCRorNull(final LatLng[] points) {
        String encodedRoute;
        try {
            encodedRoute = Route.tryEncode(Arrays.asList(points));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
        Cursor cursor = dbHelper.getReadableDatabase().query(
                CachedRoutesDbHelper.Structure.NAME,
                null,
                CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_ROUTE + " = ?",
                new String[]{ encodedRoute },
                null,
                null,
                CachedRoutesDbHelper.Structure.COLUMNS.CACHING_TIME + " DESC");

        DirectionsRoute directionsRoute = mapCursor(cursor);
        if(U.hasHourPassed(directionsRoute.queryTime)) {
            removeCR(encodedRoute);
            return null;
        }
        cursor.close();
        return new DirectionsRoute(
                directionsRoute.id,
                encodedRoute,
                directionsRoute.queryTime,
                directionsRoute.encodedDirectionsByFoot,
                directionsRoute.encodedDirectionsByBike,
                directionsRoute.avgRating,
                directionsRoute.lengthByFoot,
                directionsRoute.lengthByBike,
                directionsRoute.timeByFoot,
                directionsRoute.timeByBike,
                directionsRoute.city
        );
    }

    public void finish() { dbHelper.close(); }

    private DirectionsRoute mapCursor(final Cursor cursor) {
        int ID_ER = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_ROUTE);
        int ID_CT = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.CACHING_TIME);
        int ID_EDF = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS_FOOT);
        int ID_EDB = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS_BIKE);
        int ID_LBF = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.LENGTH_BY_FOOT);
        int ID_LBB = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.LENGTH_BY_BIKE);
        int ID_TBF = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.TIME_BY_FOOT);
        int ID_TBB = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.TIME_BY_BIKE);

        String ER = cursor.getString(ID_ER);
        long CT = cursor.getLong(ID_CT);
        String EDF = cursor.getString(ID_EDF);
        String EDB = cursor.getString(ID_EDB);
        int LBF = cursor.getInt(ID_LBF);
        int LBB = cursor.getInt(ID_LBB);
        int TBF = cursor.getInt(ID_TBF);
        int TBB = cursor.getInt(ID_TBB);

        return new DirectionsRoute(-1, ER, CT, EDF, EDB, 0, LBF, LBB, TBF, TBB, "");
    }
}
