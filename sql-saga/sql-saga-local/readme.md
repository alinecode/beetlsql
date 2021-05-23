单体应用通过saga管理多个数据库事物。

可以运行单元测试了解如何编写saga代码，模板是
```
@Test
public void test(){
    SagaContext sagaContext = SagaContext.sagaContextFactory.current();
    try{
        sagaContext.start();
        // 使用sagaMapper的数据库操作
        sagaContext.commit();
    }catch(RuntimeException ex){
        sagaContext.rollback();
    }
}

```