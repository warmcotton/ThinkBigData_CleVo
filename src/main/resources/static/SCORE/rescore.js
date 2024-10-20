document.addEventListener("DOMContentLoaded", () => {
  const sentence = JSON.parse(localStorage.getItem("selectedSentence"));
  const audioUrl = localStorage.getItem("audioUrl");
  const audioFileName = localStorage.getItem("audioFileName");

  if (!sentence || !audioUrl || !audioFileName) {
    alert("필요한 정보가 부족합니다.");
    window.location.href = "../STUDY/study.html";
    return;
  }

  document.getElementById("english-sentence").textContent = sentence.en;
  document.getElementById("korean-sentence").textContent = sentence.ko;

  const listenBtn = document.getElementById("listen-btn");
  const fluencyScoreElement = document.getElementById("fluency-score");
  const accuracyScoreElement = document.getElementById("accuracy-score");
  const totalScoreElement = document.getElementById("total-score");

  listenBtn.addEventListener("click", () => {
    const audio = new Audio(audioUrl);
    audio.play();
  });

  function evaluateScores() {
    // 간단한 점수 평가 로직 (임의의 점수를 부여)
    const fluencyScore = Math.floor(Math.random() * 101); // 0부터 100 사이의 임의의 정수
    const accuracyScore = Math.floor(Math.random() * 101); // 0부터 100 사이의 임의의 정수
    const totalScore = (fluencyScore + accuracyScore) / 2;

    fluencyScoreElement.textContent = fluencyScore;
    accuracyScoreElement.textContent = accuracyScore;
    totalScoreElement.textContent = totalScore.toFixed(2);
  }

  // 오디오 블롭을 사용하여 점수를 평가
  fetch(audioUrl)
    .then((response) => response.blob())
    .then((blob) => {
      evaluateScores(blob);
    })
    .catch((error) => {
      console.error("오디오 파일 로드 중 오류 발생:", error);
    });
});
