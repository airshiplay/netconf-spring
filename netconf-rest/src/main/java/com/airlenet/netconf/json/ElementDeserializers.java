package com.airlenet.netconf.json;

import com.fasterxml.jackson.databind.*;
import com.tailf.jnc.Element;
import org.springframework.util.ClassUtils;

public class ElementDeserializers extends com.fasterxml.jackson.databind.deser.Deserializers.Base {

    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        if (ClassUtils.isAssignable(Element.class, type.getRawClass())) {
            return new ElementDeserializer();
        }

        return super.findBeanDeserializer(type, config, beanDesc);
    }
}
