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

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * A Thread to listen on kafka for any newly registered store services
 *
 * @author Tobias Weber
 *
 */
public class GerdiKafkaStoreServiceInfoListener extends Thread{

    private final HashMap<String, StoreServiceInfo> map;

    private final KafkaConsumer<String, ByteBuffer> consumer;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GerdiKafkaStoreServiceInfoListener.class);

    /**
     * We must give the thread a HashMap on start-up to communicate the
     * registered store services back to the web server.
     *
     * This is not thread-safe - but considered unharmful, since there is only
     * one thread writing into the map.
     *
     * @param map HashMap used to sync between webserver and thread
     */
    public GerdiKafkaStoreServiceInfoListener(HashMap<String, StoreServiceInfo> map) {
       this.map = map;
       Properties props = new Properties();
       props.put("bootstrap.servers", "kafka-1.kafka-service.default.svc.cluster.local:9092");
       props.put("group.id", "12345"); // TODO: How-to use this attribute
       props.put("enable.auto.commit", "true");
       props.put("auto.commit.interval.ms", "1000");
       props.put("key.deserializer", "de.gerdiproject.store.data.kafka.serializer.StoreServiceInfoDeserializer");
       props.put("value.deserializer", "de.gerdiproject.store.data.kafka.serializer.StoreServiceInfoDeserializer");
       consumer = new KafkaConsumer<String, ByteBuffer>(props);
       consumer.subscribe(Arrays.asList("store"));
    }

    @Override
    public void run() {
        StoreServiceInfo storeServiceInfo;

        while(true) {
            boolean update = false;
            ConsumerRecords<String, ByteBuffer> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, ByteBuffer> record: records) {
                try {
                    storeServiceInfo = StoreServiceInfo.fromByteBuffer(record.value());
                    map.put(storeServiceInfo.getUID().toString(), storeServiceInfo);
                    update = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //persist current state when something has changed
            if(update) {
                Gson gson = new Gson();
                try {
                    gson.toJson(map, new FileWriter("cache/storeServiceInfo.json"));
                } catch (java.io.IOException e) {
                        LOGGER.warn("Could not save current state!");
                }
            }
        }
    }
}
