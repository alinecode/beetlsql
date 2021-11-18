package org.beetl.sql.test;


import org.beetl.core.Context;

public class CkFunction implements org.beetl.core.Function {

    @Override
    public Object call(Object[] objects, Context context) {
        return 1;
    }
}
