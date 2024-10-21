let userData = {
    username: '',
    greeting: '안녕하세요!',
    recentSentence: {
        text: '',
        score: 0
    },
    reviewSentences: [],
    progressRate: 80,
    dailyGoal: 5,
};

async function fetchDashboardData() {
    const accessToken = localStorage.getItem('accessToken');
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
    const latestLog = data.learning_logs.content[0];
    userData.recentSentence = {
        text: latestLog.sentenceDto.eng,
        score: Math.round(latestLog.total_score * 100) / 100 // Rounded to two decimal places
    };

    // Update review sentences with sentences from user_sentences
    userData.reviewSentences = data.user_sentences.content.map(sentence => ({
        text: sentence.sentence.eng,
        score: null // No score provided in user_sentences, so setting it as null
    }));

    // Update daily goal from user data
    userData.dailyGoal = data.user.target;
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

// Page load actions
document.addEventListener("DOMContentLoaded", fetchDashboardData);
