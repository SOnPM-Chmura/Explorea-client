package com.sonpm_cloud.explorea.data_classes;

import android.util.Pair;

import java.util.Objects;

/**
 * Container to ease passing around a mutable tuple of two objects. This object provides a sensible
 * implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 */
public class MutablePair<F, S> extends Pair<F, S> {

    public F first;
    public S second;

    /**
     * Constructor for a MutablePair.
     *
     * @param first the first object in the Pair
     * @param second the second object in the pair
     */
    public MutablePair(F first, S second) {
        super(first, second);
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) o;
        return Objects.equals(p.first, first) && Objects.equals(p.second, second);
    }

    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
    }

    @Override
    public String toString() {
        return "Pair{" + String.valueOf(first) + " " + String.valueOf(second) + "}";
    }

    /**
     * Convenience method for creating an appropriately typed mutable pair.
     * @param a the first object in the MutablePair
     * @param b the second object in the MutablePair
     * @return a MutablePair that is templatized with the types of a and b
     */
    public static <A, B> MutablePair <A, B> create(A a, B b) { return new MutablePair<A, B>(a, b); }
}
