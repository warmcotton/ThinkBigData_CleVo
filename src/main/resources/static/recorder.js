const startRecordButton = document.getElementById('startRecord');
const stopRecordButton = document.getElementById('stopRecord');
const audioPlayback = document.getElementById('audioPlayback');

let mediaRecorder;
let audioChunks = [];

startRecordButton.addEventListener('click', () => {
    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(stream => {
            mediaRecorder = new MediaRecorder(stream);
            mediaRecorder.start();

            mediaRecorder.addEventListener('dataavailable', event => {
                audioChunks.push(event.data);
            });

            mediaRecorder.addEventListener('stop', () => {
                const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
                audioChunks = [];
                const audioUrl = URL.createObjectURL(audioBlob);
                audioPlayback.src = audioUrl;

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
                        // 필요한 경우 data를 HTML 요소에 추가하여 표시할 수 있습니다.
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
