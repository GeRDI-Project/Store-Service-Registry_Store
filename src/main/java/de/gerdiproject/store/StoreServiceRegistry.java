package de.gerdiproject.store;

import de.gerdiproject.store.data.model.StoreServiceInfo;
import de.gerdiproject.store.CharSequenceDeserializer;
import de.gerdiproject.store.GerdiKafkaStoreServiceInfoListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.HashMap;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import static spark.Spark.*;

public class StoreServiceRegistry {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(StoreServiceRegistry.class);

    private static final HashMap<String, StoreServiceInfo> CACHE_MAP =
        new HashMap<String, StoreServiceInfo>();

    public static void main(final String[] args) {
        port(8080);
        Gson gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(CharSequence.class, new CharSequenceDeserializer())
            .create();

        try {
            HashMap <String, StoreServiceInfo> persistedMap = new HashMap<String, StoreServiceInfo>();
            Type listType = new TypeToken<HashMap<String, StoreServiceInfo>>() {
            }.getType();
            persistedMap =
                gsonBuilder.fromJson(
                    new FileReader("cache/storeServiceInfo.json"),
                    listType);
            for (StoreServiceInfo storeServiceInfo : persistedMap.values()) {
                CACHE_MAP.put(storeServiceInfo.getUID().toString(), storeServiceInfo);
            }
        } catch (java.io.FileNotFoundException e) {
            LOGGER.info("Persisted storeService hash has no entries");
        }

        GerdiKafkaStoreServiceInfoListener listener = new GerdiKafkaStoreServiceInfoListener(CACHE_MAP);
        listener.start();

        get("/storeservices", (req, res) -> {
            res.status(200);
            return gsonBuilder.toJson(CACHE_MAP);
        });
    }
}
