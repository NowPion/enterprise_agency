package tech.wetech.admin3.miniapp.dto;

import java.util.List;

/**
 * 提交反馈DTO
 */
public class SubmitFeedbackDTO {
    
    private String type; // suggestion-建议 complaint-投诉 question-问题 other-其他
    private String content;
    private List<String> images;
    private String contact;

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
