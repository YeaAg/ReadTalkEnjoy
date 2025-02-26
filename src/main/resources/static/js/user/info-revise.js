const userNickName = document.getElementById('nickname')
const userTel = document.getElementById('tel')
const userEmail = document.getElementById('email')

userNickName.value = userNickName.getAttribute('data');
userTel.value = userTel.getAttribute('data');
userEmail.value = userEmail.getAttribute('data');
