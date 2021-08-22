package org.beetl.sql.core.concat;

public class UpdateNotEmptyExpress extends Express {


    String name;
    Express express;
    public UpdateNotEmptyExpress(String name, Express express){
        this.express = express;
        this.name = name;
    }

    @Override
    public void toSql(ConcatBuilder sb) {
        sb.appendIfNotEmptyStart(name);
        sb.comma();
        express.toSql(sb);
        sb.appendIfNotEmptyEnd();
    }





}
