document.addEventListener("DOMContentLoaded", function() {
    const settingContainer = document.getElementById('setting');

    settingContainer.innerHTML = `
        <!-- 회원정보 변경 섹션 -->
        <div class="setting-section">
            <h3> 회원정보변경</h3>
            <div class="setting-container">
                <div class="password-confirmation">
                    <h4>개인정보 수정을 위해서 비밀번호를 입력해 주세요.</h4>
                    <input type="password" id="password" placeholder="비밀번호 입력">
                    <button class="password-confirm-btn">확인</button>
                </div>
            </div>
        </div>

        <!-- 맞춤형 설정 섹션 -->
        <div class="setting-section">
            <h3>맞춤형 설정</h3>
            <div class="setting-container">
                <div class="container-select" id="select__category">
                    <h4>학습을 원하는 카테고리를 모두 골라주세요.</h4>
                    <div class="content">
                        <section>
                            <input type='checkbox' id='hobby' /><label for='hobby' class="category-btn">취미생활</label>
                            <input type='checkbox' id='business' /><label for='business' class="category-btn">비즈니스</label>
                            <input type='checkbox' id='travel' /><label for='travel' class="category-btn">해외여행</label>
                            <input type='checkbox' id='everyday' /><label for='everyday' class="category-btn">일상생활</label>
                            <input type='checkbox' id='shopping' /><label for='shopping' class="category-btn">쇼핑</label>
                        </section>
                    </div>
                </div>
                <div class="container-select" id="select__level">
                    <h4>학습을 원하는 레벨을 골라주세요.</h4>
                    <div class="content" id="select__level">
                        <section>
                            <input type='radio' name='level' id='high' /><label for='high' class="level-btn">상</label>
                            <input type='radio' name='level' id='medium' /><label for='medium' class="level-btn">중</label>
                            <input type='radio' name='level' id='low' /><label for='low' class="level-btn">하</label>
                        </section>
                    </div>
                </div>
                <button id="submit">저장하기</button>
            </div>
        </div>
    `;

    document.querySelector('.password-confirm-btn').addEventListener('click', () => {
        const password = document.getElementById('password').value;
    
        if (password) {
            // 비밀번호가 입력되면 settingin.html 파일로 페이지 이동
            window.location.href = 'settingin.html';  // 상위 폴더로 이동 후 settingin.html 파일로 이동
        } else {
            alert("비밀번호를 입력해주세요.");
        }
    });
    


    // 체크박스와 라디오 버튼에 대한 이벤트 리스너
    document.querySelectorAll('input[type="checkbox"], input[type="radio"]').forEach(input => {
        input.addEventListener('change', (event) => {
            const label = document.querySelector(`label[for="${input.id}"]`);

            if (input.type === 'checkbox') {
                if (input.checked) {
                    label.classList.add('active');
                } else {
                    label.classList.remove('active');
                }
            } else if (input.type === 'radio') {
                document.querySelectorAll('input[type="radio"] + label').forEach(l => l.classList.remove('active'));
                label.classList.add('active');
            }
        });
    });

    // 저장 버튼 클릭 시 선택된 카테고리와 레벨 검증
    document.getElementById('submit').addEventListener('click', () => {
        const selectedCategories = [];
        document.querySelectorAll('input[type="checkbox"]:checked').forEach((checkbox) => {
            selectedCategories.push(checkbox.id);
        });

        const selectedLevel = document.querySelector('input[type="radio"]:checked')?.id || '';

        if (selectedCategories.length === 0) {
            alert("카테고리를 하나 이상 선택해주세요.");
            return;
        }
        
        if (!selectedLevel) {
            alert("레벨을 하나 선택해주세요.");
            return;
        }

        alert(`선택된 카테고리: ${selectedCategories.join(', ')}\n선택된 레벨: ${selectedLevel}`);
    });
});
