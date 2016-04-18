package leansecurity.filters;

import java.util.Objects;

/**
 * Created by sam on 20/03/16.
 */
public class Permission {

    private String resourceType;
    private String resourceId;
    private String permissionGranted;

    public Permission(String resourceType, String resourceId, String permissionGranted){
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.permissionGranted = permissionGranted;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getPermissionGranted() {
        return permissionGranted;
    }

    public void setPermissionGranted(String permissionGranted) {
        this.permissionGranted = permissionGranted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(resourceType, that.resourceType) &&
                Objects.equals(resourceId, that.resourceId) &&
                Objects.equals(permissionGranted, that.permissionGranted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceType, resourceId, permissionGranted);
    }
}
