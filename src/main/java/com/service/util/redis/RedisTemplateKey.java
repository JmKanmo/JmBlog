package com.service.util.redis;

public class RedisTemplateKey {
    // 댓글 관련 (좋아요)
    public static final String COMMENT_LIKE = "comment-like";

    // 게시글 관련 (좋아요)
    public static final String POST_LIKE = "post-like:%d";
    public static final String LIKE_POST = "like-posts:%s";

    //    public static final String POST_LIKE_USERS = "post-like-users";
    public static final String POST_VIEWS = "post-views";

    // 방문자 수 관련
    public static final String BLOG_DAY_VIEWS = "blog-day-views";
    public static final String BLOG_TOTAL_VIEWS = "blog-total-views";
}
