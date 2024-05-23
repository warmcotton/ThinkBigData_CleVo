const startRecordButton = document.getElementById('startRecord');
const stopRecordButton = document.getElementById('stopRecord');
const audioPlayback = document.getElementById('audioPlayback');

let mediaRecorder;
let audioChunks = [];

startRecordButton.addEventListener('click', () => {
    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(stream => {
            // 16kHz로 녹음
            const audioContext = new AudioContext({ sampleRate: 16000 });
            const source = audioContext.createMediaStreamSource(stream);
            const processor = audioContext.createScriptProcessor(4096, 1, 1);

            source.connect(processor);
            processor.connect(audioContext.destination);

            processor.onaudioprocess = (event) => {
                if (mediaRecorder.state === 'recording') {
                    audioChunks.push(event.inputBuffer.getChannelData(0));
                }
            };

            mediaRecorder = new MediaRecorder(stream);
            mediaRecorder.start();

            mediaRecorder.addEventListener('dataavailable', event => {
                const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
                audioChunks = [];
                const audioUrl = URL.createObjectURL(audioBlob);
                audioPlayback.src = audioUrl;

                //base64로 변환 후 백엔드에 전송
                const reader = new FileReader();
                reader.readAsDataURL(audioBlob);
                reader.onloadend = () => {
                    const base64AudioMessage = reader.result.split(',')[1];
                    fetch('/api/upload-audio', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ audio: base64AudioMessage })
                    })
                    .then(response => response.json())
                    .then(data => {
                        console.log('API response:', data);
                        alert('API response received. Check console for details.');
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Error occurred while processing the API response.');
                    });
                };
            });

            startRecordButton.disabled = true;
            stopRecordButton.disabled = false;
        });
});

stopRecordButton.addEventListener('click', () => {
    mediaRecorder.stop();
    startRecordButton.disabled = false;
    stopRecordButton.disabled = true;
});
