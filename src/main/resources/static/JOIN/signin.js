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
  console.log(Object.values(account))
});

pwInputEl.addEventListener('change', () => {
  account.pw = pwInputEl.value
  console.log(Object.values(account))
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
        body: JSON.stringify({ username: account.email, password: account.pw }),
      });

      const result = await response.json();

      if (response.ok) {
        location.href = "/dashboard";
      } else {
        alert(result.message);
      }
    } catch (error) {
      console.error('Error:', error);
      alert('이메일 또는 비밀번호가 잘못 되었습니다.');
    }
  }
});
