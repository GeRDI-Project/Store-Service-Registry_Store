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

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;

/**
 * This class enables the deserialisation of JSON values to Java's CharSequences
 *
 * @author Tobias Weber
 * 
 */
public class CharSequenceDeserializer implements JsonDeserializer<CharSequence> {

    @Override
    public CharSequence deserialize(JsonElement element, Type type,
            JsonDeserializationContext context) throws JsonParseException {
        return element.getAsString();
    }

}
