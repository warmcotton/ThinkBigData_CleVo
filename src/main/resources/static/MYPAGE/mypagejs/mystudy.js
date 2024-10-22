let userData = {
    username: '',
    greeting: '안녕하세요!',
    recentSentence: {
        text: '',
        score: 0
    },
    reviewSentences: [],
    progressRate: 0,
    dailyGoal: 5,
};
const accessToken = localStorage.getItem('accessToken');

async function fetchDashboardData() {
    try {
        const response = await fetch('/dashboard', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${accessToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to fetch data from server');
        }

        const data = await response.json();
        updateUserData(data);
        populateMyStudy();
    } catch (error) {
        console.error('Error fetching dashboard data:', error);
    }
}

function updateUserData(data) {
    // Update userData object with fetched data
    userData.username = data.user.nickname;

    // Update recent sentence with the most recent learning log's data
    if (data.learning_logs.content.length > 0) {
        const latestLog = data.learning_logs.content[0];
        userData.recentSentence = {
            text: latestLog.sentenceDto.eng,
            score: Math.round(latestLog.total_score * 100) / 100 // Rounded to two decimal places
        };
    }

    // Update review sentences with sentences from user_sentences
    userData.reviewSentences = data.user_sentences.content.length > 0 ? data.user_sentences.content.map(sentence => ({
        text: sentence.sentence.eng,
        score: null // No score provided in user_sentences, so setting it as null
    })) : [];

    // Update daily goal from user data
    userData.dailyGoal = data.user.target;

    // Calculate progress rate based on today's learning logs
    const today = new Date(new Date().toLocaleString('en-US', { timeZone: 'Asia/Seoul' })).toLocaleDateString('en-CA');
    let todayCount = 0;
    if (data.learning_logs.content.length > 0) {
        for (let log of data.learning_logs.content) {
            const logDate = log.date.split('T')[0]; // Extract date part from log's date
            if (logDate === today) {
                todayCount++;
            } else {
                break; // Since data is sorted by latest, we can stop once we encounter a non-today date
            }
        }
    }

    // Calculate progress rate as a percentage
    userData.progressRate = userData.dailyGoal ? Math.round((todayCount / userData.dailyGoal) * 100) : 0;
}

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
                            <button class="delete-btn" onclick="deleteSentence(${index})">&times;</button>
                        </li>
                    `).join('')}
                </ul>
            </div>
        </div>
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

    // Re-render the chart
    renderProgressChart();
}

function renderProgressChart() {
    const ctx = document.getElementById('progressChart').getContext('2d');
    const progressRate = userData.progressRate;

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['진도율', '남은 부분'],
            datasets: [{
                label: '학습 진도율',
                data: [progressRate, 100 - progressRate],
                backgroundColor: ['#6B8E23', '#e0e0e0'],
                borderWidth: 0,
            }]
        },
        options: {
            cutout: '70%',
            responsive: true,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    enabled: false
                }
            }
        },
        plugins: [{
            id: 'textCenter',
            beforeDraw: function(chart) {
                const width = chart.width,
                    height = chart.height,
                    ctx = chart.ctx;
                ctx.restore();
                const fontSize = (height / 8).toFixed(2);
                ctx.font = `bold ${fontSize}px sans-serif`;
                ctx.textBaseline = "middle";
                ctx.fillStyle = '#333';
                const text = progressRate + "%",
                    textX = Math.round((width - ctx.measureText(text).width) / 2),
                    textY = height / 2;
                ctx.fillText(text, textX, textY);
                ctx.save();
            }
        }]
    });
}
let tempGoal = userData.dailyGoal; // 목표 학습량을 변경하기 위한 임시 변수

// Page load actions
document.addEventListener("DOMContentLoaded", () => {
    fetchDashboardData().then(() => {
        tempGoal = userData.dailyGoal; // fetchDashboardData 완료 후 목표 학습량으로 초기화
        populateMyStudy();  // 화면 업데이트
    });
});

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

    // API 호출을 통해 목표 업데이트 (PUT 요청)
    fetch('/user-target', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },
        body: JSON.stringify({
            target: userData.dailyGoal
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('목표 업데이트 실패');
        }
        return response.json();
    })
    .then(data => {
        alert(`새로운 목표: ${userData.dailyGoal} 문장`);
        window.location.reload(); // 성공 시 페이지 새로고침
    })
    .catch(error => {
        console.error('API 호출 중 오류 발생:', error);
        alert('목표 업데이트 중 문제가 발생했습니다. 다시 시도해주세요.');
    });
}

// 페이지 로드 시 populateMyStudy 호출
document.addEventListener("DOMContentLoaded", populateMyStudy);

function deleteSentence(index) {
    userData.reviewSentences.splice(index, 1);  // 선택된 문장을 배열에서 삭제
    populateMyStudy();  // 화면을 다시 렌더링
    }