package com.worldreader.core.application.di.qualifiers;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier @Documented @Retention(RetentionPolicy.RUNTIME)
public @interface WorldreaderUserApiEndpoint {

}
