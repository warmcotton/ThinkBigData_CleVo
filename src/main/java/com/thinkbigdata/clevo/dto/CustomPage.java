package com.thinkbigdata.clevo.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
@Getter @Setter
public class CustomPage<T> {
    private List<T> content;
    private Pageable pageable;
    private boolean isLast;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
    private Sort sort;
    private boolean isFirst;
    private int numberOfElements;
    private boolean isEmpty;

    public CustomPage(List<T> content, Pageable pageable, boolean last, long totalElements, int totalPages, int size, int number, Sort sort, boolean first, int numberOfElements, boolean empty) {
        this.content = content;
        this.pageable = pageable;
        this.isLast = last;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.size = size;
        this.number = number;
        this.sort = sort;
        this.isFirst = first;
        this.numberOfElements = numberOfElements;
        this.isEmpty = empty;
    }
}
