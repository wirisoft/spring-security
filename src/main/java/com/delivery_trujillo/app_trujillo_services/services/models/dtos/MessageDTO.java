package com.delivery_trujillo.app_trujillo_services.services.models.dtos;

public class MessageDTO {
    
    private String content;
    private String attachmentUrl;
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getAttachmentUrl() {
        return attachmentUrl;
    }
    
    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
}

