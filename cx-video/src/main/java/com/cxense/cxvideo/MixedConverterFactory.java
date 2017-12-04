package com.cxense.cxvideo;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-17).
 */

public class MixedConverterFactory extends Converter.Factory {
    private final Converter.Factory jsonFactory;
    private final Converter.Factory scalarFactory;

    public MixedConverterFactory(Converter.Factory jsonFactory, Converter.Factory scalarFactory) {
        this.jsonFactory = jsonFactory;
        this.scalarFactory = scalarFactory;
    }

    private Converter.Factory selectFactory(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Json)
                return jsonFactory;
            if (annotation instanceof Scalar)
                return scalarFactory;
        }
        return null;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Converter.Factory factory = selectFactory(annotations);
        if (factory != null)
            return factory.responseBodyConverter(type, annotations, retrofit);
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        Converter.Factory factory = selectFactory(parameterAnnotations);
        if (factory != null)
            return factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Json {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Scalar {
    }
}
