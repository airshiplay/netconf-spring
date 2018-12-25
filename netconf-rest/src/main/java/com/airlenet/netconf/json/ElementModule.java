package com.airlenet.netconf.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class ElementModule extends Module {


    @Override
    public String getModuleName() {
        return ElementModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(Module.SetupContext context) {
        context.addSerializers(new ElementSerializers());
        context.addDeserializers(new ElementDeserializers());
    }

}