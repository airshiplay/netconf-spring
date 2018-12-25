package com.airlenet.netconf.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.tailf.jnc.Element;
import com.tailf.jnc.NodeSet;
import org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer;
import org.springframework.util.ClassUtils;

import java.io.IOException;

/**
 * Created by airlenet on 2017/9/2.
 */
public class ElementSerializers extends com.fasterxml.jackson.databind.ser.Serializers.Base {

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        if (ClassUtils.isAssignable(Element.class, type.getRawClass())) {
            return new ElementSerializer();
        }
        return super.findSerializer(config, type, beanDesc);
    }
}
