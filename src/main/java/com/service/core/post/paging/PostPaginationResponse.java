package com.service.core.post.paging;

import lombok.Getter;

@Getter
public class PostPaginationResponse<T> {
    private T postDto;
    private PostPagination postPagination;

    public PostPaginationResponse(T postDto, PostPagination postPagination) {
        this.postDto = postDto;
        this.postPagination = postPagination;
    }

}
