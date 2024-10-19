// 탈퇴 버튼을 클릭하면 모달이 나타남
document.querySelector('.cancel-btn').addEventListener('click', function() {
    document.getElementById('deleteModal').style.display = 'block';
});

// X 버튼을 클릭하면 모달이 사라짐
document.querySelector('.close-x').addEventListener('click', function() {
    document.getElementById('deleteModal').style.display = 'none';
});

// 탈퇴하기 버튼 클릭 시 처리 로직 (필요에 따라 추가 가능)
document.getElementById('confirmDelete').addEventListener('click', function() {
    const password = document.querySelector('.modal-content input[type="password"]').value;
    if (password) {
        alert('탈퇴가 완료되었습니다.'); // 실제 탈퇴 처리 로직 추가
        document.getElementById('deleteModal').style.display = 'none';
    } else {
        alert('비밀번호를 입력해주세요.');
    }
});
