package com.carworkz.dearo.dirtydetector;

public interface DirtyComparator {

    boolean isEqual(Object originalObject, Object currentObject);
}
