// Error message object
const errMsg = {
  email: "이메일을 입력해주세요.",
  name: "이름을 입력해주세요.",
  birth: "생년월일을 다시 확인해주세요."
};

// Account information object
const account = {
  email: null,
  name: null,
  birth: null,
};

/*** SECTION - EMAIL ***/
let emailList = ["", ""];
function checkEmailValid() {
  if (emailList[0] !== "" && emailList[1] !== "") {
    account.email = emailList.join('@');
  } else {
    account.email = null;
  }
}

// Email input listener
const emailInputEl = document.querySelector('#email-txt');
emailInputEl.addEventListener('change', () => {
  emailList[0] = emailInputEl.value.trim();
  checkEmailValid();
});

// Domain input listener
const domainInputEl = document.querySelector('#domain-txt');
domainInputEl.addEventListener('change', () => {
  emailList[1] = domainInputEl.value.trim();
  checkEmailValid();
});

// Domain dropdown selection
const domainListEl = document.querySelector('#domain-list');
domainListEl.addEventListener('change', () => {
  const domainSelected = domainListEl.value;
  if (domainSelected !== "type") {
    domainInputEl.value = domainSelected;
    domainInputEl.disabled = true;
    emailList[1] = domainSelected;
  } else {
    domainInputEl.value = "";
    domainInputEl.disabled = false;
    emailList[1] = "";
  }
  checkEmailValid();
});

/*** SECTION - NAME ***/
const nameInputEl = document.querySelector('#info__name input');
nameInputEl.addEventListener('change', () => {
  account.name = nameInputEl.value.trim() !== "" ? nameInputEl.value : null;
});

/*** SECTION - BIRTH ***/
const birthArr = [-1, -1, -1];
function checkBirthValid(birthArr) {
  let isBirthValid = true;
  const [y, m, d] = birthArr;

  if (y > 0 && m > 0 && d > 0) {
    // Handle invalid days in months
    if ((m == 4 || m == 6 || m == 9 || m == 11) && d == 31) {
      isBirthValid = false;
    } else if (m == 2) {
      // Handle leap years
      const isLeapYear = (y % 4 == 0 && y % 100 != 0) || y % 400 == 0;
      if ((isLeapYear && d > 29) || (!isLeapYear && d > 28)) {
        isBirthValid = false;
      }
    }

    if (isBirthValid) {
      birthErrorMsgEl.textContent = "";
      account.birth = birthArr.join('-');
    } else {
      birthErrorMsgEl.textContent = errMsg.birth;
      account.birth = null;
    }
  }
}

// Birth date error message element
const birthErrorMsgEl = document.querySelector('#info__birth .error-msg');

// Year select box
const birthYearEl = document.querySelector('#birth-year');
let isYearOptionExisted = false;
birthYearEl.addEventListener('focus', function () {
  if (!isYearOptionExisted) {
    isYearOptionExisted = true;
    for (let i = 1940; i <= new Date().getFullYear(); i++) {
      const yearOption = document.createElement('option');
      yearOption.setAttribute('value', i);
      yearOption.innerText = i;
      this.appendChild(yearOption);
    }
  }
});

// Update year
birthYearEl.addEventListener('change', () => {
  birthArr[0] = parseInt(birthYearEl.value);
  birthYearEl.style.color = "#383838";
  checkBirthValid(birthArr);
});

// Month select box
const birthMonthEl = document.querySelector('#birth-month');
let isMonthOptionExisted = false;
birthMonthEl.addEventListener('focus', function () {
  if (!isMonthOptionExisted) {
    isMonthOptionExisted = true;
    for (let i = 1; i <= 12; i++) {
      const monthOption = document.createElement('option');
      monthOption.setAttribute('value', i);
      monthOption.innerText = i;
      this.appendChild(monthOption);
    }
  }
});

// Update month
birthMonthEl.addEventListener('change', () => {
  birthArr[1] = parseInt(birthMonthEl.value);
  birthMonthEl.style.color = "#383838";
  checkBirthValid(birthArr);
});

// Day select box
const birthDayEl = document.querySelector('#birth-day');
let isDayOptionExisted = false;
birthDayEl.addEventListener('focus', function () {
  if (!isDayOptionExisted) {
    isDayOptionExisted = true;
    for (let i = 1; i <= 31; i++) {
      const dayOption = document.createElement('option');
      dayOption.setAttribute('value', i);
      dayOption.innerText = i;
      this.appendChild(dayOption);
    }
  }
});

// Update day
birthDayEl.addEventListener('change', () => {
  birthArr[2] = parseInt(birthDayEl.value);
  birthDayEl.style.color = "#383838";
  checkBirthValid(birthArr);
});

/*** SUBMIT ***/
const submitBtn = document.querySelector('#submit');
const resultFailEl = document.querySelector('#result-fail');

submitBtn.addEventListener('click', async function () {
  let isAllFilled = true;
  const word = {
    email: "이메일을",
    birth: "생년월일을",
    name: "이름을",
  };

  for (let element in account) {
    if (account[element] === null) {
      resultFailEl.textContent = word[element] + " 다시 한번 확인해주세요.";
      isAllFilled = false;
      break;
    }
  }

  if (isAllFilled) {
    resultFailEl.textContent = "";
    // Simulate password recovery request
    try {
      const response = await fetch('/password-recovery', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: account.email,
          name: account.name,
          birth: account.birth,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        alert('비밀번호 재발급 요청이 완료되었습니다.');
        window.location.href = 'signin.html';
      } else {
        resultFailEl.textContent = data.error || '비밀번호 재발급에 실패했습니다.';
      }
    } catch (error) {
      resultFailEl.textContent = '비밀번호 재발급에 실패했습니다.';
    }
  }
});
