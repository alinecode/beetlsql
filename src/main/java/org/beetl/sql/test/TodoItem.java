package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.mapper.BaseMapper;

import javax.persistence.Entity;

@Table(name = "TODO_ITEM")
public class TodoItem  {

    @AssignID
    private int id;

    private String desc;

    public TodoItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getDesc() {
        return desc;
    }


    public void setDesc(String desc) {
        this.desc = desc;
    }

    public interface Mapper extends BaseMapper<TodoItem> {
    }


}
