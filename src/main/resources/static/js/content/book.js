const bookForm = document.forms.namedItem('book');

const discussionBtn = document.querySelector(".discussion-btn");
const loanBtn = document.querySelector(".loan-btn");

const reviewForm = document.getElementById('review-form')

// 토론하러 가기 버튼을 눌렀을 때
discussionBtn.onclick = () => {
    const isbn = document.getElementById('bookIsbn').value; // hidden input에서 ISBN 값 가져오기
    location.href = `/book/${isbn}`;
}

/*******************************************/

// 찜하기 버튼을 눌렀을 때
// 어디에 저장?
loanBtn.onclick = () => {
    confirm('찜하시겠습니까?');
    const bookObject = create_book_object();
    request('/info', bookObject.book).then(() => {
        // 대출하기일 경우
        if(confirm('찜했습니다. 찜한 내역을 확인하시겠습니까?')){
            location.href = '/user/info';
        }
    });
}

// 대출하기 버튼을 눌렀을 때
loanBtn.onclick = () => {
    confirm('대출하시겠습니까?');
    const bookObject = create_book_object();
    request('/info', bookObject.loan).then(() => {
        // 대출하기일 경우
        if(confirm('대출에 완료했습니다. 대출 내역을 확인하시겠습니까?')){
            location.href = '/user/info';
        }
    });
}

function create_book_object(){
    return {
        // bookDTO 형태
        loan: {
            id: bookForm.id,
        }
    }
}

// 찜/대출하기 페이지에 상품 추가하는 요청
function request(url, requestBody){
    const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
    // 대출하기에 POST 요청 전송
    return fetch(url, {
        method: "POST",
        headers: {
            "X-CSRF-TOKEN": csrfToken,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBody)
    }).then(response => {
        // 로그인이 안된 유저가 클릭 시
        if(response.status === 401){
            alert('로그인을 먼저 해주세요');
            throw new Error();
        }
        else if(!response.ok){
            alert('시스템 에러 발생!');
            throw new Error();
        }
    });
}

/**************************************/

/// 일단 쿠키런 스토어에 있는거 가져옴
// 화면 로딩 시 최초 리뷰 로딩
const bookNo = bookForm.id;
load_review(null, `/book/${bookNo}/review`);
/// 상품에 대한 리뷰 불러오기
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