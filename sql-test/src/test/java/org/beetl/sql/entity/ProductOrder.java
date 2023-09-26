package org.beetl.sql.entity;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.LogicDelete;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.Version;

import java.util.Date;

@Data
@Table(name="product_order")
public class ProductOrder {
    @AutoID
    Integer id;
    @Version(1)
    Long version;
    @LogicDelete(1)
    Integer status;
    Date createDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}

