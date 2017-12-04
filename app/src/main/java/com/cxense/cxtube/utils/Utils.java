package com.cxense.cxtube.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.cxense.cxtube.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-15).
 */

public class Utils {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

    private Utils() {
    }

    public static Date parseDate(@NonNull String dateString) throws ParseException {
        return DATE_FORMAT.parse(dateString);
    }

    public static void loadImage(@NonNull ImageView destination, @Nullable String url) {
        // Picasso will throw exception if url is empty
        if (url != null && url.isEmpty())
            url = null;
        Picasso.with(destination.getContext())
                .load(url)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(destination);
    }
}
