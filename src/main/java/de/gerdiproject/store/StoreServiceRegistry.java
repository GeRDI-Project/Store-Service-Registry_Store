package de.gerdiproject.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class StoreServiceRegistry {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(StoreServiceRegistry.class);

    public static void main(final String[] args) {
        port(8080);
        staticFiles.location("/static");

        get("/storeservices", (req, res) -> {
            res.status(200);
            return "{ \"foo\": \"bar\" }";
        });
    }

}
