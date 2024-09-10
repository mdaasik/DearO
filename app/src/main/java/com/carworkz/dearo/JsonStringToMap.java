package com.carworkz.dearo;

import com.squareup.moshi.JsonQualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonStringToMap {
}