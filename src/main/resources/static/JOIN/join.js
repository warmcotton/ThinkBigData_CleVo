// 에러 메세지 객체
const errMsg = {
  id: { 
    invalid: "10자 이내로 입력해주세요",
    success: "사용 가능한 별명입니다",
    fail: "사용할 수 없는 별명입니다",
    check: "별명 중복 확인을 해주세요"  // 추가된 메시지
  },
  pw: "8~20자의 영문, 숫자, 특수문자를 모두 포함한 비밀번호를 입력해주세요",
  pwRe: {
    success: "비밀번호가 일치합니다",
    fail: "비밀번호가 일치하지 않습니다"
  },
  birth: "생년월일을 다시 확인해주세요",
  email: "이메일을 입력해주세요",
  name: "이름을 입력해주세요",
  gender: "성별을 선택해주세요"
}

// 계정 정보 객체
const account = {
  id: null,
  pw: null,
  email: null,
  birth: null,
  name: null,
  gender: null,
  isIdChecked: false  // 아이디 중복 확인 여부
}

/*** SECTION - ID ***/
const idInputEl = document.querySelector('#info__id input')
const idErrorMsgEl = document.querySelector('#info__id .error-msg')
const idCheckBtn = document.querySelector('#id-check')
idInputEl.addEventListener('change', () => {
  const idRegExp = /^.{1,10}$/ // 한글, 영어, 숫자 상관 없이 10자 이내
  if(idRegExp.test(idInputEl.value)) { // 정규식 조건 만족 O
    idErrorMsgEl.textContent = ""
    account.id = idInputEl.value
    account.isIdChecked = false  // 아이디가 변경되면 중복 확인 다시 해야 함
  } else { // 정규식 조건 만족 X
    idErrorMsgEl.style.color = "red"
    idErrorMsgEl.textContent = errMsg.id.invalid
    account.id = null
  }
});

idCheckBtn.addEventListener('click', () => {
  const randVal = Math.floor(Math.random() * 10)
  if(account.id !== null) {
    if(randVal < 7) {
      idErrorMsgEl.style.color = "green"
      idErrorMsgEl.textContent = errMsg.id.success
      account.isIdChecked = true  // 중복 확인 성공
    }
    else {
      idErrorMsgEl.style.color = "red"
      idErrorMsgEl.textContent = errMsg.id.fail
      account.isIdChecked = false  // 중복 확인 실패
    }
  }
})

/*** SECTION - PASSWORD ***/
// pwVal: 패스워드, pwReVal: 패스워드 재입력, isPwValid: 패스워드 유효 여부
let pwVal = "", pwReVal = "", isPwValid = false
// 비밀번호와 재입력 값 일치 여부
function checkPwValid() {
  account.pw = null
  if(pwReVal === "") { // 미입력
    pwReErrorMsgEl.textContent = ""
  }
  else if(pwVal === pwReVal) { // 비밀번호 재입력 일치
    if(isPwValid)
      account.pw = pwVal
    pwReErrorMsgEl.style.color = "green"
    pwReErrorMsgEl.textContent = errMsg.pwRe.success
  }
  else { // 비밀번호 재입력 불일치
    pwReErrorMsgEl.style.color = "red"
    pwReErrorMsgEl.textContent = errMsg.pwRe.fail
  }
}

const pwInputEl = document.querySelector('#info__pw input')
const pwErrorMsgEl = document.querySelector('#info__pw .error-msg')
pwInputEl.addEventListener('change', () => {
  const pwRegExp = /^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,16}$/
  pwVal = pwInputEl.value
  if(pwRegExp.test(pwVal)) { // 유효성 검사 성공
    isPwValid = true
    pwErrorMsgEl.textContent = ""
  } 
  else { // 유효성 검사 실패
    isPwValid = false
    pwErrorMsgEl.textContent = errMsg.pw
  }
  checkPwValid()
});

/*** SECTION - PASSWORD RECHECK ***/
const pwReInputEl = document.querySelector('#info__pwRe input')
const pwReErrorMsgEl = document.querySelector('#info__pwRe .error-msg')
pwReInputEl.addEventListener('change', () => {
  pwReVal = pwReInputEl.value
  checkPwValid()
});

/*** SECTION - EMAIL ***/
emailList = ["", ""]
function checkEmailValid() {
  if(emailList[0] !== "" && emailList[1] !== "") {
    account.email = emailList.join('@')
  } else {
    account.email = null
  }
}

const emailInputEl = document.querySelector('#email-txt')
emailInputEl.addEventListener('change', () => {
  emailList[0] = emailInputEl.value
  checkEmailValid()
})

const domainInputEl = document.querySelector('#domain-txt')
domainInputEl.addEventListener('change', () => {
  emailList[1] = domainInputEl.value
  checkEmailValid()
})

// 도메인 직접 입력 or domain option 선택
const domainListEl = document.querySelector('#domain-list')
domainListEl.addEventListener('change', () => {
  // option에 있는 도메인 선택 시
  const domainSelected = domainListEl.value
  if(domainSelected !== "type") {
    // 선택한 도메인을 input에 입력하고 disabled
    domainInputEl.value = domainSelected
    domainInputEl.disabled = true
    emailList[1] = domainSelected
  } else { // 직접 입력
    // input 내용 초기화 & 입력 가능하도록 변경
    domainInputEl.value = ""
    domainInputEl.disabled = false
    emailList[1] = ""
  }
  checkEmailValid()
})


/*** SECTION - BIRTH ***/
const birthArr = [-1, -1, -1]
/* 유효한 날짜인지 여부 확인 (윤년/평년) */
function checkBirthValid(birthArr) {
  isBirthValid = true
  y = birthArr[0]
  m = birthArr[1]
  d = birthArr[2]
  // 생년월일 모두 선택 완료 시
  if(y > 0 && m > 0 && d > 0) {
    if ((m == 4 || m == 6 || m == 9 || m == 11) && d == 31) { 
      isBirthValid = false
    }
    else if (m == 2) {
      if(((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0)) { // 윤년
        if(d > 29) { // 윤년은 29일까지
          isBirthValid = false
        }
      } else { // 평년
        if(d > 28) { // 평년은 28일까지
          isBirthValid = false
        }
      }
    }

    if(isBirthValid) { // 유효한 날짜
      birthErrorMsgEl.textContent = ""
      account.birth = birthArr.join('-')  // 변경된 포맷
    } else { // 유효하지 않은 날짜
      birthErrorMsgEl.textContent = errMsg.birth
      account.birth = null
    }
  }
}

const birthErrorMsgEl = document.querySelector('#info__birth .error-msg')
// '출생 연도' 셀렉트 박스 option 목록 동적 생성
const birthYearEl = document.querySelector('#birth-year')
// option 목록 생성 여부 확인
isYearOptionExisted = false;
birthYearEl.addEventListener('focus', function () {
  // year 목록 생성되지 않았을 때 (최초 클릭 시)
  if(!isYearOptionExisted) {
    isYearOptionExisted = true
    for(var i = 1940; i <= 2022; i++) {
      // option element 생성
      const yearOption = document.createElement('option')
      yearOption.setAttribute('value', i)
      yearOption.innerText = i
      // birthYearEl의 자식 요소로 추가
      this.appendChild(yearOption);
    }
  }
});

birthYearEl.addEventListener('change', () => {
  birthArr[0] = birthYearEl.value
  birthYearEl.style.color = "#383838"
  checkBirthValid(birthArr)
});

// 월 select box
const birthMonthEl = document.querySelector('#birth-month')
isMonthOptionExisted = false;
birthMonthEl.addEventListener('focus', function () {
  if(!isMonthOptionExisted) {
    isMonthOptionExisted = true
    for(var i = 1; i <= 12; i++) {
      const monthOption = document.createElement('option')
      if(i < 10)
        monthOption.setAttribute('value', '0' + i)
      else
        monthOption.setAttribute('value', i)
      monthOption.innerText = i
      this.appendChild(monthOption);
    }
  }
});

birthMonthEl.addEventListener('change', () => {
  birthArr[1] = birthMonthEl.value
  birthMonthEl.style.color = "#383838"
  checkBirthValid(birthArr)
});

// 일 select box
const birthDayEl = document.querySelector('#birth-day')
isDayOptionExisted = false;
birthDayEl.addEventListener('focus', function () {
  if(!isDayOptionExisted) {
    isDayOptionExisted = true
    for(var i = 1; i <= 31; i++) {
      const dayOption = document.createElement('option')
      if(i < 10)
        dayOption.setAttribute('value', '0' + i)
      else
        dayOption.setAttribute('value', i)
      dayOption.innerText = i
      this.appendChild(dayOption);
    }
  }
});

birthDayEl.addEventListener('change', () => {
  birthArr[2] = birthDayEl.value
  birthDayEl.style.color = "#383838"
  checkBirthValid(birthArr)
});

/*** GENDER ***/
const genderInputs = document.querySelectorAll('input[name="gender"]');
genderInputs.forEach(input => {
  input.addEventListener('change', () => {
    if (input.checked) {
      account.gender = input.id;
    }
  });
});

/*** NAME ***/
const nameInputEl = document.querySelector('#info__name input');
nameInputEl.addEventListener('change', () => {
  account.name = nameInputEl.value.trim() !== "" ? nameInputEl.value : null;
});

/*** SUBMIT ***/
const submitBtn = document.querySelector('#submit')
const resultFailEl = document.querySelector('#result-fail')
submitBtn.addEventListener('click', async function() {
  let isAllFilled = true
  const word = {  
    pw: "비밀번호를",
    email: "이메일을",
    id: "별명을",
    birth: "생년월일을",
    name: "이름을",
    gender: "성별을"  // 추가된 필드
  }

  // 아이디 중복 확인 여부 체크
  if (!account.isIdChecked) {
    alert(errMsg.id.check);
    return;
  }

  for(element in account) {
    if(account[element] === null) {
      resultFailEl.textContent = word[element] + " 다시 한번 확인해주세요";
      isAllFilled = false;
      break;
    }
  }

  if (isAllFilled) {
    resultFailEl.textContent = ""

    // 서버로 데이터 전송
    try {
    const requestBody = {
            email: account.email,
            password1: account.pw,
            password2: account.pw,
            name: account.name,
            nickname: account.id,
            birth: account.birth,
            gender: account.gender
          };

      const response = await fetch('/signup/user', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
      });

      const data = await response.json();

 // 응답이 성공적일 때만 JSON으로 파싱
      if (response.ok) {
        // 세션 ID 가져오기
        const sessionId = response.headers.get('sessionId');

        // 성공적으로 회원가입이 완료되면 다음 페이지로 이동
        if (data && sessionId) {
          // 세션 ID와 사용자 정보를 localStorage에 저장하여 다른 페이지에서 접근 가능하도록 함
          localStorage.setItem('sessionId', sessionId);
          localStorage.setItem('userDto', JSON.stringify(data));

          // 페이지 이동
          window.location.href = '/LEVEL/level.html';
        } else {
          alert('회원가입은 성공했지만 일부 정보가 누락되었습니다.');
        }
      } else {
        const errorData = await response.json();
        resultFailEl.textContent = errorData.error || '회원가입에 실패했습니다.';
      }
    } catch (error) {
      resultFailEl.textContent = '회원가입 요청 중 문제가 발생했습니다.';
      console.error('회원가입 오류:', error);
    }
  }
});