package com.bardiademon.controller.annotation;

import com.bardiademon.data.dto.NothingDto;
import com.bardiademon.data.enums.RequestMethod;
import com.bardiademon.data.enums.UserRole;

import javax.management.relation.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
