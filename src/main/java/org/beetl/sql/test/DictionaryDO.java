package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.Table;

@Table(name = "dictionary")
public class DictionaryDO {
  private Long id;
  private String name;
  private String description;
  private String scope;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }
}
