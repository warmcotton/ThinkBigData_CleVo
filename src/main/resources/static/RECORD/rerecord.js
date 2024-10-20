document.addEventListener("DOMContentLoaded", () => {
    let englishSentence;
    let koreanSentence;
    const accessToken = localStorage.getItem("accessToken");
    const sentence_id = localStorage.getItem("sentence_id");

  try {
    englishSentence = localStorage.getItem("selectedSentenceEng");
    koreanSentence = localStorage.getItem("selectedSentenceKor");
  } catch (error) {
    console.error("Error retrieving sentences from localStorage:", error);
  }


  if (!englishSentence || !koreanSentence) {
    alert("선택된 문장이 없습니다.");
    window.location.href = "/STUDY/study.html";
    return;
  }

  document.getElementById("english-sentence").textContent = englishSentence;
  document.getElementById("korean-sentence").textContent = koreanSentence;

  const listenBtn = document.getElementById("listen-btn");
  const recordBtn = document.getElementById("record-btn");
  const stopRecordBtn = document.getElementById("stop-record-btn");
  const nextBtn = document.getElementById("next-btn");
  const downloadButton = document.getElementById("download-btn");


  let mediaRecorder;
  let audioChunks = [];
  let audioBlob;

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

  recordBtn.addEventListener('click', () => {
      navigator.mediaDevices.getUserMedia({ audio: true })
          .then(stream => {
              const audioContext = new AudioContext({ sampleRate: 16000 });
              const source = audioContext.createMediaStreamSource(stream);
              const processor = audioContext.createScriptProcessor(4096, 1, 1);

              source.connect(processor);
              processor.connect(audioContext.destination);

              processor.onaudioprocess = (event) => {
                  if (mediaRecorder && mediaRecorder.state === 'recording') {
                      audioChunks.push(new Float32Array(event.inputBuffer.getChannelData(0)));
                  }
              };

              mediaRecorder = new MediaRecorder(stream);
              mediaRecorder.start();

              mediaRecorder.onstop = () => {
                  processor.disconnect();
                  audioContext.close();

                  // Float32Array chunks를 Blob으로 변환
                  const buffer = flattenArray(audioChunks);
                  audioBlob = encodeWAV(buffer, 16000);

                  audioChunks = [];
                  const audioUrl = URL.createObjectURL(audioBlob);

                  recordBtn.disabled = false;
                  stopRecordBtn.disabled = true;
                  nextBtn.disabled = false;
                  downloadButton.disabled = false;
              };

              recordBtn.disabled = true;
              stopRecordBtn.disabled = false;
          });
  });

  stopRecordBtn.addEventListener('click', () => {
      mediaRecorder.stop();
      recordBtn.disabled = false;
      stopRecordBtn.disabled = true;
      nextBtn.disabled = false;
      downloadButton.disabled = false;
  });

  nextBtn.addEventListener("click", async () => {
      const reader = new FileReader();
      reader.readAsDataURL(audioBlob);
      reader.onloadend = async () => {
       if (!audioBlob) {
              console.error("Audio blob is not available");
              return;
            }
        const base64 = reader.result.split(',')[1];
        console.log(base64);
        console.log(englishSentence);
        if (!accessToken) {
                console.error("Access token is missing");
                return;
              }
      try {
        const response = await fetch("/learning/user-sentence/score", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`
          },
          body: JSON.stringify({
            sentence_id: sentence_id,
            base64: base64
          })
        });

        if (!response.ok) {
          throw new Error("API request failed with status " + response.status);
        }

        const data = await response.json();
        console.log("API response:", data);
        const { sentence_id, accuracy, fluency } = data;

        localStorage.setItem("sentence_id", sentence_id);
        localStorage.setItem("accuracy", accuracy);
        localStorage.setItem("fluency", fluency);

        setTimeout(() => {
          window.location.href = "/SCORE/score.html";
        }, 100);
      } catch (error) {
        console.error("Error during API request:", error);
      }
    };
    });

  downloadButton.addEventListener('click', () => {
    // if (!wavBlob) {
    //   alert('녹음된 파일이 없습니다.');
    //   return;
    // }
    const audioUrl = URL.createObjectURL(audioBlob);
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = audioUrl;
    a.download = 'recording.wav';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
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

function flattenArray(channelBuffers) {
    let totalLength = channelBuffers.reduce((acc, buffer) => acc + buffer.length, 0);
    let result = new Float32Array(totalLength);
    let offset = 0;
    for (let buffer of channelBuffers) {
        result.set(buffer, offset);
        offset += buffer.length;
    }
    return result;
}

function encodeWAV(samples, sampleRate) {
    const buffer = new ArrayBuffer(44 + samples.length * 2);
    const view = new DataView(buffer);

    /* RIFF identifier */
    writeString(view, 0, 'RIFF');
    /* RIFF chunk length */
    view.setUint32(4, 36 + samples.length * 2, true);
    /* RIFF type */
    writeString(view, 8, 'WAVE');
    /* format chunk identifier */
    writeString(view, 12, 'fmt ');
    /* format chunk length */
    view.setUint32(16, 16, true);
    /* sample format (raw) */
    view.setUint16(20, 1, true);
    /* channel count */
    view.setUint16(22, 1, true);
    /* sample rate */
    view.setUint32(24, sampleRate, true);
    /* byte rate (sample rate * block align) */
    view.setUint32(28, sampleRate * 2, true);
    /* block align (channel count * bytes per sample) */
    view.setUint16(32, 2, true);
    /* bits per sample */
    view.setUint16(34, 16, true);
    /* data chunk identifier */
    writeString(view, 36, 'data');
    /* data chunk length */
    view.setUint32(40, samples.length * 2, true);

    /* PCM samples */
    floatTo16BitPCM(view, 44, samples);

    return new Blob([view], { type: 'audio/wav' });
}

function floatTo16BitPCM(output, offset, input) {
    for (let i = 0; i < input.length; i++, offset += 2) {
        let s = Math.max(-1, Math.min(1, input[i]));
        output.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
    }
}

function writeString(view, offset, string) {
    for (let i = 0; i < string.length; i++) {
        view.setUint8(offset + i, string.charCodeAt(i));
    }
}
