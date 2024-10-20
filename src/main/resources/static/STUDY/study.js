document.addEventListener("DOMContentLoaded", () => {
  const sentences = [
    {
      en: "Sentence 1: This is a test sentence.",
      ko: "문장 1: 이것은 테스트 문장입니다.",
    },
    {
      en: "Sentence 2: Add your sentence here.",
      ko: "문장 2: 여기에 문장을 추가하세요.",
    },
    {
      en: "Sentence 3: This sentence is a placeholder.",
      ko: "문장 3: 이 문장은 자리 표시자입니다.",
    },
    {
      en: "Sentence 4: Used for learning purposes.",
      ko: "문장 4: 학습 목적을 위해 사용됩니다.",
    },
    {
      en: "Sentence 5: Randomly selected sentence.",
      ko: "문장 5: 무작위로 선택된 문장입니다.",
    },
    {
      en: "Sentence 6: Randomly selected sentence.",
      ko: "문장 6: 무작위로 선택된 문장입니다.",
    },
    {
      en: "Sentence 7: Randomly selected sentence.",
      ko: "문장 7: 무작위로 선택된 문장입니다.",
    },
    {
      en: "Sentence 8: Randomly selected sentence.",
      ko: "문장 8: 무작위로 선택된 문장입니다.",
    },
    {
      en: "Sentence 9: Randomly selected sentence.",
      ko: "문장 9: 무작위로 선택된 문장입니다.",
    },
    {
      en: "Sentence 10: Randomly selected sentence.",
      ko: "문장 10: 무작위로 선택된 문장입니다.",
    },
    // ... more sentences
  ];

  const sentenceList = document.getElementById("sentence-list");
  const generateBtn = document.getElementById("generate-btn");

  function generateRandomSentences() {
    sentenceList.innerHTML = "";

    const shuffledSentences = sentences.sort(() => 0.5 - Math.random());
    const selectedSentences = shuffledSentences.slice(0, 5);

    selectedSentences.forEach((sentence) => {
      const li = document.createElement("li");
      li.textContent = sentence.en;
      li.addEventListener("click", () => {
        localStorage.setItem("selectedSentence", JSON.stringify(sentence));
        window.location.href = "../RECORD/record.html";
      });
      sentenceList.appendChild(li);
    });
  }

  generateBtn.addEventListener("click", generateRandomSentences);
  generateRandomSentences();
});
