package com.sonpm_cloud.explorea;

import android.util.Pair;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedList;
import java.util.List;

public class Activity5_Fragment_ViewModel
        extends ViewModel {

    private List<Pair<Marker, String>> points;
    public List<Pair<Marker, String>> getPoints() { return new LinkedList<>(points); }
    public void clearPoints() { points = new LinkedList<>(); }

    public Activity5_Fragment_ViewModel() {
        points = new LinkedList<>();
    }

    public void addPoint(Pair<Marker, String> point) {
        points.add(point);
    }

    public void removePoint(Pair<Marker, String> point) {
        removePoint(point.first);
    }

    public void removePoint(Marker point) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).first.equals(point)) {
                points.remove(i);
                System.out.println("<><><><><><><><><>   " + i);
                break;
            }
        }
    }

    public void swapPoints(Pair<Marker, String> p1, Pair<Marker, String> p2) {
        int pos1 = -1, pos2 = -1;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).first.equals(p1.first)) {
                pos1 = i;
                break;
            }
        }
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).first.equals(p2.first)) {
                pos2 = i;
                break;
            }
        }
        if (pos1 == -1 || pos2 == -1) return;
        Pair<Marker, String> temp = points.get(pos1);
        points.set(pos1, points.get(pos2));
        points.set(pos2, temp);

    }
}
