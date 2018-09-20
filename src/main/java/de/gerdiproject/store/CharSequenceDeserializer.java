package de.gerdiproject.store;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;

public class CharSequenceDeserializer implements JsonDeserializer<CharSequence> {

    @Override
    public CharSequence deserialize(JsonElement element, Type type,
            JsonDeserializationContext context) throws JsonParseException {
        return element.getAsString();
    }

}
