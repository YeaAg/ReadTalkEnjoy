let currentPage = 0; // 현재 페이지 인덱스
const imagesPerPage = 2; // 한 페이지에 보여줄 이미지 수
const images = document.querySelectorAll('#bookImages img'); // 이미지 요소들
const totalPages = Math.ceil(images.length / imagesPerPage); // 총 페이지 수
const mainPage = document.getElementById('bookMainPageContainer');
const bookPages = document.getElementById('bookImagesContainer');

function showPage(page) {
    // 모든 이미지를 숨김
    images.forEach(image => image.style.display = 'none');

    if(currentPage>=1){
        mainPage.style.display = 'none';
        bookPages.style.removeProperty('flex');
    }
    else{
        mainPage.style.display = 'block';
        bookPages.classList.add('flex-container');
    }

    // 현재 페이지의 이미지 보여주기
    const start = page * imagesPerPage;
    const end = start + imagesPerPage;
    for (let i = start; i < end && i < images.length; i++) {
        images[i].style.display = 'block';
    }

    // 버튼 활성화/비활성화
    document.getElementById('prevPage').disabled = page === 0;
    document.getElementById('nextPage').disabled = page >= totalPages - 1;
}

// 페이지 로드 시 첫 페이지 보여주기
showPage(currentPage);

// 이전 페이지 버튼 클릭 시
document.getElementById('prevPage').addEventListener('click', () => {
    if (currentPage > 0) {
        currentPage--;
        showPage(currentPage);
    }
    console.log(currentPage);
});

// 다음 페이지 버튼 클릭 시
document.getElementById('nextPage').addEventListener('click', () => {
    if (currentPage < totalPages - 1) {
        currentPage++;
        showPage(currentPage);
    }
});