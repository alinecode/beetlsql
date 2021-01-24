package org.beetl.sql.core.db;

public class KingbaseStyle extends MySqlStyle{
	public KingbaseStyle(){
		super();
		this.keyWordHandler = null;
	}
    @Override
    public String getName() {
        return "kingbase";
    }

    @Override
    public int getDBType() {
        return DBType.DB_KINGBASE;
    }

}
