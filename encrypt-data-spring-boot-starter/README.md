## 前言
对于大多数系统来说，敏感数据的加密存储都是必须考虑和实现的。最近在公司的项目中也接到了相关的安全需求，因为项目使用了 MyBatis 作为数据库持久层框架，在经过一番调研后决定使用其插件机制来实现字段加解密功能，并且封装成一个轻量级、支持配置、方便扩展的组件提供给其他项目使用。

## MyBatis 的插件机制
关于 MyBatis 插件的详细说明可以查阅[官方文档](https://mybatis.org/mybatis-3/configuration.html#plugins)

### 简介
MyBatis 提供了插件功能，它允许你拦截 MyBatis 执行过程中的某个方法，对其增加自定义操作。默认情况下，MyBatis 允许拦截的方法包括：

| 类                                                   | 方法                                                                               | 说明                 |
|------------------------------------------------------|-----------------------------------------------------------------------------------|--------------------|
| org.apache.ibatis.executor.Executor                  | update, query, flushStatements, commit, rollback, getTransaction, close, isClosed | 拦截执行器的方法           |
| org.apache.ibatis.executor.parameter.ParameterHandler| getParameterObject, setParameters                                                 | 说明：拦截参数处理的方法       |
| org.apache.ibatis.executor.resultset.ResultSetHandler| handleResultSets, handleOutputParameters                                          | 拦截结果集处理的方法         |
| org.apache.ibatis.executor.statement.StatementHandler| prepare, parameterize, batch, update, query                                       | 拦截 Sql 语句构建的方法     |

### 插件实现
在 MyBatis 中，一个插件其实就是一个拦截器，插件的实现方式非常简单，只需要实现 org.apache.ibatis.plugin.Interceptor 接口，并且通过 @Intercepts 注解指定要拦截的方法签名即可。
以下是官方文档提供的例子：

```java
// ExamplePlugin.java
@Intercepts({@Signature(
  type= Executor.class,
  method = "update",
  args = {MappedStatement.class,Object.class})})
public class ExamplePlugin implements Interceptor {
  private Properties properties = new Properties();

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // implement pre-processing if needed
    Object returnObject = invocation.proceed();
    // implement post-processing if needed
    return returnObject;
  }

  @Override
  public void setProperties(Properties properties) {
    this.properties = properties;
  }
}
```
上面的插件会拦截 org.apache.ibatis.executor.Executor#update 方法的所有调用，你可以在 invocation.proceed() 前后增加插件逻辑。
