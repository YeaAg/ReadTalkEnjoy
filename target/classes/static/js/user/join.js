const idInput = document.getElementById('id');
const pwInput = document.getElementById('password');
const pwReInput = document.getElementById('password-re');
const idCheckBtn = document.querySelector('.id-input-container button')

const idCheckInput = document.querySelector('input[name="id-check"]');
const idCheckValidInput = document.querySelector('input[name="id-check-valid"]');

const telAuthBtn = document.querySelector('.tel-input-container button');
const [emailHead, emailTail] = document.querySelectorAll('.email-input-container input');
const emailSelect = document.querySelector('.email-input-container select');
const emailAuthBtn = document.querySelector('.email-input-container button');
const emailAuthConfirmInput = document.querySelector('.email-auth-input-container input');
const emailAuthConfirmBtn = document.querySelector('.email-auth-input-container button');
const [joinBtn, cancelBtn] = document.querySelectorAll('.join-btn-section > button');
const [idInputError, pwInputError, telInputError, emailInputError] = document.querySelectorAll('.error-container');

let telAuthCompleted = false; // 휴대폰 인증 여부
let emailAuthCompleted = false; // 이메일 인증 여부

let IMP_init;
/************************************************************************/
// id 중복 체크 버튼 클릭 시
idCheckBtn.onclick = () => {
    const id = idInput.value;
    if(id.trim() === ''){
        alert('ID를 입력해주세요!');
        return;
    }
    fetch(`/user/id/${id}`)
        .then(response => {
            idCheckInput.value = id;
            idCheckValidInput.value = false;

            switch (response.status){
                case 200:
                    alert('해당 아이디는 사용 가능합니다^-^');
                    idCheckValidInput.value = true;
                    break;
                case 302:
                    alert('해당 아이디는 사용 불가능합니다ㅠㅠ');
                    break;
            }
        })
}

/***********************************************/
// IMP_init = get_imp_init();
// async function get_imp_init(){
//     let imp_init;
//     try{
//         await fetch(`/imp_init`)
//             .then(response => {
//                 if(response.ok){
//                     return response.text();
//                 }
//                 alert('imp불러오기 실패')
//             })
//             .then(value => {
//                 console.log("IMP_init :", IMP_init);
//                 return imp_init = value.toString().trim();
//             })
//     } catch (e) {
//         console.log(e);
//     }
//
//
// }
// console.log(IMP_init);
// IMP.init(IMP_init);

IMP.init("imp25064853");

telAuthBtn.onclick = () => {
    IMP.certification(
        {
            channelKey: "channel-key-7a2760a1-e367-48b3-870f-c1b6f2e28776", // KG이니시스
            merchant_uid: "ORD20180131-0000011",
        },
        function (importResponse){
            if(importResponse.success){
                const csrfToken = document.forms[0].querySelector('input[name=_csrf]').value;
                const impUid = importResponse['imp_uid'];
                fetch(`/user/tel/auth`, {
                    method: "POST",
                    headers: {"X-CSRF-TOKEN": csrfToken},
                    body: impUid
                }).then(response => {
                    if(response.ok){
                        console.log('본인인증완료')
                        alert('본인인증 완료')
                    }
                })
                telAuthCompleted=true;
            }else{
                alert('본인인증 실패')
                telAuthCompleted=false;
            }
        }
    )
}

/***********************************************************/
///// ***** 이메일
/// 이메일 주소 직접입력/자동입력
emailSelect.onchange = () => {
    if(emailSelect.value === '직접입력'){
        emailTail.value = '';
        emailTail.readOnly = false;
    }else{
        emailTail.value = emailSelect.value;
        emailTail.readOnly = true;
    }
}

emailAuthBtn.onclick = () => {
    const csrfToken = document.forms[0].querySelector('input[name=_csrf]').value;
    const email = `${emailHead.value}@${emailTail.value}`;
    console.log(email)
    fetch(`/user/email/auth?requestAuth=join`,{
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
    const email = `${emailHead.value}@${emailTail.value}`;
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

joinBtn.onclick = (event) => {
    // input값을 하나라도 잘못 눌렀으면
    // if(!check_input_values()){
    //     event.preventDefault();
    // }
    // const id = idCheckInput.value;
    // const valid = idCheckValidInput.value;
    // // 현재 회원가입하려고 하는 아이디가, 중복체크해서 사용한 아이디와 다르거나
    // // 중복체크를 통과하지 못했다면 회원가입을 시키면 안된다
    // if(idInput.value !== id || valid === 'false'){
    //     alert('id 중복 체크는 필수입니다')
    //     event.preventDefault();
    //     return;
    // }
    // if(!telAuthCompleted){
    //     alert('휴대폰 인증은 필수입니다')
    //     event.preventDefault();
    //     return;
    // }
    // if(!emailAuthCompleted){
    //     alert('이메일 인증은 필수입니다')
    //     event.preventDefault();
    //     return;
    // }
}