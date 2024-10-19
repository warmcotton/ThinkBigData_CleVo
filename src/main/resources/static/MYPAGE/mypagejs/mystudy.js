const userData = {
    username: "Clevo12",
    greeting: "안녕하세요!",
    recentSentence: {
        text: "Laughing out loud makes me slow down when I'm in mental burden.",
        score: 95
    },
    reviewSentences: [
        { text: "Laughing out loud makes me slow down when I'm in mental burden.", score: 32 },
        { text: "Laughing out loud makes me slow down when I'm in mental burden.", score: 43 },
        { text: "Laughing out loud makes me slow down when I'm in mental burden.", score: 76 },
        { text: "Laughing out loud makes me slow down when I'm in mental burden.", score: 87 },
        { text: "Laughing out loud makes me slow down when I'm in mental burden.", score: 25 }
    ],
    progressRate: 80,
    dailyGoal: 5,
};

function populateMyStudy() {
    
    const container = document.getElementById('mystudy');
    container.innerHTML = `
        <div class="profile-info-container">
            <h3>프로필 정보</h3>
            <div class="profile-info box-section">
                <span>'${userData.username}'님 ${userData.greeting}</span>
            </div>
        </div>
        <div class="recent-sentence-container">
            <h3>최근 학습 문장</h3>
            <div class="recent-sentence box-section">
                <span>${userData.recentSentence.text}</span>
                <span class="sentence-score">${userData.recentSentence.score}</span>
            </div>
        </div>
        <div class="review-sentences-container">
            <h3>보관 문장</h3>
            <div class="review-sentences box-section">
                <ul>
                    ${userData.reviewSentences.map((sentence, index) => `
                        <li class="review-sentence-item">
                            <span>${sentence.text}</span> 
                            <span class="sentence-score">${sentence.score}</span>
                            <button class="delete-btn" onclick="deleteSentence(${index})">&times;</button>
                        </li>
                    `).join('')}
                </ul>
            </div>
        </div>
        <!-- 학습진도율과 일일 학습 목표를 같은 줄에 배치하기 위해 추가된 컨테이너 -->
        <div class="progress-goal-container">
            <div class="learning-progress-container">
                <h3>학습 진도율</h3>
                <div class="learning-progress box-section">
                    <canvas id="progressChart"></canvas>
                    <h5>현재 학습진도율은 <span>${userData.progressRate}%</span>입니다!</h5>
                </div>
            </div>
            <div class="daily-goal-container">
                <h3>일일 학습 목표</h3>
                <div class="daily-goal box-section">
                    <span>현재 목표 학습량은 <span class="goal-count">${userData.dailyGoal}</span>개 입니다.</span>
                    <h6></h6><br>일일 학습량을 목표까지 달성해보세요.<br>학습 목표는 언제든 변경 가능합니다.</br>
                    <div class="goal-controls">
                        <button class="goal-btn" onclick="adjustGoal(-1)">-</button>
                        <span class="goal-display">${userData.dailyGoal}</span>
                        <button class="goal-btn" onclick="adjustGoal(1)">+</button>
                    </div>
                    <div class="apply-btn-container">
                        <button class="apply-btn" onclick="applyGoal()">저장하기</button>
                    </div>
                </div>
            </div>

        </div>
    `;
}


// 보관 문장 삭제 함수
function deleteSentence(index) {
    userData.reviewSentences.splice(index, 1);  // 선택된 문장을 배열에서 삭제
    populateMyStudy();  // 화면을 다시 렌더링
}

let tempGoal = userData.dailyGoal; // 목표 학습량을 변경하기 위한 임시 변수

// Adjust daily goal (컨트롤러 부분만 업데이트)
function adjustGoal(change) {
    tempGoal += change;
    
    if (tempGoal < 5) { // 최소 갯수 5개로 설정
        tempGoal = 5;
        alert('최소 학습량은 5개입니다.')
    }

    // 컨트롤러에서 숫자 부분만 업데이트
    const goalDisplay = document.querySelector('.goal-display');
    if (goalDisplay) {
        goalDisplay.textContent = tempGoal; // 목표 숫자 업데이트
    }
}

// Apply new goal (저장하기 버튼을 눌렀을 때 최종 저장)
function applyGoal() {
    userData.dailyGoal = tempGoal; // 임시 값을 실제 값으로 반영
    
    const goalCount = document.querySelector('.goal-count'); // 상단에 표시된 목표 숫자
    if (goalCount) {
        goalCount.textContent = userData.dailyGoal; // 문장에 표시된 목표 업데이트
    }

    alert(`새로운 목표: ${userData.dailyGoal} 문장`);
}

// 페이지 로드 시 populateMyStudy 호출
document.addEventListener("DOMContentLoaded", populateMyStudy);



// 페이지 로드 후 차트를 그립니다
document.addEventListener("DOMContentLoaded", function() {
    const ctx = document.getElementById('progressChart').getContext('2d');
    const progressRate = userData.progressRate; // userData에서 진도율 가져오기

    const progressChart = new Chart(ctx, {
        type: 'doughnut', // 원형 도넛 차트
        data: {
            labels: ['진도율', '남은 부분'],
            datasets: [{
                label: '학습 진도율',
                data: [progressRate, 100 - progressRate], // 실제 진도율과 나머지 부분
                backgroundColor: ['#6B8E23', '#e0e0e0'], // 색상: 진도율, 나머지
                borderWidth: 0, // 경계선 없애기
            }]
        },
        options: {
            cutout: '70%', // 도넛 가운데 부분 비우기
            responsive: true,
            plugins: {
                legend: {
                    display: false // 범례 숨기기
                },
                tooltip: {
                    enabled: false // 툴팁 비활성화
                }
            }
        },
        plugins: [{
            id: 'textCenter',  // 플러그인 ID
            beforeDraw: function(chart) {
                const width = chart.width,
                    height = chart.height,
                    ctx = chart.ctx;
                ctx.restore();
                
                // 텍스트 스타일 지정 (크기, 두께, 폰트 설정)
                const fontSize = (height /8).toFixed(2); // 글씨 크기 줄이기
                ctx.font = `bold ${fontSize}px sans-serif`; // 두꺼운 글씨로 설정
                ctx.textBaseline = "middle";
                ctx.fillStyle = '#333'; // 글씨 색상 설정

                const text = progressRate + "%",  // 표시할 퍼센트 값
                    textX = Math.round((width - ctx.measureText(text).width) / 2),
                    textY = height / 2;

                ctx.fillText(text, textX, textY);  // 차트 중앙에 퍼센트 표시
                ctx.save();
            }
        }]
    });
});

