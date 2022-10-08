package com.service.core.comment.controller;

import com.service.core.comment.dto.CommentPagingResponseDto;
import com.service.core.comment.dto.CommentRegisterDto;
import com.service.core.comment.model.CommentInput;
import com.service.core.comment.paging.CommentSearchPagingDto;
import com.service.core.comment.service.CommentService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.CommentManageException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentRestController {
    private final CommentService commentService;

    @Operation(summary = "특정 포스트 댓글 반환", description = "특정 포스트 댓글 반환 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 포스트 댓글 반환 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 특정 포스트 댓글 반환 실패")
    })
    @GetMapping("/{postId}/{blogId}")
    public ResponseEntity<CommentPagingResponseDto> findTotalCommentsByPostId(@PathVariable Long postId, @PathVariable Long blogId, @ModelAttribute CommentSearchPagingDto commentSearchPagingDto, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(CommentPagingResponseDto.success(commentService.findTotalPaginationComment(postId, blogId, commentSearchPagingDto, principal)));
        } catch (Exception exception) {
            log.error("[freeblog-findTotalCommentsByPostId] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommentPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "댓글 업로드", description = "댓글 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 댓글 업로드 실패")
    })
    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<CommentRegisterDto> registerComment(@Valid CommentInput commentInput, BindingResult bindingResult, Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
            return ResponseEntity.status(HttpStatus.OK).body(CommentRegisterDto.success(commentService.registerComment(commentInput, principal), "댓글 작성에 성공하였습니다."));
        } catch (Exception exception) {
            log.error("[freeblog-registerComment] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommentRegisterDto.fail(exception));
        }
    }

    @Operation(summary = "댓글 썸네일 이미지 업로드", description = "댓글 썸네일 이미지 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 썸네일 이미지 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 댓글 썸네일 이미지 업로드 실패")
    })
    @ResponseBody
    @PostMapping("/upload/comment-thumbnail-image")
    public ResponseEntity<String> uploadCommentThumbnailImage(@RequestParam("post_comment_image_file_input") MultipartFile multipartFile) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(commentService.uploadAwsSCommentThumbnailImage(multipartFile));
        } catch (Exception exception) {
            log.error("[freeblog-uploadCommentThumbnailImage] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("댓글 썸네일 이미지 업로드에 실패하였습니다. %s", exception.getMessage()));
        }
    }
}