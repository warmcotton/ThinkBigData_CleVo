document.addEventListener('DOMContentLoaded', () => {
    const submitBtn = document.getElementById('submit');

    submitBtn.addEventListener('click', async () => {
        const selectedCategories = Array.from(document.querySelectorAll('input[name="category"]:checked'))
                                        .map(checkbox => checkbox.id);
        const selectedLevel = document.querySelector('input[name="level"]:checked')?.id;

        if (selectedCategories.length === 0) {
            alert('카테고리를 하나 이상 선택해주세요.');
            return;
        }

        if (!selectedLevel) {
            alert('레벨을 선택해주세요.');
            return;
        }

        const data = {
            categories: selectedCategories,
            level: selectedLevel
        };

        try {
            const response = await fetch('/submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                window.location.href = '../MAIN/main.html';
            } else {
                const result = await response.json();
                alert(result.message);  // 서버에서 제공하는 오류 메시지
            }
        } catch (error) {
            console.error('Error:', error);
            alert('오류가 발생했습니다.');
        }
    });
});
