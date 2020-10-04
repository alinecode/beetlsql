package org.beetl.sql.gen;

import java.io.StringWriter;
import java.io.Writer;

public class DummyProject extends  BaseProject{

    @Override
    public Writer getWriterByName(String sourceBuilderName, String targetName) {
        return new StringWriter();
    }


}
