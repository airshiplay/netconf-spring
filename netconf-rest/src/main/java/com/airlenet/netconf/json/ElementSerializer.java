package com.airlenet.netconf.json;

import com.airlenet.data.domain.Tree;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;

public class ElementSerializer<T extends com.tailf.jnc.Element> extends JsonSerializer<T> {

    @Override
    public void serialize(T value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (jsonGenerator instanceof ToXmlGenerator) {
            jsonGenerator.writeRawValue(value.toXMLString());
        } else {
            jsonGenerator.writeRawValue(value.toJSONString());
        }
    }

}
