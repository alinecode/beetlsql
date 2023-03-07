condition
===

    order_id !=#{orderId}

select1
===

    select *  from order_log 
    -- @where(){
        #{use("condition")}
    -- @}