package com.carworkz.dearo.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Farhan on 26/7/17.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Remote {
}
