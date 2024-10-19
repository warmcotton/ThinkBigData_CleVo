// 현재 표시된 페이지 ID를 저장하는 변수
let currentPage = 'mystudy';

// 페이지를 전환하는 함수
function setNew(pageId) {
    // 모든 페이지 콘텐츠를 숨기기
    document.querySelectorAll('.container > div').forEach(div => {
        div.style.display = 'none';
    });

    // 선택한 페이지 콘텐츠만 보이기
    document.getElementById(pageId).style.display = 'block';

    // 사이드바 메뉴의 활성화 상태를 업데이트
    document.querySelectorAll('.side_bar ul li').forEach(li => {
        li.classList.remove('on');
    });
    document.querySelector(`.side_bar ul li.${pageId}`).classList.add('on');

    // 현재 페이지 상태 업데이트
    currentPage = pageId;
}

// 페이지 로드 시 초기 설정
document.addEventListener("DOMContentLoaded", function() {
    // 초기 페이지 설정
    setNew(currentPage);

    // 사이드바 메뉴 클릭 이벤트 설정
    document.querySelectorAll('.side_bar ul li').forEach(li => {
        li.addEventListener('click', function() {
            const pageId = this.classList[0];
            setNew(pageId);
        });
    });
});

