useTest
===

    select #{use('cols')} from sys_user where #{use('useTestCondition')}

cols
===

    id,name
    
useTestCondition    
===

    id =#{id} /*hi*/
    
useTest2
===

    select #{use('cols')} from sys_user where #{globalUse('common.idCondition')}

whereTest
===

    select * from sys_user
    -- @ where(){
    or id=#{id}  
    -- @}
    
trimTest
===

    select * from sys_user
    -- @ trim({"prefix":"where","prefixOverrides":"and|or"}){
    or id=#{id}  
    -- @}
    
    
joinTest
===

    select * from sys_user where
    id in (#{join(ids)})
   
