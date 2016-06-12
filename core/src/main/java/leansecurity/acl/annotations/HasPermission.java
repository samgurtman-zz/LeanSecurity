package leansecurity.acl.annotations;


/**
 * Created by sam on 20/03/16.
 */
public @interface HasPermission {
    String resourceType() default "";
    String value();
}
