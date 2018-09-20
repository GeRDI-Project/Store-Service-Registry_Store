/**
 * Copyright © 2018 Tobias Weber (http://www.gerdi-project.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * A Web Service (ReST-API) to disseminate registered GeRDI store services to a frontend
 *
 * @author Tobias Weber
 */

public class StoreServiceRegistry {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(StoreServiceRegistry.class);

    private static final HashMap<String, StoreServiceInfo> CACHE_MAP =
        new HashMap<String, StoreServiceInfo>();

    /**
     * entry point for the apache spark framework - ReST API is defined via lambdas
     */
    public static void main(final String[] args) {
        
        /* SETUP */
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

        /* ReST-API */
        get("/storeservices", (req, res) -> {
            res.status(200);
            return gsonBuilder.toJson(CACHE_MAP);
        });
    }
}
