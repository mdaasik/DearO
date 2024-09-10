package com.carworkz.dearo.base;

import com.carworkz.dearo.helpers.MetaData;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created by Farhan on 3/8/17.
 */

public interface ErrorWrapper {

    int getErrorStatusCode();

    String getErrorMessage();

    Map<String,List<String>> getErrorMessages();

    String getErrorStatus();

    String getErrorCode();

    @Nullable
    MetaData getHeader();
}
