package com.service.core.post.controller;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.post.dto.PostResponseDto;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.service.PostService;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserHeaderDto;
import com.service.core.user.service.UserService;
import com.service.util.ConstUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Tag(name = "포스트", description = "포스트 관련 API")
@RequiredArgsConstructor
@Controller
@RequestMapping("/post")
@Slf4j
public class PostController {
    private final PostService postService;
    private final UserService userService;

    @Operation(summary = "블로그 포스트 작성 페이지 반환", description = "블로그 포스트 작성 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 포스트 작성 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "블로그 포스트 작성 페이지 반환 실패")
    })
    @GetMapping("/write/{userId}")
    public String postWritePage(@PathVariable String userId, Model model, Principal principal) {
        UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());

        if (!userId.equals(userHeaderDto.getId())) {
            throw new UserAuthException(ServiceExceptionMessage.MISMATCH_ID);
        }

        if (principal != null) {
            model.addAttribute("user_header", userHeaderDto);
        }
        model.addAttribute("blogPostInput", BlogPostInput.builder().build());
        return "post/post-write";
    }

    @Operation(summary = "블로그 포스트 작성 작업", description = "블로그 포스트 작성 페이지 작업 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 포스트 작성 작업 성공"),
            @ApiResponse(responseCode = "500", description = "DB 연결 오류, SQL 쿼리 수행 실패 등의 이유로 블로그 포스트 작성 작업 실패")
    })
    @PostMapping("/write/{userId}")
    public String postWrite(@Valid BlogPostInput blogPostInput, BindingResult bindingResult, Model model, Principal principal) {
        // TODO 사용자 정보 기반으로 포스트 데이터 저장
        return "post/post-write-complete";
    }

    @Operation(summary = "해당 블로그의 전체 포스트 반환", description = "해당 블로그의 전체 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 전체 포스트 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 반환 실패")
    })
    @ResponseBody
    @GetMapping("/all/{blogId}")
    public ResponseEntity<PostResponseDto> findTotalPostByBlogId(@PathVariable Long blogId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostResponseDto.success(postService.findTotalPost(blogId, ConstUtil.TOTAL_POST)));
        } catch (Exception exception) {
            log.error("[freelog-findTotalPostByBlogId] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostResponseDto.fail(exception));
        }
    }
}