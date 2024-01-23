select
===

```sql

select #{page("*")} from example_tbl_agg1 
-- :pageIgnore(){
order by user_id 
-- :}
```