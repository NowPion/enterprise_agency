package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 资源
 *
 * @author cjbi
 */
@Entity
@Comment("权限资源表")
public class Resource extends BaseEntity {

  @Comment("资源名称")
  private String name;

  @Comment("类型 0-菜单 1-按钮")
  private Type type;

  @Comment("权限标识")
  private String permission;

  @ManyToOne(fetch = FetchType.LAZY)
  private Resource parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
  private Set<Resource> children = new LinkedHashSet<>();

  @Comment("所有父级ID路径")
  private String parentIds;

  @Comment("图标")
  private String icon;

  @Comment("路由地址")
  private String url;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinTable(name = "role_resource",
    joinColumns = @JoinColumn(name = "resource_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private Set<Role> roles = new LinkedHashSet<>();

  public enum Type {
    MENU, BUTTON
  }

  public String getIcon() {
    return icon;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Resource getParent() {
    return parent;
  }

  public void setParent(Resource parent) {
    this.parent = parent;
  }

  public Set<Resource> getChildren() {
    return children;
  }

  public String getParentIds() {
    return parentIds;
  }

  public void setParentIds(String parentIds) {
    this.parentIds = parentIds;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setChildren(Set<Resource> children) {
    this.children = children;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}
