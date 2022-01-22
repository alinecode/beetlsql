condition
===

    order_id !=#{orderId}

select
===

    select *  from order_log 
    -- @where(){
        #{use("condition")}
    -- @}