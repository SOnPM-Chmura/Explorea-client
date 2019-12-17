package com.sonpm_cloud.explorea.data_classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CachedDirectionsDbHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 4;
    public static final String DB_NAME = "CachedDirections.db";

    public CachedDirectionsDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + Structure.NAME + " ( " +
                        Structure.COLUMNS.ENCODED_ROUTE + " TEXT PRIMARY KEY, " +
                        Structure.COLUMNS.CACHING_TIME + " INTEGER, " +
                        Structure.COLUMNS.ENCODED_DIRECTIONS_FOOT + " TEXT, " +
                        Structure.COLUMNS.ENCODED_DIRECTIONS_BIKE + " TEXT, " +
                        Structure.COLUMNS.LENGTH_BY_FOOT + " INTEGER, " +
                        Structure.COLUMNS.LENGTH_BY_BIKE + " INTEGER, " +
                        Structure.COLUMNS.TIME_BY_FOOT + " INTEGER, " +
                        Structure.COLUMNS.TIME_BY_BIKE + " INTEGER," +
                        Structure.COLUMNS.BOUNDS_NE_LAT + " DOUBLE, " +
                        Structure.COLUMNS.BOUNDS_NE_LNG + " DOUBLE, " +
                        Structure.COLUMNS.BOUNDS_SW_LAT + " DOUBLE, " +
                        Structure.COLUMNS.BOUNDS_SW_LNG + " DOUBLE )"
        );
        init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Structure.NAME);
        onCreate(db);
    }

    public interface Structure {
        String NAME = "CachedDirections";

        interface COLUMNS {
            String ENCODED_ROUTE = "EncodedRoute";
            String CACHING_TIME = "CachingTime";
            String ENCODED_DIRECTIONS_FOOT = "EncodedDirectionsByFoot";
            String ENCODED_DIRECTIONS_BIKE = "EncodedDirectionsByBike";
            String LENGTH_BY_FOOT = "LengthByFoot";
            String LENGTH_BY_BIKE = "LengthByBike";
            String TIME_BY_FOOT = "TimeByFoot";
            String TIME_BY_BIKE = "TimeByBike";
            String BOUNDS_NE_LAT = "BoundsNELat";
            String BOUNDS_NE_LNG = "BoundsNELng";
            String BOUNDS_SW_LAT = "BoundsSWLat";
            String BOUNDS_SW_LNG = "BoundsSWLng";
        }
    }

    private void init(SQLiteDatabase db) {  }
}
