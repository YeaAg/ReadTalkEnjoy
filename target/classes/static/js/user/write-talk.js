const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content'); // CSRF 토큰 추출 (없으면 undefined)

document.forms[0].onsubmit = (event) => {
    event.preventDefault();
    // name 속성을 이용해 요소 가져오기
    const bookTitle = document.getElementsByName("bookTitle")[0].value;
    const topic = document.getElementsByName("topic")[0].value;
    const contents = document.getElementsByName("contents")[0].value;

    // 객체 생성
    const discussionData = {
        bookTitle: bookTitle,
        topic: topic,
        contents: contents
    };

    // JSON 데이터 서버로 전송
    fetch("/discussion/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            'X-CSRF-TOKEN': csrfToken // CSRF 토큰 추가
        },
        body: JSON.stringify(discussionData)
    })
        .then(response => response.text())
        .then(data => {
            alert("토론 게시판에 등록되었습니다!");
            console.log("서버 응답");
        })
        .catch(error => {
            console.error("오류 발생:", error);
        });
}
