document.addEventListener("DOMContentLoaded", function () {
    let currentIndex = 0;
    const slides = document.querySelectorAll(".book-slide");
    const slideContainer = document.querySelector(".book-slide-container");
    const totalSlides = slides.length;
    slideContainer.style.width = `${totalSlides * 100}%`;
    function updateSlidePosition() {
        slideContainer.style.transform = `translateX(${-currentIndex * 100}%)`;
        document.querySelector(".prev-btn").style.display = currentIndex === 0 ? "none" : "block";
        document.querySelector(".next-btn").style.display = currentIndex === totalSlides - 1 ? "none" : "block";
    }
    window.prevBook = function () {
        if (currentIndex > 0) {
            currentIndex--;
            updateSlidePosition();
        }
    };
    window.nextBook = function () {
        if (currentIndex < totalSlides - 1) {
            currentIndex++;
            updateSlidePosition();
        }
    };
    updateSlidePosition();
});
