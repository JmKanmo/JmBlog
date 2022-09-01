package com.service.core.post.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class BlogPostInput {
    @NotEmpty(message = "게시글 제목이 비어있습니다.")
    @NotBlank(message = "게시글 제목은 공백만 올 수 없습니다")
    @Size(max = 128, message = "게시글 제목은 최대 128글자 까지 작성 가능합니다.")
    private final String title;

    @NotEmpty(message = "게시글 본문이 비어있습니다.")
    @NotBlank(message = "게시글 본문은 공백만 올 수 없습니다")
    private final String contents;

    private final Long category;
}