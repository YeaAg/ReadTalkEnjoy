const bookForm = document.forms.namedItem('book');
const bookIsbn = bookForm.id;

const discussionBtn = document.querySelector(".discussion-btn");
const loanBtn = document.querySelector(".loan-btn");
const cartBtn = document.querySelector(".heart-btn");

const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content'); // CSRF 토큰 가져오기

const reviewForm = document.getElementById('review-form')

discussionBtn.onclick = () => {
    const bookTitleElement = document.querySelector("h1"); // 책 제목 가져오기
    if (!bookTitleElement) {
        console.error("🚨 책 제목을 찾을 수 없습니다!");
        return;
    }

    let bookTitle = bookTitleElement.innerText.trim();
    if (bookTitle === "") {
        console.error("🚨 책 제목이 비어 있습니다.");
        return;
    }

    // 🔹 URL에서 안전하게 사용하기 위해 인코딩
    const encodedBookTitle = encodeURIComponent(bookTitle);

    console.log(`📚 토론 검색 요청: ${bookTitle}`);

    // ✅ 쿠키 설정 (URL 인코딩 적용)
    document.cookie = `searchKeyword=${encodedBookTitle}; path=/; max-age=300`;

    // ✅ 저장된 쿠키 확인
    console.log("🍪 저장된 쿠키:", document.cookie);

    // 🔥 검색된 페이지로 이동
    location.href = `/discussion/category`;
};

/*******************************************/
// 찜하기 버튼을 눌렀을 때
cartBtn.onclick = () => {
    const cartObject = createCartObject();

    if (!cartObject || !cartObject.isbn) {
        alert('책 정보를 가져올 수 없습니다.');
        return;
    }
    if (confirm('찜하시겠습니까?')) {
        // 서버 요청
        fetch(`/user/wishlist/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken, // CSRF 토큰 추가
            },
            body: JSON.stringify(cartObject), // JSON 형식으로 요청 본문 설정
        })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 401) {
                        throw new Error('로그인이 필요합니다.');
                    }
                    throw new Error('찜하기 요청 실패');
                }
                return response.json(); // 응답 처리
            })
            .then(cartNo => {
                if (confirm(`찜했습니다. 찜한 내역을 확인하시겠습니까?`)) {
                    location.href = '/user/wishlist'; // 찜한 목록 페이지로 이동
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message || '찜 요청 처리 중 문제가 발생했습니다. 다시 시도해주세요.');
            });
    }
};

function createCartObject() {
    const bookForm = document.forms.namedItem('book'); // 폼 요소 선택
    if (!bookForm) {
        console.error("bookForm 을 찾을 수 없습니다.");
        return null;
    }

    const bookTitle = document.querySelector('h1').innerText.trim(); // 책 제목
    const bookAuthor = document.querySelector('h2').innerText.split('/')[0].trim(); // 저자
    const bookPublisher = document.querySelector('h2').innerText.split('/')[1]?.trim(); // 출판사
    const bookPrice = document.querySelector('.book-price span:nth-of-type(2)')?.innerText?.replace(/[^0-9]/g, ''); // 가격

    return {
        isbn: bookForm.id,
        title: bookTitle || null,
        author: bookAuthor || null,
        publisher: bookPublisher || null,
        price: parseInt(bookPrice, 10) || 0,
    };
}

/**************************************/
document.addEventListener("DOMContentLoaded", function () {
    const reviewFormContainer = document.getElementById("review-form");

    function initializeReviewForm() {
        const form = document.querySelector(".my-opinion-form");

        if (!form) {
            console.error("🚨 리뷰 작성 폼을 찾을 수 없습니다. 다시 시도합니다...");
            return;
        }

        console.log("✅ 리뷰 작성 폼을 찾았습니다!");

        form.addEventListener("submit", function (event) {
            event.preventDefault();

            const bookIsbn = document.forms.namedItem("book").id;
            const textArea = form.querySelector("textarea");
            const reviewContent = textArea.value.trim();
            const ratingValue = parseInt(document.getElementById("rating-value").value, 10);

            if (reviewContent === "") {
                alert("리뷰 내용을 입력해주세요.");
                return;
            }

            if (!ratingValue || ratingValue < 1 || ratingValue > 5) {
                alert("별점을 올바르게 선택해주세요.");
                return;
            }

            const reviewData = { content: reviewContent, rate: ratingValue };

            fetch(`/book/${bookIsbn}/review/add`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": document.querySelector("meta[name='_csrf']").getAttribute("content")
                },
                body: JSON.stringify(reviewData)
            })
                .then(response => response.text())
                .then(() => {
                    console.log("✅ 리뷰 추가 성공! 목록 업데이트");
                    updateReviewSection(bookIsbn);
                })
                .catch(error => console.error("❌ Error:", error));
        });

        // ⭐ 별점 선택 기능 추가
        const stars = document.querySelectorAll(".star-rating i");
        const ratingValue = document.getElementById("rating-value");

        stars.forEach(star => {
            star.addEventListener("click", function () {
                const value = this.getAttribute("data-value");
                ratingValue.value = value;

                // 클릭한 별과 그 이전 별들은 모두 채우기 (solid)
                stars.forEach((s, index) => {
                    if (index < value) {
                        s.classList.remove("fa-regular");
                        s.classList.add("fa-solid");
                    } else {
                        s.classList.remove("fa-solid");
                        s.classList.add("fa-regular");
                    }
                });
            });
        });

        // 👍 좋아요 버튼 클릭 이벤트 바인딩
        document.querySelectorAll(".review-recommend-section").forEach(button => {
            button.addEventListener("click", function () {
                const bookIsbn = document.forms.namedItem("book").id;
                const reviewContent = this.closest(".review").querySelector(".review-content").innerText.trim();
                const userId = this.closest(".review").querySelector(".review-user-name").innerText.trim();

                console.log(`👍 좋아요 요청: bookIsbn=${bookIsbn}, content=${reviewContent}, userId=${userId}`);

                fetch(`/book/${bookIsbn}/review/like?content=${encodeURIComponent(reviewContent)}&userId=${encodeURIComponent(userId)}`, {
                    method: "POST",
                    headers: {
                        "X-CSRF-TOKEN": document.querySelector("meta[name='_csrf']").getAttribute("content")
                    }
                })
                    .then(response => {
                        if (response.status === 401) {
                            alert("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
                            window.location.href = "/user/login";
                            throw new Error("로그인이 필요합니다.");
                        }
                        return response.text();
                    })
                    .then(() => {
                        console.log("✅ 좋아요 성공! 리뷰 목록 업데이트");
                        updateReviewSection(bookIsbn);
                    })
                    .catch(error => console.error("❌ Error:", error));
            });
        });
    }

    function updateReviewSection(bookIsbn) {
        fetch(`/book/${bookIsbn}/review`)
            .then(response => response.text())
            .then(reviewTemplate => {
                reviewFormContainer.innerHTML = reviewTemplate;
                initializeReviewForm(); // 리뷰 폼 재초기화
            })
            .catch(error => console.error("❌ 리뷰 로딩 실패:", error));
    }

    const bookForm = document.forms.namedItem("book");
    if (bookForm) {
        const bookIsbn = bookForm.id;
        updateReviewSection(bookIsbn);
    }
});

/**************************************/
/// 상품에 대한 리뷰 불러오기
load_review(null, `/book/${bookIsbn}/review`);
function load_review(event, url){
    if(event !== null){
        event.preventDefault();
    }
    reviewForm.innerHTML = '';
    fetch(url)
        .then(response => response.text())
        .then(reviewTemplate => {
            reviewForm.insertAdjacentHTML(`beforeend`, reviewTemplate)
        });
}

/********************* 대여 버튼 *****************************/

document.addEventListener("DOMContentLoaded", function () {
    const loanBtn = document.querySelector(".loan-btn");
    if (!loanBtn) return;

    /********** 🔹 로그인 여부 확인 함수 **********/
    function isUserLoggedIn() {
        return document.querySelector(".user-logged-in") !== null;
    }

    /********** 🔹 대출 버튼 클릭 이벤트 **********/
    loanBtn.onclick = async () => {
        if (!isUserLoggedIn()) {
            alert("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
            window.location.href = "/user/login";
            return;
        }

        if (!confirm('대출하시겠습니까?')) return;
        IMP.init("imp25064853"); // 가맹점 코드 확인

        const loanObject = await createLoanObj();

        if (!loanObject) {
            console.error("❌ 대출 객체 생성 실패");
            return;
        }

        console.log("포트원 결제 요청 시작...")

        // 🔹 결제 금액이 0원이면 결제 없이 바로 대출 요청
        if (loanObject.finalPrice === 0) {
            console.log("🎉 결제 필요 없음 - 바로 대출 처리 진행");
            return requestLoan(loanObject);
        }

        // 🔹 포트원 결제 요청
        IMP.request_pay(
            {
                channelKey: "channel-key-744b24b7-9388-444b-8aa9-c38549be4242",
                pg: "kakaopay",
                merchant_uid: `loan_${loanObject.bookIsbn}_${new Date().getTime()}`,
                currency: "KRW",
                name: `${loanObject.bookTitle} 대출`,
                amount: loanObject.finalPrice
            },
            function (response) {
                if (!response.success) {
                    alert(`결제 실패: ${response.error_msg}`);
                    return;
                }

                if (!response.imp_uid) {
                    alert("결제 정보가 정상적으로 처리되지 않았습니다. 다시 시도해주세요.");
                    return;
                }
                loanObject.impUid = response.imp_uid; // 🔹 impUid 추가

                requestLoan(loanObject);
                if(confirm("대여 목록으로 이동하시겠습니까?")) {
                    location.href = "/user/lendbook";
                }
            }
        );
    };

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
        }).then(response => {
            return response.text().then(data => ({ response, data }));
        }).then(({ response, data }) => {
            console.log("📨 서버 응답 성공");
        }).catch(error => {
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

    /********** 🔹 대출 객체 생성 (비동기 함수로 수정) **********/
    async function createLoanObj() {
        const bookTitle = document.querySelector("h1")?.textContent?.trim();
        const bookAuthor = document.querySelector("h2")?.textContent?.split("/")[0]?.trim();
        const bookIsbn = document.querySelector("form[name='book']")?.id;
        const originalPrice = parseInt(
            document.querySelector(".book-price span:nth-child(2)").textContent.replace(/[^0-9]/g, ""),
            10
        );

        // 🔹 사용자 포인트 가져오기 (비동기 처리)
        const userPoints = await fetchUserPoints();

        // 🔹 포인트 할인 계산
        let maxDiscount = Math.floor(originalPrice / 10000) * 1000;  // 1000포인트당 10,000원 할인
        let usedPoints = Math.min(userPoints, maxDiscount); // 사용 가능한 최대 포인트
        let discountAmount = Math.floor(usedPoints / 1000) * 10000; // 실제 할인 적용 금액
        discountAmount = Math.min(originalPrice, discountAmount); // 원래 가격보다 할인을 초과할 수 없음

        let finalPrice = Math.max(0, originalPrice - discountAmount); // 최종 결제 금액 (0원 이상)

        console.log(`✅ 포인트 적용 전 가격: ${originalPrice}, 할인 금액: ${discountAmount}, 최종 결제 금액: ${finalPrice}`);

        return {
            bookTitle,
            bookAuthor,
            bookIsbn,
            originalPrice,
            discountAmount,
            finalPrice,
            usedPoints
        };
    }
    }
);

