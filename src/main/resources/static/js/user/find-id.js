const emailAuthInput = document.querySelector('.email-input-container input')
const emailAuthBtn = document.querySelector('.email-input-container button');
const emailAuthConfirmInput = document.querySelector('.email-auth-input-container input');
const emailAuthConfirmBtn = document.querySelector('.email-auth-input-container button');


const findIdSection = document.querySelector('.find-id-section')
const foundIdSpan = findIdSection.querySelector('span');

let emailAuthCompleted = false; // 이메일 인증 여부

const gotoLoginBtn = findIdSection.querySelector('button');

emailAuthBtn.onclick = () => {
    const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
    const email = emailAuthInput.value;
    fetch(`/user/email/auth?requestAuth=find-id`,{
        method: "POST",
        headers:{
            "X-CSRF-TOKEN": csrfToken,
            "Content-Type": "text/plain"
        },
        body: email
    }).then(response => {
        if(response.ok){
            alert('인증번호 전송 완료')
        }else{
            alert('인증번호 전송 실패')
        }
    })
}

emailAuthConfirmBtn.onclick = () => {
    const email = emailAuthInput.value;
    const certNumber = emailAuthConfirmInput.value;
    fetch(`/user/email/auth?email=${email}&certNumber=${certNumber}`)
        .then(response => {
            if(response.ok){
                alert('이메일 인증 완료')
                emailAuthCompleted = true;
                emailAuthConfirmInput.setAttribute('disabled', '');
                emailAuthConfirmBtn.setAttribute('disabled', '');
            }else{
                alert('이메일 인증 실패')
                emailAuthCompleted = false;
            }
        })
}

document.forms[0].onsubmit = (event) => {
    event.preventDefault();
    if(emailAuthCompleted){
        const email = emailAuthInput.value;
        fetch(`/user/findId/${email}`)
            .then(response => {
                if(response.status === 302){
                    alert('아이디를 찾았습니다')
                    return response.text()
                }
                else{
                    alert('아이디를 찾지 못했습니다')
                }
            }).then(value => {
                findIdSection.setAttribute('active' ,'');
                foundIdSpan.textContent = `당신의 아이디는 ${value}입니다`;
            })
    }
}
gotoLoginBtn.onclick = () => {
    if(emailAuthCompleted){
        location.href = `/user/login`;
    }
}
