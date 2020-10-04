package org.beetl.sql.act;

import act.Act;
import act.db.DbBind;
import act.db.sql.tx.Transactional;
import org.osgl.mvc.annotation.*;

import javax.inject.Inject;
import java.io.File;

import static act.controller.Controller.Util.notFoundIfNull;

/**
 * A Simple Todo application controller
 */
@SuppressWarnings("unused")
@With(BeetlSqlTransactional.class)
public class Todo {

    @Inject
    private BeetlSqlDao<Integer, TodoItem> dao;

    @GetAction
    public void home() {}

    @GetAction("file")
    public static File file() {
        return new File("target/classes/org/beetl/sql/act/Todo.class");
    }

    @GetAction("/list")
    public Iterable<TodoItem> list(String q) {
        return dao.findAll();
    }

    @PostAction("/list")
    @Transactional
    public void post(String desc) {
        TodoItem item = new TodoItem();
        item.setDesc(desc);
        dao.save(item);
    }

    @DeleteAction("/list/{id}")
    public void delete(int id) {
        dao.deleteById(id);
    }

    @PutAction("/list/{item}")
    public void update(@DbBind TodoItem item, String desc) {
        notFoundIfNull(item);
        item.setDesc(desc);
        dao.save(item);
    }

    @GetAction("/list/{item}")
    public TodoItem showItem(@DbBind TodoItem item) {
        return item;
    }
    
    public static void main(String[] args) throws Exception {
        Act.start("TODO-BeetlSql");
    }


}
