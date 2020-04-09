package org.beetl.sql.test;

import java.math.*;
import java.util.Date;
import java.sql.Timestamp;
import org.beetl.sql.core.annotatoin.Table;

import java.io.Serializable;
/*
 *
 * gen by beetlsql 2020-04-03
 */
@Table(name="order")
public class Order  implements Serializable {

    // alias
    public static final String ALIAS_id = "id";
    public static final String ALIAS_finish = "finish";
    public static final String ALIAS_num = "num";
    public static final String ALIAS_address = "address";
    public static final String ALIAS_gid = "gid";
    public static final String ALIAS_price = "price";
    public static final String ALIAS_realname = "realname";
    public static final String ALIAS_tel = "tel";
    public static final String ALIAS_trackingno = "trackingno";
    public static final String ALIAS_update_time = "update_time";
    public static final String ALIAS_userid = "userid";
    public static final String ALIAS_username = "username";

    /*
    订单编号
    */
    private Integer id ;
    /*
    1. 完成 2.未完成
    */
    private Integer finish ;
    /*
    兑换数量
    */
    private Integer num ;
    /*
    住址
    */
    private String address ;
    /*
    礼品名称
    */
    private String gid ;
    /*
    订单金额
    */
    private String price ;
    /*
    真实姓名
    */
    private String realname ;
    /*
    电话
    */
    private String tel ;
    /*
    快递单号
    */
    private String trackingno ;
    /*
    订单时间
    */
    private Long updateTime ;
    /*
    用户ID
    */
    private String userid ;
    /*
    兑换用户
    */
    private String username ;

    public Order() {
    }

    /**
     * 订单编号
     *@return
     */
    public Integer getId(){
        return  id;
    }
    /**
     * 订单编号
     *@param  id
     */
    public void setId(Integer id ){
        this.id = id;
    }

    /**
     * 1. 完成 2.未完成
     *@return
     */
    public Integer getFinish(){
        return  finish;
    }
    /**
     * 1. 完成 2.未完成
     *@param  finish
     */
    public void setFinish(Integer finish ){
        this.finish = finish;
    }

    /**
     * 兑换数量
     *@return
     */
    public Integer getNum(){
        return  num;
    }
    /**
     * 兑换数量
     *@param  num
     */
    public void setNum(Integer num ){
        this.num = num;
    }

    /**
     * 住址
     *@return
     */
    public String getAddress(){
        return  address;
    }
    /**
     * 住址
     *@param  address
     */
    public void setAddress(String address ){
        this.address = address;
    }

    /**
     * 礼品名称
     *@return
     */
    public String getGid(){
        return  gid;
    }
    /**
     * 礼品名称
     *@param  gid
     */
    public void setGid(String gid ){
        this.gid = gid;
    }

    /**
     * 订单金额
     *@return
     */
    public String getPrice(){
        return  price;
    }
    /**
     * 订单金额
     *@param  price
     */
    public void setPrice(String price ){
        this.price = price;
    }

    /**
     * 真实姓名
     *@return
     */
    public String getRealname(){
        return  realname;
    }
    /**
     * 真实姓名
     *@param  realname
     */
    public void setRealname(String realname ){
        this.realname = realname;
    }

    /**
     * 电话
     *@return
     */
    public String getTel(){
        return  tel;
    }
    /**
     * 电话
     *@param  tel
     */
    public void setTel(String tel ){
        this.tel = tel;
    }

    /**
     * 快递单号
     *@return
     */
    public String getTrackingno(){
        return  trackingno;
    }
    /**
     * 快递单号
     *@param  trackingno
     */
    public void setTrackingno(String trackingno ){
        this.trackingno = trackingno;
    }

    /**
     * 订单时间
     *@return
     */
    public Long getUpdateTime(){
        return  updateTime;
    }
    /**
     * 订单时间
     *@param  updateTime
     */
    public void setUpdateTime(Long updateTime ){
        this.updateTime = updateTime;
    }

    /**
     * 用户ID
     *@return
     */
    public String getUserid(){
        return  userid;
    }
    /**
     * 用户ID
     *@param  userid
     */
    public void setUserid(String userid ){
        this.userid = userid;
    }

    /**
     * 兑换用户
     *@return
     */
    public String getUsername(){
        return  username;
    }
    /**
     * 兑换用户
     *@param  username
     */
    public void setUsername(String username ){
        this.username = username;
    }


}