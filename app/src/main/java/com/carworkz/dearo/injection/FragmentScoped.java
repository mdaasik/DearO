package com.carworkz.dearo.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by ambab on 18/8/17.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentScoped {
}
