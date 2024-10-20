document.addEventListener("DOMContentLoaded", () => {

    let englishSentence;
    let koreanSentence;
    const accessToken = localStorage.getItem("accessToken");
    let accuracy;
    let fluency;
    let sentence_id;

      try {
        englishSentence = localStorage.getItem("selectedSentenceEng");
        koreanSentence = localStorage.getItem("selectedSentenceKor");
      } catch (error) {
        console.error("Error retrieving sentences from localStorage:", error);
      }

        try {
          accuracy = localStorage.getItem("accuracy");
          fluency = localStorage.getItem("fluency");
          sentence_id = localStorage.getItem("sentence_id");

        } catch (error) {
          console.error("Error retrieving score data from localStorage:", error);
        }


      if (!englishSentence || !koreanSentence) {
        alert("선택된 문장이 없습니다.");
        window.location.href = "/STUDY/study.html";
        return;
      }

      document.getElementById("english-sentence").textContent = englishSentence;
      document.getElementById("korean-sentence").textContent = koreanSentence;

      const listenBtn = document.getElementById("listen-btn");
      const studyBtn = document.getElementById("study-btn");
      const reRecordBtn = document.getElementById("rerecord-btn");
      const storeBtn = document.getElementById("store-btn");
      const stopBtn = document.getElementById("stop-btn");

    const fluencyScoreElement = document.getElementById("fluency-score");
    const accuracyScoreElement = document.getElementById("accuracy-score");
    const totalScoreElement = document.getElementById("total-score");

    if (accuracy && fluency) {
      accuracyScoreElement.textContent = parseFloat(accuracy).toFixed(1);
      fluencyScoreElement.textContent = parseFloat(fluency).toFixed(1);
      const totalScore = ((parseFloat(accuracy) + parseFloat(fluency)) / 2).toFixed(1);
      totalScoreElement.textContent = totalScore;
    }


  function getVoiceByName(name) {
    const voices = speechSynthesis.getVoices();
    return voices.find((voice) => voice.name === name);
  }

  listenBtn.addEventListener("click", () => {
    const utterance = new SpeechSynthesisUtterance(englishSentence);
    // 원어민처럼 들리는 영어 음성 선택 (예: Google UK English Female)
    const voice =
      getVoiceByName("Google UK English Female") ||
      getVoiceByName("Google US English");
    if (voice) {
      utterance.voice = voice;
    }
    utterance.rate = 0.9; // 발음 속도 조정
    utterance.pitch = 1.0; // 발음 높낮이 조정
    speechSynthesis.speak(utterance);
  });

  // 음성 목록을 로드하는 함수
  function loadVoices() {
    return new Promise((resolve) => {
      const voices = speechSynthesis.getVoices();
      if (voices.length !== 0) {
        resolve(voices);
      } else {
        speechSynthesis.addEventListener("voiceschanged", () => {
          resolve(speechSynthesis.getVoices());
        });
      }
    });
  }

      // 페이지 로드 시 음성 목록을 미리 로드
      loadVoices();

       // studyBtn 클릭 시
        studyBtn.addEventListener("click", () => {
          window.location.href = "/STUDY/study.html";
        });

        // reRecordBtn 클릭 시
        reRecordBtn.addEventListener("click", () => {
          window.location.href = "/RECORD/rerecord.html";
        });

        // storeBtn 클릭 시
        storeBtn.addEventListener("click", async () => {
          if (!accessToken || !sentence_id) {
            alert("저장할 수 없습니다. 필수 정보가 없습니다.");
            return;
          }
          try {
            const response = await fetch("/sentence/user", {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`,
              },
              body: JSON.stringify({ sentence_id }),
            });

            if (!response.ok) {
              throw new Error("Error storing sentence");
            }
            alert("문장이 성공적으로 저장되었습니다.");
          } catch (error) {
            console.error("Error storing sentence:", error);
            alert("문장을 저장하는 도중 오류가 발생했습니다.");
          }
        });

        // stopBtn 클릭 시
        stopBtn.addEventListener("click", () => {
          localStorage.removeItem("sentence_id");
          window.location.href = "/MAIN/main.html";
        });
});
