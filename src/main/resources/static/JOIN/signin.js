const account = {
  email: null,
  pw: null
}

/*** SECTION - ID ***/
const idInputEl = document.querySelector('.box#signin-email')
const pwInputEl = document.querySelector('.box#signin-pw')
const signInBtn = document.querySelector('#signin')

idInputEl.addEventListener('change', () => {
  account.email = idInputEl.value
});

pwInputEl.addEventListener('change', () => {
  account.pw = pwInputEl.value
});

signInBtn.addEventListener('click', async () => {
  if(account.email === "" || account.email === null) {
    alert("이메일을 입력해주세요")
  } else if (account.pw === "" || account.pw === null) {
    alert("비밀번호를 입력해주세요")
  } else {
    try {
      const response = await fetch('/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email: account.email, password: account.pw }),
      });



      if (response.ok) {
          const result = await response.json();
          // 로그인 성공 시 액세스 토큰과 리프레시 토큰을 저장
          localStorage.setItem('accessToken', result.access);
          localStorage.setItem('refreshToken', result.refresh);
            window.location.href = '/MAIN/main.html';

            } else {
              // 로그인 실패 시 서버에서 제공하는 메시지 표시
              alert(result.message);
            }
          } catch (error) {
            console.error('Error:', error);
            alert('이메일 또는 비밀번호가 잘못 되었습니다.');
          }
        }
      });