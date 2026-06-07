package backend;

import java.sql.Date;
import java.sql.Timestamp;

public class Item {
    private int itemId;
    private String trackingNumber;
    private int userId;
    private String itemName;
    private String description;
    private String category;
    private String location;
    private Date dateLost;
    private Date dateFound;
    private String status;
    private Integer foundBy;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    private String ownerUsername;
    private String finderUsername;
    
    private String reporterEmail;
    private String reporterPhone;

    public Item() {
        this.isActive = true;
        this.status = "LOST";
    }

    public Item(String trackingNumber, int userId, String itemName, String description, 
                String category, String location, Date dateLost, String status) {
        this();
        this.trackingNumber = trackingNumber;
        this.userId = userId;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.location = location;
        this.dateLost = dateLost;
        this.status = status;
    }

    public Item(int itemId, String trackingNumber, String itemName, String description, 
                String category, String location, String status, String ownerUsername) {
        this.itemId = itemId;
        this.trackingNumber = trackingNumber;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.location = location;
        this.status = status;
        this.ownerUsername = ownerUsername;
        this.isActive = true;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateLost() {
        return dateLost;
    }

    public void setDateLost(Date dateLost) {
        this.dateLost = dateLost;
    }

    public Date getDateFound() {
        return dateFound;
    }

    public void setDateFound(Date dateFound) {
        this.dateFound = dateFound;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFoundBy() {
        return foundBy;
    }

    public void setFoundBy(Integer foundBy) {
        this.foundBy = foundBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getFinderUsername() {
        return finderUsername;
    }

    public void setFinderUsername(String finderUsername) {
        this.finderUsername = finderUsername;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public String getReporterPhone() {
        return reporterPhone;
    }

    public void setReporterPhone(String reporterPhone) {
        this.reporterPhone = reporterPhone;
    }

    public boolean isLost() {
        return "LOST".equalsIgnoreCase(this.status);
    }

    public boolean isFound() {
        return "FOUND".equalsIgnoreCase(this.status);
    }

    public boolean isClaimed() {
        return "CLAIMED".equalsIgnoreCase(this.status);
    }

    public boolean isReturned() {
        return "RETURNED".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", dateLost=" + dateLost +
                ", dateFound=" + dateFound +
                ", ownerUsername='" + ownerUsername + '\'' +
                '}';
    }

    public String getFormattedDate() {
        if (dateLost != null) {
            return dateLost.toString();
        } else if (dateFound != null) {
            return dateFound.toString();
        }
        return "N/A";
    }
}
