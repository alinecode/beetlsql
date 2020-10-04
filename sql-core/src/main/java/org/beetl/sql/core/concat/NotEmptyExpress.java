package org.beetl.sql.core.concat;

public class NotEmptyExpress extends Express {


    String name;
    Express express;
    public NotEmptyExpress(String name, Express express){
        this.express = express;
        this.name = name;
    }

    @Override
    public void toSql(ConcatBuilder sb) {
        sb.appendIfNotEmptyStart(name);
        express.toSql(sb);
        sb.appendIfNotEmptyEnd();
    }





}
