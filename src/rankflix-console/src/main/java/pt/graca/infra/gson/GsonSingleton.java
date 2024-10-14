package pt.graca.infra.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;

public class GsonSingleton {
    private static Gson instance;

    private GsonSingleton() {
    }

    public static Gson getInstance() {
        if (instance == null) {
            instance = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new GsonInstantTypeAdapter())
                    .create();
        }
        return instance;
    }
}
