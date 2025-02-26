const emailAuthInput = document.querySelector('.email-input-container input')
const idInput = document.querySelector('.id-input-container input')

document.forms[0].onsubmit = (event) => {
    event.preventDefault();
    const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
    const email = emailAuthInput.value;
    fetch(`/user/email/auth?requestAuth=reset-pw&id=${idInput.value}`,{
        method: "POST",
        headers:{
            "X-CSRF-TOKEN": csrfToken,
            "Content-Type": "text/plain"
        },
        body: email
    }).then(response => {
        if(response.ok){
            alert('비번 이메일 전송 완료')
        }else{
            alert('비번 이메일 전송 실패')
        }
    })
}