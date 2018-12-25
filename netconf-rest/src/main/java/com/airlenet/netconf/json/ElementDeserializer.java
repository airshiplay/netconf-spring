package com.airlenet.netconf.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.tailf.jnc.Element;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.YangElement;

import java.io.IOException;

public class ElementDeserializer<T extends com.tailf.jnc.Element> extends JsonDeserializer<T> {

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            YangElement.readXml(jsonParser.getText());
            Element.readXml(jsonParser.getText());
        } catch (JNCException e) {
            e.printStackTrace();
        }
        return null;
    }
}
