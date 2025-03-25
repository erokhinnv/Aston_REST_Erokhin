package utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;

public class ParseUtils {

    public static Gson createParser() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory())
                .create();
    }

    private ParseUtils() {

    }
}
