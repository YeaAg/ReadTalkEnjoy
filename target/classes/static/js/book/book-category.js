const input = document.querySelector('.search-input');
const button = document.querySelector('.search-button');

// CSRF 토큰 추출
const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;

/******************************************/
// 📌 이벤트 위임 방식으로 찜하기 이벤트 추가
function addToWishlist (button) {
    const book = {
        isbn: button.getAttribute('data-isbn'),
        title: button.getAttribute('data-title'),
    };

    if (!book.isbn) {
        alert('책 정보를 가져올 수 없습니다.');
        return;
    }

    fetch('/user/wishlist/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken, // CSRF 토큰 추가
        },
        body: JSON.stringify(book), // 요청 본문에 book 데이터를 JSON으로 포함
    })
        .then(response => {
            if (!response.ok) {
                if (response.status === 401) {
                    throw new Error('로그인이 필요합니다. 로그인 후 다시 시도해주세요.');
                }
                throw new Error('찜하기 요청 실패');
            }
            return response.json();
        })
        .then(() => {
            if (confirm('상품이 등록되었습니다. 카트로 이동하시겠습니까?')) {
                location.href = '/user/wishlist';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message || '요청 처리 중 문제가 발생했습니다.');
        });
}

// 📌 찜하기 버튼을 눌렀을 때 (이벤트 위임 방식)
document.addEventListener('click', function(event) {
    if (event.target.classList.contains('book-heart-button')) {
        if (confirm('찜하시겠습니까?')) {
            addToWishlist(event.target);  // 클릭된 버튼을 전달
        }
    }
});

/*****************************/
// 📌 검색하기 (Enter 키 입력 시 실행)
input.addEventListener("keypress", (event) => {
    if (event.key === 'Enter') {
        event.preventDefault(); // 기본 폼 제출 방지
        executeSearch(); // 검색 함수 호출
    }
});

// 📌 검색 버튼 클릭 시 실행
button.addEventListener("click", (event) => {
    event.preventDefault(); // 기본 동작 방지
    executeSearch(); // 검색 함수 호출
});

const executeSearch = () => {
    const input = document.getElementById('search-input');
    const inputValue = input.value.trim();

    if (!inputValue) {
        // 검색어가 없으면 목록 페이지로 이동
        document.cookie = "searchKeyword=; Max-Age=0; path=/"; // 쿠키 삭제
        location.href = '/book/book-category';
        return;
    }

    // 검색 키워드를 쿠키에 저장
    document.cookie = `searchKeyword=${encodeURIComponent(inputValue)}; path=/`;

    // 현재 URL에서 기존 bookName 제거 후 새 검색어 적용
    const url = new URL(window.location.href);
    url.searchParams.delete('bookName'); // 기존 bookName 제거
    url.searchParams.set('bookName', inputValue); // 새 검색어 추가
    history.replaceState(null, '', url.toString()); // URL 업데이트

    // bookName 파라미터를 포함하여 검색 요청
    fetch(`/book/book-category/search?bookName=${encodeURIComponent(inputValue)}`, {
        method: 'GET',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const resultDiv = document.querySelector('.all-book');
            const paginationDiv = document.querySelector('.pagination');
            const totalCountElement = document.getElementById('total-count');

            // 검색 결과 렌더링
            resultDiv.innerHTML = '';
            paginationDiv.innerHTML = ''; // 페이지네이션 초기화

            if (data.elements && data.elements.length > 0) {
                totalCountElement.textContent = data.totalElementCount; // 총 개수 업데이트
                data.elements.forEach(book => {
                    const searchBook = document.createElement('div');
                    searchBook.className = 'one-book';
                    searchBook.innerHTML = `
                        <div class="image-container">
                            <img src="${book.base64Image || '../../static/images/book_main.jpg'}" alt="${book.title}" />
                        </div>
                        <div class="book-info">
                            <h2>
                                <a href="/book/${book.isbn}">${book.title}</a>
                            </h2>
                            <div class="author-publisher">
                                <span class="author">${book.author}</span>
                                <span>/</span>
                                <span class="publisher">${book.publisher}</span>
                            </div>
                            <input type="hidden" class="book-price-hidden" value="${book.price}">
                            <div class="rent-available">
                                <span>대출가능여부: </span>
                                <span class="rent-status">${book.copiesAvailable > 0 ? '가능' : '불가'}</span>
                            </div>
                            <div class="plot">
                                <p>${book.detail}</p>
                            </div>
                            <div class="rent-button-section">
                                <button class="book-heart-button"
                                    data-isbn="${book.isbn}"
                                    data-title="${book.title}">
                                    찜하기
                                </button>
                                <button class="book-rent-button"
                                    data-isbn="${book.isbn}"
                                    data-title="${book.title}">
                                    대출하기
                                </button>
                            </div>
                        </div>
                    `;
                    resultDiv.appendChild(searchBook);
                });

                // 페이지네이션 렌더링
                if (data.totalPageCount > 1) {
                    for (let i = data.startPage; i <= data.endPage; i++) {
                        const pageLink = document.createElement('a');
                        pageLink.href = `/book/book-category?page=${i}&size=${data.size}`;
                        pageLink.textContent = i;
                        if (i === data.page) {
                            pageLink.classList.add('active');
                        }
                        paginationDiv.appendChild(pageLink);
                    }
                }
            } else {
                totalCountElement.textContent = 0; // 검색 결과가 없을 경우
                resultDiv.innerHTML = '<p>검색 결과가 없습니다.</p>';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('검색 중 문제가 발생했습니다. 다시 시도해주세요.');
        });
};


/**************************************/
// 📌 보기설정 변경 시 페이지 새로고침
const viewSizeSelect = document.getElementById('view-size-select');
viewSizeSelect.addEventListener("change", () => {
    const searchParams = new URLSearchParams(location.search);
    searchParams.set('size', viewSizeSelect.value);
    location.href = `/book/book-category?${searchParams.toString()}`;
});

/******************* 대출하기 버튼 *********************/
document.addEventListener("DOMContentLoaded", function () {
    const csrfToken = document.querySelector('meta[name=_csrf]')?.content;

    /********** 🔹 로그인 여부 확인 함수 **********/
    function isUserLoggedIn() {
        return document.querySelector(".user-logged-in") !== null;
    }

    /********** 🔹 대출 버튼 클릭 이벤트 **********/
    document.addEventListener("click", async function (event) {
        const button = event.target.closest(".book-rent-button");
        if (!button) return;

        // 🔹 로그인 여부 체크
        if (!isUserLoggedIn()) {
            alert("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
            window.location.href = "/user/login";
            return;
        }

        if (!confirm('대출하시겠습니까?')) return;
        IMP.init("imp25064853"); // 포트원 가맹점 코드

        // 🔹 책 정보 가져오기
        const bookInfo = button.closest(".book-info");
        const bookIsbn = button.getAttribute("data-isbn");
        const bookTitle = button.getAttribute("data-title");
        const bookAuthor = bookInfo.querySelector(".author")?.textContent.trim();
        const originalPrice = parseInt(bookInfo.querySelector(".book-price-hidden")?.value, 10) || 0;

        const userPoints = await fetchUserPoints();
        let maxDiscount = Math.floor(originalPrice / 10000) * 1000;
        let usedPoints = Math.min(userPoints, maxDiscount);
        let discountAmount = Math.min(originalPrice, Math.floor(usedPoints / 1000) * 10000);
        let finalPrice = Math.max(0, originalPrice - discountAmount);

        const loanObject = { bookTitle, bookAuthor, bookIsbn, originalPrice, discountAmount, finalPrice, usedPoints };

        if (finalPrice === 0) {
            console.log("🎉 결제 필요 없음 - 바로 대출 처리 진행");
            return requestLoan(loanObject);
        }

        IMP.request_pay(
            {
                channelKey: "channel-key-744b24b7-9388-444b-8aa9-c38549be4242",
                pg: "kakaopay",
                merchant_uid: `loan_${bookIsbn}_${new Date().getTime()}`,
                currency: "KRW",
                name: `${bookTitle} 대출`,
                amount: finalPrice
            },
            function (response) {
                if (!response.success) {
                    console.error("❌ 결제 실패:", response.error_msg);
                    alert(`결제 실패: ${response.error_msg}`);
                    return;
                }

                loanObject.impUid = response.imp_uid;
                requestLoan(loanObject);
                if(confirm("대여 목록으로 이동하시겠습니까?")) {
                    location.href="/user/lendbook";
                }
            }
        );
    });

    /********** 🔹 대출 요청 (포인트 포함하여 서버로 전송) **********/
    function requestLoan(requestBody) {
        fetch(`/loan`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },
            credentials: "include",
            body: JSON.stringify(requestBody)
        })
            .then(response => response.text().then(data => ({ response, data })))
            .then(({ response, data }) => {
                console.log("📨 서버 응답 성공");
            })
            .catch(error => {
                console.error("❌ 대출 요청 중 오류 발생:", error);
            });
    }

    /********** 🔹 사용자 포인트 조회 **********/
    async function fetchUserPoints() {
        try {
            const response = await fetch("/points");
            if (!response.ok) throw new Error("포인트 정보를 가져올 수 없습니다.");
            return await response.json();
        } catch (error) {
            console.error("❌ 포인트 조회 오류:", error);
            return 0;
        }
    }
});

