// 메모 데이터를 저장하는 배열 (localStorage에서 로드)
let memoList = JSON.parse(localStorage.getItem('memos')) || [
    { text: "메모 1: 중요한 일정을 기록하세요." },
    { text: "메모 2: 해야 할 일 목록을 작성하세요." },
    { text: "메모 3: 학습 목표를 정리하세요." }
];

// 메모장 페이지에 내용을 삽입하는 함수
function loadMemoContent() {
    const memoContainer = document.getElementById('memo');
    memoContainer.innerHTML = `
        <h2>메모장</h2>
        <div id="memoInputContainer">
            <textarea id="newMemo" placeholder="새 메모 입력"></textarea>
            <button id="addMemoBtn" onclick="addMemo()">+</button>
        </div>
        <ul id="memoList">
            ${memoList.map((memo, index) => `
                <li>
                    ${memo.text}
                    <button class="delete-btn" onclick="deleteMemo(${index})">삭제</button>
                </li>
            `).join('')}
        </ul>
    `;
    document.getElementById('newMemo').addEventListener('input', autoResizeTextarea); // 입력 시 높이 자동 조절
}

// textarea의 높이를 입력 내용에 따라 자동으로 조절하는 함수
function autoResizeTextarea() {
    const textarea = document.getElementById("newMemo");
    textarea.style.height = "auto";  // 높이를 먼저 초기화한 후
    textarea.style.height = (textarea.scrollHeight) + "px";  // 입력 내용에 맞춰 높이를 다시 설정
}

// 메모 추가 함수
function addMemo() {
    const newMemoInput = document.getElementById('newMemo');
    const newMemoText = newMemoInput.value.trim();

    if (newMemoText !== "") {
        // 메모 리스트에 추가
        memoList.push({ text: newMemoText });
        // 입력 필드 초기화
        newMemoInput.value = "";
        newMemoInput.style.height = "auto"; // 입력 필드 높이 초기화
        // LocalStorage에 저장
        localStorage.setItem('memos', JSON.stringify(memoList));
        // 화면 업데이트
        loadMemoContent();
    } else {
        alert("메모를 입력하세요.");
    }
}

// 메모 삭제 함수
function deleteMemo(index) {
    // 선택된 메모를 배열에서 삭제
    memoList.splice(index, 1);
    // LocalStorage에 저장
    localStorage.setItem('memos', JSON.stringify(memoList));
    // 화면 업데이트
    loadMemoContent();
}

// 페이지 로드 시 메모장 내용 로드
document.addEventListener("DOMContentLoaded", function() {
    if (document.getElementById('memo')) {
        loadMemoContent();
    }
});
