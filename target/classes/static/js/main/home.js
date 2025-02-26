document.addEventListener("DOMContentLoaded", function() {
    new Swiper(".swiper", {
        loop: true, // 무한 반복
        autoplay: {
            delay: 5000, // 5초마다 변경
            disableOnInteraction: false
        },
        speed: 5000, // 전환 속도
        effect: "slide", // 기본 슬라이드 효과 (부드러운 느낌)
        slidesPerView: 1, // 한 번에 한 개의 이미지만 보이도록 설정
        pagination: {
            el: ".swiper-pagination",
            clickable: true
        },
        navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev"
        }
    });
});

