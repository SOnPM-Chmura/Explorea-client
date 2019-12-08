package com.sonpm_cloud.explorea.data_classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CachedRoutesDAO {

    private CachedRoutesDbHelper dbHelper;

    public CachedRoutesDAO(Context context) { dbHelper = new CachedRoutesDbHelper(context); }

    public void insertCR(final Route route, final String directionsRoute) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_ROUTE,
                route.encodedRoute);
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.CACHING_TIME,
                U.getCurrentMillis());
        contentValues.put(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS,
                directionsRoute);
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

    public void removeCR(final Route route) {
        dbHelper.getWritableDatabase().delete(CachedRoutesDbHelper.Structure.NAME,
                CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_ROUTE + " = ?",
                new String[]{ route.encodedRoute });
    }

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
                directionsRoute.encodedDirections,
                route.avgRating,
                directionsRoute.lengthByFoot,
                directionsRoute.lengthByBike,
                directionsRoute.timeByFoot,
                directionsRoute.timeByBike,
                route.city
        );
    }

    public void finish() { dbHelper.close(); }

    private DirectionsRoute mapCursor(final Cursor cursor) {
        int ID_ER = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_ROUTE);
        int ID_CT = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.CACHING_TIME);
        int ID_ED = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.ENCODED_DIRECTIONS);
        int ID_LBF = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.LENGTH_BY_FOOT);
        int ID_LBB = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.LENGTH_BY_BIKE);
        int ID_TBF = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.TIME_BY_FOOT);
        int ID_TBB = cursor.getColumnIndex(CachedRoutesDbHelper.Structure.COLUMNS.TIME_BY_BIKE);

        String ER = cursor.getString(ID_ER);
        long CT = cursor.getLong(ID_CT);
        String ED = cursor.getString(ID_ED);
        int LBF = cursor.getInt(ID_LBF);
        int LBB = cursor.getInt(ID_LBB);
        int TBF = cursor.getInt(ID_TBF);
        int TBB = cursor.getInt(ID_TBB);

        return new DirectionsRoute(-1, ER, CT, ED, 0, LBF, LBB, TBF, TBB, "");
    }
}
