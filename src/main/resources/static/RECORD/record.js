document.addEventListener("DOMContentLoaded", () => {
  let sentence;
  try {
    sentence = JSON.parse(localStorage.getItem("selectedSentence"));
  } catch (error) {
    console.error("Error parsing selectedSentence:", error);
  }

  if (!sentence) {
    alert("선택된 문장이 없습니다.");
    window.location.href = "../STUDY/study.html";
    return;
  }

  document.getElementById("english-sentence").textContent = sentence.en;
  document.getElementById("korean-sentence").textContent = sentence.ko;

  const listenBtn = document.getElementById("listen-btn");
  const recordBtn = document.getElementById("record-btn");
  const stopRecordBtn = document.getElementById("stop-record-btn");
  const nextBtn = document.getElementById("next-btn");
  const downloadLink = document.getElementById("download-link");

  let mediaRecorder;
  let audioChunks = [];

  function getVoiceByName(name) {
    const voices = speechSynthesis.getVoices();
    return voices.find((voice) => voice.name === name);
  }

  listenBtn.addEventListener("click", () => {
    const utterance = new SpeechSynthesisUtterance(sentence.en);
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

  recordBtn.addEventListener("click", async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaRecorder = new MediaRecorder(stream);
      mediaRecorder.start();

      audioChunks = []; // 녹음 시작 시 이전 녹음 데이터를 초기화

      mediaRecorder.addEventListener("dataavailable", (event) => {
        audioChunks.push(event.data);
      });

      recordBtn.disabled = true;
      stopRecordBtn.disabled = false;
    } catch (error) {
      console.error("Error accessing microphone:", error);
    }
  });

  stopRecordBtn.addEventListener("click", () => {
    if (mediaRecorder && mediaRecorder.state !== "inactive") {
      mediaRecorder.addEventListener(
        "stop",
        () => {
          const audioBlob = new Blob(audioChunks, { type: "audio/wav" });
          const audioUrl = URL.createObjectURL(audioBlob);

          const audioFileName = "recording.wav";

          // 오디오 URL과 파일명을 로컬 스토리지에 저장
          localStorage.setItem("audioUrl", audioUrl);
          localStorage.setItem("audioFileName", audioFileName);

          // 다운로드 링크 설정
          downloadLink.href = audioUrl;
          downloadLink.download = audioFileName;
          downloadLink.style.display = "block";

          recordBtn.disabled = false;
          stopRecordBtn.disabled = true;
        },
        { once: true }
      );

      mediaRecorder.stop();
    }
  });

  nextBtn.addEventListener("click", () => {
    window.location.href = "../SCORE/score.html";
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
});
