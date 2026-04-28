package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 小程序用户
 */
@Entity
@Table(name = "t_user")
@Comment("小程序用户表")
public class MiniappUser extends BaseEntity {

    @Column(nullable = false, unique = true, length = 64)
    @Comment("微信openid")
    private String openid;

    @Column(length = 64)
    @Comment("微信unionid")
    private String unionid;

    @Column(length = 64)
    @Comment("昵称")
    private String nickname = "微信用户";

    @Column(length = 512)
    @Comment("头像URL")
    private String avatar = "";

    @Column(length = 20)
    @Comment("手机号")
    private String phone;

    @Comment("性别 0-未知 1-男 2-女")
    private Integer gender = 0;

    @Comment("状态 0-禁用 1-正常")
    private Integer status = 1;

    @Comment("是否拉黑 0-否 1-是")
    private Integer blocked = 0;

    @Column(name = "block_reason", length = 255)
    @Comment("拉黑原因")
    private String blockReason;

    @Column(name = "block_time")
    @Comment("拉黑时间")
    private LocalDateTime blockTime;

    @Column(name = "last_login_time")
    @Comment("最后登录时间")
    private LocalDateTime lastLoginTime;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }
    public String getUnionid() { return unionid; }
    public void setUnionid(String unionid) { this.unionid = unionid; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getBlocked() { return blocked; }
    public void setBlocked(Integer blocked) { this.blocked = blocked; }
    public String getBlockReason() { return blockReason; }
    public void setBlockReason(String blockReason) { this.blockReason = blockReason; }
    public LocalDateTime getBlockTime() { return blockTime; }
    public void setBlockTime(LocalDateTime blockTime) { this.blockTime = blockTime; }
    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
