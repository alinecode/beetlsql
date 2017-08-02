	
query
===

    select * from user where 1=1
    @if(isNotEmpty(name)){
     and name like #"%"+name+"%"#
    @}
    @if(isNotEmpty(id)){
     and department_id = #id#
    @}

query2
===

    #use("query")#
    
query3
===

    #use("query")#
    
selectUser
===

    select #page("*")# from user where 1=1
    @if(isNotEmpty(name)){
     and name like #"%"+name+"%"#
    @}
    
    
selectUser2
===

    #use("selectUser")#
   
insertUser
===

    insert into user (name) values(#name#)
    
updateUser
===

    update user set name=#name#
    @if(isNotEmpty(departmentId)){
    ,department_id=#departmentId#
    @}
    where id=#id#

updateUser2
===

    #use("updateUser")#
  
updateUser3
===

    #use("updateUser")#