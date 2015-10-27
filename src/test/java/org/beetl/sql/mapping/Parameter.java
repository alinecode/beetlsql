package org.beetl.sql.mapping;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;



@Table(name="sys_parametr_info")
public class Parameter {

    private String parametrName;
    private String parametrValue;
    private String parametrModule;
    private String parametrDesc;

    @AssignID
    public String getParametrName() {
        return parametrName;
    }

    public void setParametrName(String parametrName) {
        this.parametrName = parametrName;
    }

    public String getParametrValue() {
        return parametrValue;
    }

    public void setParametrValue(String parametrValue) {
        this.parametrValue = parametrValue;
    }

    public String getParametrModule() {
        return parametrModule;
    }

    public void setParametrModule(String parametrModule) {
        this.parametrModule = parametrModule;
    }

    public String getParametrDesc() {
        return parametrDesc;
    }

    public void setParametrDesc(String parametrDesc) {
        this.parametrDesc = parametrDesc;
    }
}
