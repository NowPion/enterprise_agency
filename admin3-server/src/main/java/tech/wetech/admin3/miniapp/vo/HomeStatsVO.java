package tech.wetech.admin3.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 首页统计VO
 */
@Schema(description = "首页统计")
public class HomeStatsVO {

    @Schema(description = "今日活跃用户数")
    private Long todayUsers;

    @Schema(description = "总用户数")
    private Long totalUsers;

    // Getters and Setters
    public Long getTodayUsers() { return todayUsers; }
    public void setTodayUsers(Long todayUsers) { this.todayUsers = todayUsers; }
    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
}
