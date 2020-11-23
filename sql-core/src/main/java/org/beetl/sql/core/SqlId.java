package org.beetl.sql.core;

import java.util.Objects;

/**
 * sqlId标识,通过namespace,id，type来区分唯一一个sql
 * @author xiandafu
 */
public class SqlId {

  protected  String namespace;
  protected  String id;
  /**
   * 通过sql转化成其他sql用，比如分页sql
   */
  protected Type type = Type.general;



  transient  int hashCode = 0;
  transient String sqlId;
  protected ManagedType managedType = ManagedType.resource;
  enum Type {general,page,range,count,view}
  enum ManagedType {resource,auto,template,sql}

  protected SqlId(){

  }

  protected SqlId(String namespace, String id){
    this.namespace = namespace;
    this.id = id ;

  }


  protected SqlId(String sqlId){
    this.sqlId = sqlId;
    String[] array = parseId(sqlId);
    this.namespace= array[0];
    String temp = array[1];
    if(temp.endsWith("$page")){
      this.type = Type.page;
      this.id = temp.substring(0,temp.length()-5);
    }else if(temp.endsWith("$count")){
      this.type = Type.count;
      this.id = temp.substring(0,temp.length()-6);
    }else if(temp.endsWith("$range")){
      this.type = Type.range;
      this.id = temp.substring(0,temp.length()-6);
    }else if(temp.endsWith("$view")){
      this.type = Type.view;
      this.id = temp.substring(0,temp.length()-5);
    }
    else{
      this.id = array[1];
    }


  }

  public static SqlId of(String namespace, String id){
    return new SqlId(namespace,id);
  }


  /**
   * 从一个字符串的sqlId解析得出Identity，通常这个是在sql template 里使用，比如#globalUse("common.whereCondtion")#
   * @param sqlId
   * @return
   */
  public static SqlId of(String sqlId){
    return new SqlId(sqlId);
  }

  public SqlId toPage(){
    SqlId pageSqlId = clone();
    pageSqlId.type=Type.page;
    return pageSqlId;
  }

  public SqlId toCount(){
    SqlId pageSqlId = clone();
    pageSqlId.type=Type.count;
    return pageSqlId;
  }

  public SqlId toRange(){
    SqlId rangeSqlId = clone();
    rangeSqlId.type=Type.range;
    return rangeSqlId;
  }

  public SqlId toView(Class viewType){
    SqlId viewSqlId = clone();
    viewSqlId.type=Type.view;
    viewSqlId.namespace=viewSqlId.namespace+"#"+viewType.getSimpleName();
    return viewSqlId;
  }


  public boolean isPage(){
    return this.type==Type.page;
  }
  public boolean isSql(){
  	return this.managedType==ManagedType.sql||this.managedType==ManagedType.template;
  }

  @Override
  protected SqlId clone(){
    SqlId clone = new SqlId();
    clone.namespace = this.namespace;
    clone.id = this.id;
    clone.type = this.type;
    clone.managedType = this.managedType;
    return clone;
  }



  public SqlId sibling(String id){
    SqlId newSqlId = new SqlId(namespace,id);
    return newSqlId;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getId() {
    return id;
  }


  /**
   * 解析常规sqlId。即namespace+id
   * 此方法仅仅用于UseFunction或者GlobalUseFunction等在md文件通过字符串引用sql片段
   * @param id
   * @return
   */
  protected  String[] parseId(String id){
    int index = id.lastIndexOf('.');
    return new String[]{id.substring(0,index),id.substring(index+1)};
  }




  @Override
  public String toString() {
    /*不要修改实现*/
    if(type==Type.general){
      return  namespace +"." +id;

    }else{
      return  namespace +"." +id+"$"+type;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SqlId sqlId = (SqlId) o;
    return namespace.equals(sqlId.namespace) &&
            id.equals(sqlId.id) &&
            type == sqlId.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(namespace, id, type);
  }
}
