document.addEventListener("DOMContentLoaded", () => {
  const sentence = JSON.parse(localStorage.getItem("selectedSentence"));
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

  let mediaRecorder;
  let audioChunks = [];

  listenBtn.addEventListener("click", () => {
    const utterance = new SpeechSynthesisUtterance(sentence.en);
    speechSynthesis.speak(utterance);
  });

  recordBtn.addEventListener("click", async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    mediaRecorder = new MediaRecorder(stream);
    mediaRecorder.start();

    audioChunks = []; // 녹음 시작 시 이전 녹음 데이터를 초기화

    mediaRecorder.addEventListener("dataavailable", (event) => {
      audioChunks.push(event.data);
    });

    recordBtn.disabled = true;
    stopRecordBtn.disabled = false;
  });

  stopRecordBtn.addEventListener("click", () => {
    mediaRecorder.stop();

    //stop 버튼을 누르면 로컬에 오디오 파일이 저장되게 함
    mediaRecorder.addEventListener("stop", () => {
      const audioBlob = new Blob(audioChunks, { type: "audio/wav" });
      const audioUrl = URL.createObjectURL(audioBlob);

      const audioFileName = "recording.wav";

      // 오디오 URL과 파일명을 로컬 스토리지에 저장
      localStorage.setItem("audioUrl", audioUrl);
      localStorage.setItem("audioFileName", audioFileName);

      recordBtn.disabled = false;
      stopRecordBtn.disabled = true;
    });
  });

  nextBtn2.addEventListener("click", () => {
    window.location.href = "../SCORE/rescore.html";
  });
});
