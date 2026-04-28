package tech.wetech.admin3.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 分页VO
 */
@Schema(description = "分页结果")
public class PageVO<T> {

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "是否有更多")
    private Boolean hasMore;

    public PageVO() {}

    public PageVO(List<T> list, Long total) {
        this.list = list;
        this.total = total;
        this.hasMore = false;
    }

    public PageVO(List<T> list, Long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.hasMore = (long) page * pageSize < total;
    }

    // Getters and Setters
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Boolean getHasMore() { return hasMore; }
    public void setHasMore(Boolean hasMore) { this.hasMore = hasMore; }
}
