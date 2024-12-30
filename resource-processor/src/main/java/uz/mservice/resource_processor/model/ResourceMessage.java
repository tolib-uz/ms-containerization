package uz.mservice.resource_processor.model;

public class ResourceMessage {
    private Long resourceId;


    public ResourceMessage() {
    }

    public ResourceMessage(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getResourceId() {
        return resourceId;
    }


    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}
