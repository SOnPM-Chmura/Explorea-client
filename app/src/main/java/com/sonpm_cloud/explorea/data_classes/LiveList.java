package com.sonpm_cloud.explorea.data_classes;


import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * This class is {@link LiveData}{@code <}{@link List}{@code <E>>} extension that delegates all of
 * {@link List} methods in a way that allow observers to be notified when structural changes (such as
 * {@link List#add(Object)} or {@link List#remove(int)}) appear inside {@link LiveData} value. It also
 * partially hides possibility of {@link List} object being {@code null}, by returning values expected
 * from empty list, although trying to perform structural changes while {@link LiveData} value is null
 * will always result in {@link IndexOutOfBoundsException}
 * {@link Exception}
 *
 * @param <E> the type of elements in this list
 * @see androidx.lifecycle.LiveData
 * @see java.util.List
 */
public class LiveList<E> extends LiveData<List<E>> implements List<E> {

    public LiveList(List<E> initialValue) {
        super(initialValue);
    }

    @Override
    public int size() {
        return getValue() != null ? getValue().size() : 0;
    }

    @Override
    public boolean isEmpty() {
        return getValue() == null || getValue().isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return getValue() != null && getValue().contains(o);
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return getValue() != null ? getValue().iterator() : Collections.emptyIterator();
    }

    @Nullable
    @Override
    public Object[] toArray() {
        return getValue() != null ? getValue().toArray() : null;
    }

    @Override
    public <T> T[] toArray(@Nullable T[] a) {
        return getValue() != null ? getValue().toArray(a) : null;
    }

    @StructuralChange
    @Override
    public boolean add(E e) {
        if (getValue() != null) {
            boolean ret = getValue().add(e);
            setValue(getValue());
            return ret;
        } else throwIOOBE();
        return false;
    }

    @StructuralChange
    @Override
    public boolean remove(@Nullable Object o) {
        if (getValue() != null) {
            boolean ret = getValue().remove(o);
            setValue(getValue());
            return ret;
        } else throwIOOBE();
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return getValue() != null && getValue().containsAll(c);
    }

    @StructuralChange
    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        if (getValue() != null) {
            boolean ret = getValue().addAll(c);
            setValue(getValue());
            return ret;
        } else throwIOOBE();
        return false;
    }

    @StructuralChange
    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        if (getValue() != null) {
            boolean ret = getValue().addAll(index, c);
            setValue(getValue());
            return ret;
        } else throwIOOBE();
        return false;
    }

    @StructuralChange
    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        if (getValue() != null) {
            boolean ret = getValue().removeAll(c);
            setValue(getValue());
            return ret;
        } else throwIOOBE();
        return false;
    }

    @StructuralChange
    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        if (getValue() != null) {
            boolean ret = getValue().retainAll(c);
            setValue(getValue());
            return ret;
        } else throwIOOBE();
        return false;
    }

    @StructuralChange
    @Override
    public void clear() {
        if (getValue() != null) {
            getValue().clear();
            setValue(getValue());
        } else throwIOOBE();
    }

    @Override
    public E get(int index) {
        if (getValue() != null) return getValue().get(index);
        else throwIOOBE();
        return null;
    }

    @StructuralChange
    @Override
    public E set(int index, E element) {
        if (getValue() != null) {
            E ret = getValue().set(index, element);
            setValue(getValue());
            return ret;
        }
        else throwIOOBE();
        return null;
    }

    @StructuralChange
    @Override
    public void add(int index, E element) {
        if (getValue() != null) {
            getValue().add(index, element);
            setValue(getValue());
        }
        else throwIOOBE();
    }

    @StructuralChange
    @Override
    public E remove(int index) {
        if (getValue() != null) {
            E ret = getValue().remove(index);
            setValue(getValue());
            return ret;
        }
        else throwIOOBE();
        return null;
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return getValue() != null ? getValue().indexOf(o) : -1;
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return getValue() != null ? getValue().lastIndexOf(o) : -1;
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator() {
        return getValue() != null ? getValue().listIterator() : Collections.emptyListIterator();
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return getValue() != null ? getValue().listIterator(index) : Collections.emptyListIterator();
    }

    @NonNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (getValue() != null) return getValue().subList(fromIndex, toIndex);
        else return Collections.emptyList();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @StructuralChange
    @Override
    public void replaceAll(@NonNull UnaryOperator<E> operator) {
        if (getValue() != null) getValue().replaceAll(operator);
        else throwIOOBE();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @StructuralChange
    @Override
    public void sort(@Nullable Comparator<? super E> c) {
        if (getValue() != null) getValue().sort(c);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Spliterator<E> spliterator() {
        return getValue() != null ? getValue().spliterator() : Collections.<E>emptyList().spliterator();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @StructuralChange
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        if (getValue() != null) getValue().removeIf(filter);
        else throwIOOBE();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Stream<E> stream() {
        return getValue() != null ? getValue().stream() : Stream.empty();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Stream<E> parallelStream() {
        return getValue() != null ? getValue().stream() : Stream.empty();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void forEach(Consumer<? super E> action) {
        if (getValue() != null) getValue().forEach(action);
    }

    @NonNull
    @Override
    public String toString() {
        return getValue() != null ? getValue().toString() : Collections.emptyList().toString();
    }

    private void throwIOOBE() {
        throw new IndexOutOfBoundsException("Size: none (List is null)");
    }

    /**
     * Annotates that this method performs structural change in {@link List} held by this {@link LiveData}
     * object. If value of list is null, this method will result in {@link IndexOutOfBoundsException},
     * if it is proper list object however, it will notify observers about value post-invocation.
     * Observers will be notified each time this method is invoked, even if new value is equal to previous
     * (similarly as in case of {@link LiveData#setValue(Object)})
     */
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface StructuralChange {}
}