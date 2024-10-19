document.addEventListener('DOMContentLoaded', () => {
    const submitBtn = document.getElementById('submit');

    submitBtn.addEventListener('click', async () => {
        // 카테고리 체크박스에서 선택된 항목들 가져오기
        const selectedCategories = Array.from(document.querySelectorAll('input[name="category"]:checked'))
                                        .map(checkbox => checkbox.id);

        // 선택된 레벨 가져오기
        const selectedLevel = document.querySelector('input[name="level"]:checked')?.id;

        // 선택된 카테고리 확인
        if (selectedCategories.length === 0) {
            alert('카테고리를 하나 이상 선택해주세요.');
            return;
        }

        // 선택된 레벨 확인
        if (!selectedLevel) {
            alert('레벨을 선택해주세요.');
            return;
        }

        // 레벨을 숫자로 변환
        let levelNumber;
        switch (selectedLevel) {
            case 'high':
                levelNumber = 3;
                break;
            case 'medium':
                levelNumber = 2;
                break;
            case 'low':
                levelNumber = 1;
                break;
            default:
                alert('올바른 레벨을 선택해주세요.');
                return;
        }

        // 데이터 객체 구성
        const data = {
            level: levelNumber,
            category: selectedCategories
        };
        const sessionId = localStorage.getItem('sessionId');

        try {
            // 서버로 PUT 요청 보내기
            const response = await fetch('/signup/info', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'sessionId' : sessionId
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                window.location.href = '/MAIN/main.html'; // 성공 시 페이지 이동
            } else {
                const result = await response.json();
                alert(result.message);  // 서버에서 제공하는 오류 메시지 표시
            }
        } catch (error) {
            console.error('Error:', error);
            alert('오류가 발생했습니다.');
        }
    });
});
