package com.carworkz.dearo.dirtydetector;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DirtyDetector {

    Object originalObject, clonedCopy;

    private DirtyComparator comparator;

    public static DirtyDetector getInstance() {
        return new DirtyDetectorImpl();
    }

    public abstract void observe(@Nonnull Parcelable parcelable);

    public abstract void observe(@Nonnull Serializable serializable);

    public abstract <E extends Parcelable> void observe(@Nonnull ArrayList<E> list, @Nonnull ClassLoader elementClassLoader);

    public void observe(@Nonnull Parcelable parcelable, @Nullable DirtyComparator comparator) {
        observe(parcelable);
        this.comparator = comparator;
    }

    public void observe(@Nonnull Serializable serializable, @Nullable DirtyComparator comparator) {
        observe(serializable);
        this.comparator = comparator;
    }

    public <E extends Parcelable> void observe(@Nonnull ArrayList<E> list, @Nonnull ClassLoader elementClassLoader, @Nullable DirtyComparator comparator) {
        observe(list, elementClassLoader);
        this.comparator = comparator;
    }

    public boolean isDirty() {
        if (originalObject == null) {
            throw new IllegalStateException("No Object has been put to observe,Please make sure you called dirtyDetector().observe(obj) or check isObserving() before calling isDirty()");
        }


        if (comparator != null) {
            return !comparator.isEqual(originalObject, clonedCopy);
        }
        return !originalObject.equals(clonedCopy);
    }

    public boolean isObserving() {
        return originalObject != null;
    }
}
