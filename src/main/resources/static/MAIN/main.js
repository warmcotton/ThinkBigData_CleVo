async function login(username, password) {
    const response = await fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
    });

    const result = await response.json();
    return result.success;
}

async function logout() {
    const response = await fetch('/logout', {
        method: 'POST',
    });

    const result = await response.json();
    return result.success;
}

async function checkLoginStatus() {
    const response = await fetch('/status');  // 잘못된 부분 수정: 실제 서버 엔드포인트에 맞게 '/status'로 변경
    const result = await response.json();
    return result.isLoggedIn;
}

async function handleAuth() {
    const loginButton = document.getElementById('loginButton');
    const signupButton = document.getElementById('signupButton');
    const studyLink = document.getElementById('studyLink');
    const boardLink = document.getElementById('boardLink');
    const mypageLink = document.getElementById('mypageLink');
    const footerStudyLink = document.getElementById('footerStudyLink');
    const footerBoardLink = document.getElementById('footerBoardLink');
    const footerMypageLink = document.getElementById('footerMypageLink');

    const isLoggedIn = await checkLoginStatus();

    if (isLoggedIn) {
        const success = await logout();
        if (success) {
            loginButton.textContent = '로그인';
            loginButton.href = '../JOIN/signin.html';
            loginButton.onclick = null; // 기존 onclick 이벤트 제거
            signupButton.style.display = 'inline-block'; // 회원가입 버튼 보이기

            // 링크들을 다시 로그인 필요하도록 설정
            studyLink.href = 'javascript:alert("로그인을 먼저 해주세요.");';
            boardLink.href = 'javascript:alert("로그인을 먼저 해주세요.");';
            mypageLink.href = 'javascript:alert("로그인을 먼저 해주세요.");';
            footerStudyLink.href = 'javascript:alert("로그인을 먼저 해주세요.");';
            footerBoardLink.href = 'javascript:alert("로그인을 먼저 해주세요.");';
            footerMypageLink.href = 'javascript:alert("로그인을 먼저 해주세요.");';
        }
    } else {
        const username = prompt('Username:');
        const password = prompt('Password:');
        const success = await login(username, password);
        if (success) {
            loginButton.textContent = '로그아웃';
            loginButton.href = '#';
            loginButton.onclick = function() {
                handleAuth();
            };
            signupButton.style.display = 'none'; // 회원가입 버튼 숨기기

            // 링크들을 실제 페이지로 이동하도록 설정
            studyLink.href = '../STUDY/study.html'; // 실제 학습하기 페이지 링크
            boardLink.href = '../BOARD/board.html'; // 실제 게시판 페이지 링크
            mypageLink.href = '../MYPAGE/mypage.html'; // 실제 마이페이지 링크
            footerStudyLink.href = '../STUDY/study.html';
            footerBoardLink.href = '../BOARD/board.html';
            footerMypageLink.href = '../MYPAGE/mypage.html';
        } else {
            alert('로그인 실패');
        }
    }
}

window.onpageshow = async function() {
    const loginButton = document.getElementById('loginButton');
    const signupButton = document.getElementById('signupButton');

    const isLoggedIn = await checkLoginStatus();

    if (isLoggedIn) {
        loginButton.textContent = '로그아웃';
        loginButton.href = '#';
        loginButton.onclick = function() {
            handleAuth();
        };
        signupButton.style.display = 'none';

        // 링크들을 실제 페이지로 이동하도록 설정
        document.getElementById('studyLink').href = '../STUDY/study.html';
        document.getElementById('boardLink').href = '../BOARD/board.html';
        document.getElementById('mypageLink').href = '../MYPAGE/mypage.html';
        document.getElementById('footerStudyLink').href = '../STUDY/study.html';
        document.getElementById('footerBoardLink').href = '../BOARD/board.html';
        document.getElementById('footerMypageLink').href = '../MYPAGE/mypage.html';
    } else {
        loginButton.textContent = '로그인';
        loginButton.href = '../JOIN/signin.html';
        signupButton.style.display = 'inline-block';

        // 링크들을 로그인 필요하도록 설정
        document.getElementById('studyLink').href = 'javascript:alert("로그인을 먼저 해주세요.");';
        document.getElementById('boardLink').href = 'javascript:alert("로그인을 먼저 해주세요.");';
        document.getElementById('mypageLink').href = 'javascript:alert("로그인을 먼저 해주세요.");';
        document.getElementById('footerStudyLink').href = 'javascript:alert("로그인을 먼저 해주세요.");';
        document.getElementById('footerBoardLink').href = 'javascript:alert("로그인을 먼저 해주세요.");';
        document.getElementById('footerMypageLink').href = 'javascript:alert("로그인을 먼저 해주세요.");';
    }
};
