select
===

select * from order_log where id = 1
 
count
===

    select count(1)+1 from sys_user where 1=1 
    
    -- @if(isNotEmpty(name)){
    and name=#{name} 
    -- @}