// 메뉴바 선택 시 해당 콘텐츠 표시
document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll('.content').forEach(function (content) {
        content.style.display = 'none'; // 초기에는 모든 콘텐츠 숨김
    });

    document.getElementById('menu1').addEventListener('click', function () {
        showContent(1);
    });
    document.getElementById('menu2').addEventListener('click', function () {
        showContent(2);
    });
    document.getElementById('menu3').addEventListener('click', function () {
        showContent(3);
    });

    function showContent(menuNumber) {
        // 모든 콘텐츠 숨기기
        document.querySelectorAll('.content').forEach(function (content) {
            content.style.display = 'none';
        });

        // 선택된 메뉴에 해당하는 콘텐츠만 표시
        document.getElementById('content' + menuNumber).style.display = 'block';

        // 모든 메뉴에서 active 클래스 제거
        document.querySelectorAll('.ani-navbar-menu').forEach(function (menu) {
            menu.classList.remove('active');
        });

        // 클릭한 메뉴에 active 클래스 추가
        document.getElementById('menu' + menuNumber).classList.add('active');
    }
});

const deleteBtn = document.querySelector('.delete-selected-btn');
function deleteUser() {
    const checkboxes = document.querySelectorAll('.checklist1 tbody input[type="checkbox"]:checked');
    const userIds = Array.from(checkboxes).map(checkbox => {
        return checkbox.closest('tr').querySelector('td').textContent;
    });
    if(userIds.length === 0) {
        alert("사용자를 선택하세요.");
        return;
    }
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    fetch(`/admin/drop-user`, {
        method: 'DELETE',
        headers: {
            'X-CSRF-TOKEN': csrfToken,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({userIds: userIds})
    }).then(response => {
        if(!response.ok) {
            throw new Error('요청 실패');
        }
        return response.json();
    }).then(data => {
        console.log('삭제 성공 : ', data);
    }).catch(error => {
        console.error('오류 발생 : ', error);
    });
}
deleteBtn.onclick = deleteUser;

document.addEventListener("DOMContentLoaded", function () {
    const promoteBtn = document.querySelectorAll(".promote-btn");
    promoteBtn.forEach(button => {
        button.addEventListener("click", function () {
            const userRow = button.closest("tr");
            const userId = userRow.querySelector("td").textContent.trim();
            if(!userId) {
                alert("유저가 없습니다.");
                return;
            }
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            fetch("/admin/update-user", {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": csrfToken
                },
                body: JSON.stringify({userId: userId})
            }).then(response => {
                if(!response.ok) {
                    return response.text().then(text => {throw new Error(text);});
                }
                return response.json();
            }).then(data => {
                alert("승격 완료");
                console.log("승격 완료 : ", data);
                userRow.style.backgroundColor = "#d4edda";
            }).catch(error => {
                console.error("오류 발생 : ", error);
                alert("실패했습니다 : " + error.message);
            })
        })
    })
})

document.addEventListener("DOMContentLoaded", function() {
    const deleteBookBtn = document.querySelectorAll(".book-delete-btn");
    deleteBookBtn.forEach(button => {
        button.addEventListener("click", function() {
            const bookRow = button.closest("tr");
            const bookIsbn = bookRow.querySelector("td:nth-child(3)").textContent.trim();
            if(!bookIsbn) {
                alert("책을 조회할 수 없습니다.");
                return;
            }
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            fetch("/admin/book/delete", {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": csrfToken
                },
                body: JSON.stringify({bookIsbn: bookIsbn})
            }).then(response => {
                if(!response.ok) {
                    return response.text().then(text => {throw new Error(text)});
                }
                return response.json();
            }).then(data => {
                alert("삭제 완료");
                console.log("삭제 완료 : ", data);
                bookRow.style.backgroundColor = "#d4edda";
            }).catch(error => {
                console.error("오류 발생 : ", error);
                alert("실패했습니다 : " + error.message);
            })
        })
    })
})

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".comment-trigger").forEach(item => {
        item.addEventListener("click", function () {
            let replyBox = this.closest("td").querySelector(".reply-box");
            if (replyBox.querySelector(".reply-input")) {
                replyBox.innerHTML = "";
                return;
            }
            let input = document.createElement("input");
            input.type = "text";
            input.classList.add("reply-input");
            input.placeholder = "답변을 입력하세요...";
            let submitBtn = document.createElement("button");
            submitBtn.textContent = "전송";
            submitBtn.classList.add("submit-reply");
            replyBox.innerHTML = "";
            replyBox.appendChild(input);
            replyBox.appendChild(submitBtn);
            input.focus();
            submitBtn.addEventListener("click", function () {
                let answerText = input.value.trim();
                if (!answerText) {
                    alert("답변을 입력하세요.");
                    return;
                }
                let complainNo = item.closest("tr").getAttribute("data-complain-no");
                if (!complainNo) {
                    alert("잘못된 데이터입니다.");
                    return;
                }
                let csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
                fetch("/admin/update/answer", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "X-CSRF-TOKEN": csrfToken // CSRF 토큰 추가
                    },
                    body: JSON.stringify({
                        complainNo: complainNo,
                        answer: answerText
                    })
                })
                    .then(response => response.text())
                    .then(message => {
                        alert(message);  // 서버 응답 메시지 표시
                        replyBox.innerHTML = `<p class="reply-comment">${answerText}</p>`; // 입력창 대신 답변 표시
                    })
                    .catch(error => {
                        alert("서버 오류 발생: " + error);
                    });
            });
        });
    });
    document.addEventListener("click", function (event) {
        if (!event.target.closest(".comment-trigger") && !event.target.closest(".reply-box")) {
            document.querySelectorAll(".reply-box").forEach(box => box.innerHTML = "");
        }
    });
});


