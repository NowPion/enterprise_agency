package tech.wetech.admin3.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户统计VO
 */
@Schema(description = "用户统计")
public class UserStatsVO {

    @Schema(description = "用户ID")
    private Long userId;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
