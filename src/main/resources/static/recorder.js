const startRecordButton = document.getElementById('startRecord');
const stopRecordButton = document.getElementById('stopRecord');
const submitButton = document.getElementById('submit');
const audioPlayback = document.getElementById('audioPlayback');

let mediaRecorder;
let audioChunks = [];
let wavBlob;

startRecordButton.addEventListener('click', () => {
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
                wavBlob = encodeWAV(buffer, 16000);

                audioChunks = [];
                const audioUrl = URL.createObjectURL(wavBlob);
                audioPlayback.src = audioUrl;

                startRecordButton.disabled = false;
                stopRecordButton.disabled = true;
                submitButton.disabled = false;
            };

            startRecordButton.disabled = true;
            stopRecordButton.disabled = false;
        });
});

stopRecordButton.addEventListener('click', () => {
    mediaRecorder.stop();
    startRecordButton.disabled = false;
    stopRecordButton.disabled = true;
    submitButton.disabled = false;
});

submitButton.addEventListener('click', () => {
    // base64로 변환 후 백엔드에 전송
    const reader = new FileReader();
    reader.readAsDataURL(wavBlob);
    reader.onloadend = () => {
        const base64AudioMessage = reader.result.split(',')[1];
        fetch('/learning/score', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ audio: base64AudioMessage })
        })
        .then(response => response.json())
        .then(data => {
            console.log('API response:', data);
            // "score.html"로 이동하면서 응답 데이터를 전달
//            const params = new URLSearchParams(data).toString();
//            window.location.href = `score.html?${params}`;
            alert('성공');
        })
        .catch(error => {
            console.error('Error:', error);
            alert('API 통신 중 에러가 발생했습니다.');
        });
    };
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
