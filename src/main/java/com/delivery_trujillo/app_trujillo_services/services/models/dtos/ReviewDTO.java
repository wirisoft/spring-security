package com.delivery_trujillo.app_trujillo_services.services.models.dtos;

public class ReviewDTO {
    
    private Long orderId;
    private Integer restaurantRating;
    private Integer deliveryRating;
    private String comment;
    private String photoUrl;
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Integer getRestaurantRating() {
        return restaurantRating;
    }
    
    public void setRestaurantRating(Integer restaurantRating) {
        this.restaurantRating = restaurantRating;
    }
    
    public Integer getDeliveryRating() {
        return deliveryRating;
    }
    
    public void setDeliveryRating(Integer deliveryRating) {
        this.deliveryRating = deliveryRating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

