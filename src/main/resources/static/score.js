document.addEventListener('DOMContentLoaded', () => {
    const scoreField1 = document.getElementById('score1');
    const scoreField2 = document.getElementById('score2');

    // URL에서 점수 추출
    const params = new URLSearchParams(window.location.search);
    const originalScore = parseFloat(params.get('score'));

    if (originalScore) {
        const adjustedScore1 = (originalScore + getRandomAdjustment()).toFixed(1);
        const adjustedScore2 = (originalScore + getRandomAdjustment()).toFixed(1);

        scoreField1.value = adjustedScore1;
        scoreField2.value = adjustedScore2;
    } else {
        scoreField1.value = 'No score available';
        scoreField2.value = 'No score available';
    }
});

function getRandomAdjustment() {
    const randomValue = Math.random() * (1.0 - 0.1) + 0.1; // 0.1 ~ 1.0 사이의 난수 생성
    return Math.random() < 0.5 ? randomValue : -randomValue; // 50% 확률로 더하거나 빼기
}
