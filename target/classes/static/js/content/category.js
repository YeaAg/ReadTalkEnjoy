const heartButton = document.querySelector('.book-heart-button');
const rentButton = document.querySelector('.book-heart-button');

const viewSizeSelect = document.getElementById('view-size-select');

const input = document.querySelector('.search-input');
const button = document.querySelector('.search-button');

/******************************************/

// 검색하기
// enter 키 눌렀을 때
input.onkeypress = (event) => {
    if (event.key === 'Enter') {
        event.preventDefault(); // 기본 폼 제출 방지
        executeSearch(); // 검색 함수 호출
    }
};

// 돋보기 아이콘 눌렀을 때
button.onclick = () => {
    executeSearch(); // 검색 함수 호출
};

const executeSearch = () => {
    const inputValue = input.value.trim();

    if (inputValue === "") {
        alert("검색을 해주세요");
        input.value = ""; // 입력 필드 초기화
    }

    fetch('/search', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ query: inputValue }) // 객체로 감싸서 전달
    })
        .then(response => response.json())
        .then(data => {
            // 기존 검색 결과 초기화
            const resultDiv = document.querySelector('.all-book');
            resultDiv.innerHTML = ''; // 이전 검색 결과 지우기

            if (data.length > 0) {
                // 검색 결과가 있을 경우
                data.forEach(book => {
                    const searchBook = document.createElement('div');
                    searchBook.className = 'one-book';
                    searchBook.innerHTML = `
                    <div class="image-container">
                        <img src="${book.image || '../../static/images/book_main.jpg'}" alt="${book.title}"/>
                    </div>
                    <div class="book-info">
                        <h2>
                            <a href="/book">${book.title}</a>
                        </h2>
                        <div class="author-publisher">
                            <span class="author">${book.author}</span>
                            <span>/</span>
                            <span class="publisher">${book.publisher}</span>
                        </div>
                        <div class="rent-available">
                            <span>대출가능여부: </span>
                            <span class="rent-status">${book.rentStatus}</span>
                        </div>
                        <div class="plot">
                            <p>${book.plot}</p>
                        </div>
                        <div class="rent-button-section">
                            <button class="book-heart-button">찜하기</button>
                            <button class="book-rent-button">대출하기</button>
                        </div>
                    </div>
                `;
                    resultDiv.appendChild(searchBook); // 검색 결과를 결과 영역에 추가
                });
            } else {
                // 검색 결과가 없을 경우
                resultDiv.innerHTML = '<p>검색 결과가 없습니다.</p>';
            }
        })
        .catch(error => console.error('Error:', error));
};
/**************************************/

// 찜하기 버튼을 눌렀을 때
heartButton.onclick = () => {
    if (confirm('찜하시겠습니까?')){
        if (confirm('찜 목록으로 이동하시겠습니까?')){
            location.href = '/user/info';
        }
    }
}

// 대출하기 버튼을 눌렀을 때
rentButton.onclick = () => {
    confirm('대출하시겠습니까?');
}

/*******************************************/

// 보기설정
viewSizeSelect.onchange = () => {
    const searchParams = new URLSearchParams(location.search);
    searchParams.set('size', viewSizeSelect.value);
    location.href = `/book?${searchParams.toString()}`;
}