const discussionContainer = document.querySelector('.all-discussion-container');
const discussionForm = document.forms.namedItem('discussionId');
const discussionId = discussionForm?.id;

let abortController = new AbortController(); // 기존 요청 취소를 위한 컨트롤러

const csrfMetaTag = document.querySelector('meta[name="_csrf"]');
const csrfHeaderMetaTag = document.querySelector('meta[name="_csrf_header"]');

const csrfToken = csrfMetaTag ? csrfMetaTag.getAttribute('content') : null;


document.addEventListener("DOMContentLoaded", function () {
    initializeDiscussionForm();
    initializeVoteButtons();
});

/// ✅ 댓글 작성 폼 이벤트 리스너 등록 함수 (중복 방지)
function initializeDiscussionForm() {
    const form = document.querySelector(".my-opinion-form");

    if (!form) {
        console.error("🚨 토론 댓글 작성 폼을 찾을 수 없습니다.");
        return;
    }

    console.log("✅ 토론 댓글 작성 폼을 찾았습니다!");

    // ✅ 기존 이벤트 리스너 제거 후 다시 추가 (중복 실행 방지)
    form.removeEventListener("submit", handleDiscussionSubmit);
    form.addEventListener("submit", handleDiscussionSubmit);
}

/// ✅ 댓글 작성 이벤트 핸들러 (중복 방지)
function handleDiscussionSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;

    if (!csrfToken) {
        console.error("🚨 CSRF 토큰이 없습니다.");
        alert("보안 문제가 발생했습니다. 페이지를 새로고침 해주세요.");
        return;
    }

    const textArea = form.querySelector("textarea");
    const commentContent = textArea.value.trim();

    if (commentContent === "") {
        alert("댓글 내용을 입력해주세요.");
        return;
    }

    fetch(`/discussion/${discussionId}/comment/add`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": csrfToken
        },
        body: JSON.stringify({
            discussionId: parseInt(discussionId),
            content: commentContent
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`❌ 서버 응답 오류: ${response.status}`);
            }
            return response.text();
        })
        .then(responseText => {
            textArea.value = "";
            load_comment(null, `/discussion/${discussionId}/comment`);
        })
        .catch(error => console.error("❌ Error:", error));
}

/// ✅ 찬성(👍) & 반대(👎) 버튼 클릭 이벤트 등록
function initializeVoteButtons() {
    document.querySelectorAll(".agree, .disagree").forEach(button => {
        button.removeEventListener("click", handleVoteClick);
        button.addEventListener("click", handleVoteClick);
    });
}

/// ✅ 찬성/반대 이벤트 핸들러 (중복 방지)
function handleVoteClick(event) {
    event.preventDefault();

    const button = event.currentTarget;
    const commentElement = button.closest(".one-discussion");
    const commentId = commentElement ? commentElement.getAttribute("data-comment-id") : null;

    if (!commentId) {
        console.error("🚨 commentId를 찾을 수 없습니다!");
        return;
    }

    // ✅ 이미 투표한 경우 차단
    if (button.getAttribute("data-voted") === "true") {
        alert("이미 투표한 댓글입니다.");
        return;
    }

    if (button.classList.contains("agree")) {
        voteLikeComment(commentId, button);
    } else {
        voteUnlikeComment(commentId, button);
    }
}

/// ✅ 좋아요(👍) 요청 보내기
/// ✅ 좋아요(👍) 요청 보내기
function voteLikeComment(commentId, button) {
    fetch(`/discussion/${discussionId}/comment/${commentId}/like`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": csrfToken
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log("좋아요 업데이트 성공");
            // UI 업데이트 로직
            updateVoteUI(commentId, data.like, data.unlike, true);
        })
        .catch(error => console.error("좋아요 요청 실패:", error));
}

/// ✅ 싫어요(👎) 요청 보내기
function voteUnlikeComment(commentId, button) {
    fetch(`/discussion/${discussionId}/comment/${commentId}/unlike`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": csrfToken
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log("싫어요 업데이트 성공");
            // UI 업데이트 로직
            updateVoteUI(commentId, data.like, data.unlike, false);
        })
        .catch(error => console.error("싫어요 요청 실패:", error));
}

/// ✅ UI 업데이트 (서버 값 반영)
function updateVoteUI(commentId, likeCount, unlikeCount, isLike) {
    const commentElement = document.querySelector(`[data-comment-id="${commentId}"]`);
    const likeButton = commentElement.querySelector(".agree");
    const unlikeButton = commentElement.querySelector(".disagree");

    const likeCountElement = likeButton.querySelector(".like-count");
    const unlikeCountElement = unlikeButton.querySelector(".unlike-count");

    // 서버에서 받은 좋아요, 싫어요 개수로 UI 업데이트
    likeCountElement.textContent = likeCount;
    unlikeCountElement.textContent = unlikeCount;

    if (isLike) {
        likeButton.setAttribute("data-voted", "true");
        unlikeButton.setAttribute("data-voted", "false");

        likeButton.disabled = true;  // 좋아요 버튼 비활성화
        unlikeButton.disabled = false; // 싫어요 버튼 활성화
    } else {
        unlikeButton.setAttribute("data-voted", "true");
        likeButton.setAttribute("data-voted", "false");

        unlikeButton.disabled = true; // 싫어요 버튼 비활성화
        likeButton.disabled = false; // 좋아요 버튼 활성화
    }
}


/// ✅ 댓글 목록 불러오기
load_comment(null, `/discussion/${discussionId}/comment`);
function load_comment(event, url) {
    if (event !== null) {
        event.preventDefault();
    }

    fetch(url)
        .then(response => response.text())  // ✅ 서버에서 Fragment HTML 받아오기
        .then(commentTemplate => {
            const tempDiv = document.createElement("div");
            tempDiv.innerHTML = commentTemplate.trim();  // ✅ 공백 제거

            const newContainer = tempDiv.querySelector(".all-discussion-container");

            if (!newContainer) {
                console.error("🚨 불러온 템플릿에서 '.all-discussion-container'를 찾을 수 없습니다. 반환된 HTML:");
                return;
            }

            // ✅ 기존 댓글 컨테이너 교체
            const existingContainer = document.querySelector(".all-discussion-container");
            if (existingContainer) {
                existingContainer.replaceWith(newContainer);
            } else {
                document.querySelector("main").appendChild(newContainer);
            }

            initializeDiscussionForm();
            initializeVoteButtons();
        })
        .catch(error => console.error("❌ 댓글 불러오기 실패:", error));
}





