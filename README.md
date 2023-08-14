# VertxRest

## This is the repository for Vert.x rest.

### Vert.x Rest for convenience in rest api programming

# How use

```java

@Rest(method = RequestMethod.POST, path = {"/tst" , "/test"}, db = false, dto = TestDto.class, validator = TestValidation.class)
public final class TestRest extends RestController<TestDto, String> {
    @Override
    public void restHandler(final Handler<TestDto, String> restHandler) throws Exception {
        restHandler.success(Response.TEST);
    }
}
```

### Rest

```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Rest {
    RequestMethod method() default RequestMethod.ANY;

    String[] path() default {};

    String produces() default "application/json";

    String consumes() default "application/json";

    boolean db() default true;

    Class<?> dto() default NothingDto.class;

    Class<?>[] validator() default {};

    boolean authentication() default false;

    UserRole[] roles() default UserRole.ANY;
}
```

## Rest Specifies that the class is a Rest

+ method: Specifies the request method
+ path: Specifies the route
+ produces: Specifies what this request produces
+ consumes: Specifies what this request consumes
+ db: Determines whether this Rest requires a database connection or not
+ dto: Defines the input Dto class
+ validator: It defines the classes that are used to validate the DTO
+ authentication: Checking the user login with the token created from sign-in rest
+ roles: If authentication is enabled, this item is checked and the access level is determined

### RestController

```java
public abstract class RestController<REQUEST, RESPONSE> {

    public RestController() {
    }

    abstract public void restHandler(final Handler<REQUEST, RESPONSE> restHandler) throws Exception;
}
```

### Handler

```java
public interface Handler<REQUEST, RESPONSE> {
    void fail(final Throwable throwable , final ServerResponse<RESPONSE> response);

    void fail(final Throwable throwable);

    void fail(final Throwable throwable , final RESPONSE info , final Response response);

    void fail(final ServerResponse<RESPONSE> response);

    void fail(final RESPONSE info , final Response response);

    void fail(final Response response);

    void success(final ServerResponse<RESPONSE> serverResponse);

    void success(final RESPONSE info , final Response response);

    void success(final RESPONSE info);

    void success(final Response response);

    REQUEST request();

    RoutingContext routingContext();

    Vertx vertx();

    SQLConnection sqlConnection();

    UserEntity user();
}
```

<h1 align="center">
    💻 Technologies
</h1>

<div align="center">
    <img src="https://img.shields.io/static/v1?style=for-the-badge&message=Java&color=782A90&logo=Java&logoColor=e11f21&label=" alt="Java"/>
    <img src="https://img.shields.io/static/v1?style=for-the-badge&message=Eclipse+Vert.x&color=782A90&logo=Eclipse+Vert.x&logoColor=FFFFFF&label=" alt="Vert.x"/>
</div>

<h1 align="center">
    🌟 Spread the word!
</h1>

If you want to say thank you:

- Add a GitHub Star to the project!
- Follow my GitHub [bardiademon](https://github.com/bardiademon)

<h1 align="center">
    ⚠️ License & 📝 Credits
</h1>

by bardiademon [https://bardiademon.com](https://www.bardiademon.com)
