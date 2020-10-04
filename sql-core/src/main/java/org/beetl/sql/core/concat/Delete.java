package org.beetl.sql.core.concat;

public class Delete extends WhereNode {
    String mainTable;

    public Delete(ConcatContext ctx) {
        super(ctx);
    }

    public Delete from(Class target) {
        mainTable = ctx.nc.getTableName(target);
        return this;
    }

    public Delete from(String table) {
        this.mainTable = table;
        return this;
    }

    //Override
    public void toSql(ConcatBuilder sb) {
        sb.append("delete ");
        sb.append("from ").append(mainTable);
        //where builder
        super.toSql(sb);

    }

    public String toSql() {
        ConcatBuilder sb = ctx.concatBuilder;
        this.toSql(sb);
        return sb.toString();
    }
}
