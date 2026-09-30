package com.example.demo.common;


import java.util.List;
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private  Long total;
    private long totalPages;

    public PageResponse(){

    }

    public PageResponse(
            List<T> items,
            int page,
            int size,
            long total,
            long totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = totalPages;
    }

    public static <T> PageResponse<T> of (
            List<T> items,
            int page,
            int size,
            long total){
        long totalPages=total==0?0:(total+size-1)/size;

        return new PageResponse<>(items,page,size,total,totalPages) ;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public long getTotalPages() {
        return totalPages;
    }





}
