package com.wangyi.component.web.util;

import java.util.List;
import java.util.Map;

public class PageData<T> {

    /**
     * 页码
     */
    private Long page;

    /**
     * 每页条数
     */
    private Long size;

    /**
     * 总条数
     */
    private Long total;

    /**
     * 每页数据
     */
    private List<T> list;

    /**
     * 扩展数据
     */
    private Map<String, Object> ext;

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }
}
