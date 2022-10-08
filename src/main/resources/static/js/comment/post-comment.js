class PostCommentController extends UtilController {
    constructor() {
        super();
        this.postCommentForm = document.getElementById("post_comment_form");
        this.postCommentImageButton = document.getElementById("post_comment_image_button");
        this.postCommentLockButton = document.getElementById("post_comment_lock_button");
        this.postCommentSubmitButton = document.getElementById("post_comment_submit_button");
        this.postCommentImageFileInput = document.getElementById("post_comment_image_file_input");
        this.postCommentImageForm = document.getElementById("post_comment_image_form");
        this.postCommentThumbnailImage = document.getElementById("post_comment_thumbnail_image");
        this.postCommentThumbnailImageBox = document.getElementById("post_comment_thumbnail_image_box");
        this.postCommentThumbnailImageDeleteButton = document.getElementById("post_comment_thumbnail_image_delete_button");
        this.postCommentSecretImage = document.getElementById("post_comment_secret_image");

        this.postCommentBlogId = document.getElementById("post_comment_blogId");
        this.postCommentPostId = document.getElementById("post_comment_postId");
        this.postCommentThumbnailImageValueInput = document.getElementById("post_comment_thumbnail_image_value_input");
        this.postCommentIsSecretInput = document.getElementById("post_comment_is_secret_input");
        this.commentIsAnonymous = document.getElementById("commentIsAnonymous");
        this.postCommentTextInput = document.getElementById("post_comment_text_input");
        this.commentUserNickname = document.getElementById("commentUserNickname");
        this.commentUserPassword = document.getElementById("commentUserPassword");

        this.currentTextCount = document.getElementById("current_text_count");

        this.postCommentList = document.getElementById("post_comment_list");
        this.commentPagination = document.getElementById("commentPagination");

        this.commentRecordSize = 20;
        this.commentPageSize = 10;
    }

    initEventListener() {
        this.postCommentImageButton.addEventListener("click", evt => {
            this.postCommentImageFileInput.click();
        });

        this.postCommentLockButton.addEventListener("click", evt => {
            const isCommentLock = this.postCommentIsSecretInput.checked;

            if (isCommentLock === true) {
                this.postCommentSecretImage.src = "../images/unlock.png";
                this.postCommentIsSecretInput.checked = false;
            } else {
                this.postCommentSecretImage.src = "../images/lock.png";
                this.postCommentIsSecretInput.checked = true;
            }
        });

        this.postCommentSubmitButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();

            if (this.checkCommentForm() === false) {
                this.showToastMessage("폼 입력 정보가 양식 조건에 유효하지 않습니다.");
                return;
            }

            xhr.open("POST", `/comment/register`);
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

            xhr.addEventListener("loadend", evt => {
                let status = evt.target.status;
                const responseValue = JSON.parse(evt.target.responseText);

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showToastMessage(responseValue["message"]);
                } else {
                    this.resetCommentForm();
                    const commentCount = responseValue["commentCount"];
                    this.#requestComment(`/comment/${this.postCommentPostId.value}/${this.postCommentBlogId.value}`, Math.ceil((commentCount / this.commentRecordSize)));
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('오류가 발생하여 댓글 등록에 실패하였습니다.');
            });
            xhr.send(new FormData(this.postCommentForm));
        });

        this.postCommentImageFileInput.addEventListener("change", evt => {
            const imgFile = evt.target.files;

            if (this.checkProfileThumbnailFile(imgFile)) {
                const fileReader = new FileReader();

                fileReader.onload = (event) => {
                    const xhr = new XMLHttpRequest();

                    xhr.open("POST", `/comment/upload/comment-thumbnail-image`);
                    xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

                    xhr.addEventListener("loadend", event => {
                        let status = event.target.status;
                        const responseValue = event.target.responseText;

                        if ((status >= 400 && status <= 500) || (status > 500)) {
                            this.showToastMessage(responseValue);
                            this.removeCommentImage();
                        } else {
                            this.postCommentThumbnailImageBox.style.display = "block";
                            this.postCommentThumbnailImage.src = responseValue;
                            this.postCommentThumbnailImageValueInput.value = responseValue;
                        }
                    });

                    xhr.addEventListener("error", event => {
                        this.showToastMessage('오류가 발생하여 댓글 이미지 전송에 실패하였습니다.');
                        this.removeCommentImage();
                    });
                    xhr.send(new FormData(this.postCommentImageForm));
                }
                fileReader.readAsDataURL(imgFile[0]);
            } else {
                this.removeCommentImage();
            }
        });

        this.postCommentThumbnailImageDeleteButton.addEventListener("click", evt => {
            if (confirm("해당 댓글 이미지를 삭제하겠습니까?")) {
                this.removeCommentImage();
            }
        });

        this.postCommentTextInput.addEventListener("input", evt => {
            this.setTextCount(evt.target);
        });

        this.commentPagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.#requestComment(url, page);
                }
            }
        });

        this.postCommentList.addEventListener("click", evt => {
            const commentUpdateButton = evt.target.closest(".post_comment_update_button");
            const commentDeleteButton = evt.target.closest(".post_comment_delete_button");
            const commentReplyButton = evt.target.closest(".post_comment_reply_button");
            // TODO 신고 버튼 추후에 추가

            if (commentUpdateButton != null) {
                this.showToastMessage("수정 버튼 클릭");
                const commentId = commentUpdateButton.closest(".post_comment_util_block").getAttribute("commentId");
                this.openPopUp(1070, 360, `/comment/update/${commentId}`, 'popup')
            } else if (commentDeleteButton != null) {
                this.showToastMessage("삭제 버튼 클릭");
                const commentId = commentDeleteButton.closest(".post_comment_util_block").getAttribute("commentId");
            } else if (commentReplyButton != null) {
                this.showToastMessage("답글 버튼 클릭");
                const commentId = commentReplyButton.closest(".post_comment_util_block").getAttribute("commentId");
            }
        });
    }

    requestComment() {
        this.#requestComment(`/comment/${this.postCommentPostId.value}/${this.postCommentBlogId.value}`);
    }

    setTextCount(commentTextArea) {
        const textLength = commentTextArea.value.length;
        this.currentTextCount.textContent = textLength;
    }

    removeCommentImage() {
        let fileBuffer = new DataTransfer();
        this.postCommentImageFileInput.files = fileBuffer.files; // <-- according to your file input reference
        this.postCommentThumbnailImage.src = "";
        this.postCommentThumbnailImageBox.style.display = "none";
        this.postCommentThumbnailImageValueInput.value = null;
    }

    resetCommentForm() {
        this.removeCommentImage();
        this.postCommentTextInput.value = "";
        this.currentTextCount.textContent = this.postCommentTextInput.value.length;
        this.commentUserNickname.value = "";
        this.commentUserPassword.value = "";
        this.postCommentIsSecretInput.checked = false;
        this.postCommentSecretImage.src = "../images/unlock.png";
        this.commentIsAnonymous.checked = false;
    }

    checkCommentForm() {
        if (this.commentIsAnonymous.checked === true) {
            if ((!this.commentUserNickname.value || this.commentUserNickname.value.match(/\s/g))
                || (!this.commentUserPassword || this.commentUserPassword.value.match(/\s/g))) {
                return false;
            }
            return true;
        }
    }

    #handleTemplateList(responseValue) {
        const postCommentTemplate = document.getElementById("post-comment-template").innerHTML;
        const postCommentTemplateObject = Handlebars.compile(postCommentTemplate);
        const postCommentTemplateHTML = postCommentTemplateObject(responseValue["commentPaginationResponse"]["commentSummaryDto"]);
        this.postCommentList.innerHTML = postCommentTemplateHTML;
    }

    #clearPagination() {
        this.commentPagination.innerHTML = ``;
    }

    #handlePagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.commentPagination.innerHTML = '';
            return;
        }
        this.commentPagination.innerHTML = this.drawPagination(pagination, queryParam, url);
    }

    #requestComment(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.commentRecordSize, this.commentPageSize);

        xhr.open("GET", url + '?' + queryParam.toString());


        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleTemplateList(responseValue);
                this.#clearPagination();
                this.#handlePagination(responseValue["commentPaginationResponse"]["commentPagination"], queryParam, url);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("댓글 정보를 불러오는데 실패하였습니다.");
        });

        xhr.send();
    }
}