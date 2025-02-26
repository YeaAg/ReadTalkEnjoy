const discussionBtn = document.querySelector('.discussion-button')
const bookBtn = document.querySelector('.book-button');

const input = document.querySelector('.search-input');
const button = document.querySelector('.search-button');

document.addEventListener("DOMContentLoaded", () => {
    const input = document.querySelector('.search-input');
    const button = document.querySelector('.search-button');

    const getCookieValue = (name) => {
        const value = document.cookie.match('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)');
        return value ? value.pop() : '';
    };

    /**
     * 페이지네이션을 업데이트하는 함수
     */
    const updatePagination = (data) => {
        const paginationDiv = document.querySelector('.pagination');
        if (!paginationDiv) {
            console.warn("Pagination element not found.");
            return;
        }

        paginationDiv.innerHTML = ''; // 기존 페이지네이션 초기화

        if (data.totalPageCount > 1) {
            for (let i = data.startPage; i <= data.endPage; i++) {
                const pageLink = document.createElement('a');
                pageLink.href = `/discussion/category?page=${i}&size=${data.size}`;
                pageLink.textContent = i;

                if (i === data.page) {
                    pageLink.classList.add('active');
                }

                paginationDiv.appendChild(pageLink);
            }
        }
    };

    /**
     * 페이지네이션을 제거하는 함수
     */
    const removePagination = () => {
        const paginationDiv = document.querySelector('.pagination');
        if (paginationDiv) {
            paginationDiv.innerHTML = ''; // 페이지네이션 초기화
        } else {
            console.warn("Pagination element not found for removal.");
        }
    };

    /**
     * 검색 실행 함수
     */
    const executeSearch = () => {
        const inputValue = input.value.trim();

        if (!inputValue) {
            document.cookie = "searchKeyword=; Max-Age=0; path=/"; // 쿠키 삭제
            location.href = '/discussion/category';
            return;
        }

        document.cookie = `searchKeyword=${encodeURIComponent(inputValue)}; path=/`;

        fetch(`/discussion/category/search?bookName=${encodeURIComponent(inputValue)}`, {
            method: 'GET',
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`API 요청 실패: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                const resultDiv = document.querySelector('.all-book-discussion');
                const totalCountElement = document.getElementById('total-count');

                resultDiv.innerHTML = '';

                if (data.elements && data.elements.length > 0) {
                    totalCountElement.textContent = data.totalElementCount;

                    data.elements.forEach(discussion => {
                        const searchDiscussion = document.createElement('div');
                        searchDiscussion.className = 'one-book-discussion';
                        searchDiscussion.innerHTML = `
                            <div class="image-container">
                                <img src="${discussion.base64Image || '../../static/images/book_main.jpg'}" alt="메인이미지" />
                            </div>
                            <div class="discussion-title-recent">
                                <h2><a href="/discussion/${discussion.id}">${discussion.bookTitle}</a></h2>
                                <div class="discussion-title">
                                    <span>토론 주제:</span>
                                    <p>${discussion.topic}</p>
                                </div>
                                <div class="discussion-recent">
                                    <p>${discussion.recentComment || '최근 댓글 없음'}</p>
                                </div>
                                <div class="discussion-button-section">
                                    <button class="discussion-button" data-discussion-id="${discussion.id}">토론 참여하기</button>
                                    <button class="book-button" data-book-isbn="${discussion.bookIsbn}">책 보러가기</button>
                                </div>
                            </div>
                        `;
                        resultDiv.appendChild(searchDiscussion);
                    });

                    updatePagination(data);
                } else {
                    totalCountElement.textContent = 0;
                    resultDiv.innerHTML = '<p>검색 결과가 없습니다.</p>';
                    removePagination();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('검색 중 문제가 발생했습니다. 나중에 다시 시도해주세요.');
            });
    };

    // Enter 키 입력 시 검색 실행
    input.addEventListener('keypress', (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            executeSearch();
        }
    });

    // 검색 버튼 클릭 시 검색 실행
    button.addEventListener('click', executeSearch);

    // 🔹 쿠키에 검색 키워드가 있을 때만 초기 검색 실행
    const initialKeyword = getCookieValue('searchKeyword');
    if (initialKeyword) {
        input.value = decodeURIComponent(initialKeyword); // 검색 키워드를 입력창에 표시
        executeSearch(); // 초기 검색 실행
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const resultDiv = document.querySelector('.all-book-discussion'); // 동적 요소가 추가될 부모 컨테이너

    // 🔹 이벤트 위임: 부모 요소에 이벤트를 걸고 클릭된 요소가 버튼인지 확인
    resultDiv.addEventListener("click", (event) => {
        const target = event.target;

        // 🎯 토론 참여하기 버튼 클릭 시 이동
        if (target.classList.contains("discussion-button")) {
            const discussionId = target.dataset.discussionId;
            if (discussionId) {
                location.href = `/discussion/${discussionId}`;
            } else {
                alert("❌ 토론 ID를 찾을 수 없습니다.");
            }
        }

        // 🎯 책 보러가기 버튼 클릭 시 이동
        if (target.classList.contains("book-button")) {
            const bookIsbn = target.dataset.bookIsbn;
            if (bookIsbn) {
                location.href = `/book/${bookIsbn}`;
            } else {
                alert("❌ 책 ISBN을 찾을 수 없습니다.");
            }
        }
    });
});

document.addEventListener("DOMContentLoaded", () => {
    // 토론 참여하기 버튼 클릭 시 이동
    document.querySelectorAll(".discussion-button").forEach(button => {
        button.addEventListener("click", (event) => {
            const discussionId = event.currentTarget.dataset.discussionId;
            if (discussionId) {
                location.href = `/discussion/${discussionId}`;
            } else {
                alert("토론 ID를 찾을 수 없습니다.");
            }
        });
    });

    // 책 보러가기 버튼 클릭 시 이동
    document.querySelectorAll(".book-button").forEach(button => {
        button.addEventListener("click", (event) => {
            const bookIsbn = event.currentTarget.dataset.bookIsbn;
            if (bookIsbn) {
                location.href = `/book/${bookIsbn}`;
            } else {
                alert("책 ISBN을 찾을 수 없습니다.");
            }
        });
    });
});


/*******************************************/

// 보기설정
const viewSizeSelect = document.getElementById('view-size-select');
viewSizeSelect.onchange = () => {
    const searchParams = new URLSearchParams(location.search);
    searchParams.set('size', viewSizeSelect.value);
    location.href = `/discussion/category?${searchParams.toString()}`;
}