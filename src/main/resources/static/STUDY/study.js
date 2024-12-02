const searchForm = document.querySelector('.header__search'); // 검색 form 선택자 추가
const searchInput = document.querySelector('.search_txt'); // 검색어 input 필드 선택자 추가

document.addEventListener("DOMContentLoaded", async () => {
  const sentenceList = document.getElementById("sentence-list");
  const generateBtn = document.getElementById("generate-btn");

  async function getUserData() {
      const userNameElement = document.getElementById("user_id");
      const accessToken = localStorage.getItem("accessToken");
      try {
        const response = await fetch("/user", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        if (!response.ok) throw new Error("Failed to fetch user data");
        const userData = await response.json();
        userNameElement.textContent = userData.nickname;
        return userData;
      } catch (error) {
        console.error("Error fetching user data:", error);
      }
  }

  // 본 소프트웨어는 ETRI의 ETRI Open API와 OpenAI의 ChatGPT API를 활용하여 데이터를 제공합니다.
  // Copyright © 2024 OpenAI & ETRI. All rights reserved.
  async function generateSentences(params) {
    const accessToken = localStorage.getItem("accessToken");

    try {
      const response = await fetch("https://53qa9adz8h.execute-api.ap-northeast-2.amazonaws.com/sengen", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${accessToken}`
        },
        body: JSON.stringify(params),
      });
      if (!response.ok) throw new Error("Failed to generate sentences");
      return await response.json();
    } catch (error) {
      console.error("Error generating sentences:", error);
    }
  }

  async function generateRandomSentences() {
    sentenceList.innerHTML = "";

    const userData = await getUserData();
        if (!userData) return;

     let params = {};
    if (localStorage.getItem("sentence_id")) {
          // Use localStorage data if sentence_id exists
          const accuracy = localStorage.getItem("accuracy");
          const fluency = localStorage.getItem("fluency");
          const selectedSentenceEng = localStorage.getItem("selectedSentenceEng");
          const vulnerable = localStorage.getItem("vulnerable");

          params = {
            topic: userData.category,
            length: userData.length,
            reference: selectedSentenceEng,
            score1: accuracy,
            score2: fluency,
            vulnerable: vulnerable,
          };
    } else {
      // Use user data
      params = {
        topic: userData.category,
        length: userData.length,
      };
    }

    // Generate sentences using API
    const result = await generateSentences(params);

    // Populate sentence list with generated sentences
    const sentences = result.sentences;
    const translations = result.translations;

    for (let i = 1; i <= Object.keys(sentences).length; i++) {
      const li = document.createElement("li");
      li.textContent = sentences[`sen${i}`];
      li.addEventListener("click", () => {
        localStorage.setItem("selectedSentenceEng", sentences[`sen${i}`]);
        localStorage.setItem("selectedSentenceKor", translations[`sen_trans_${i}`]);
        setTimeout(() => {
          window.location.href = "/RECORD/record.html";
        }, 100);
      });
      sentenceList.appendChild(li);
    }
  }
  // 검색어 입력 후 Cambridge 사전 검색으로 이동
  searchForm.addEventListener('submit', (e) => {
    e.preventDefault(); // 기본 form 제출 방지
    const query = searchInput.value.trim(); // 검색어 가져오기
    if (query) {
      // Cambridge 사전 검색 페이지를 새 창에서 열기
      window.open(`https://dictionary.cambridge.org/ko/%EC%82%AC%EC%A0%84/%EC%98%81%EC%96%B4-%ED%95%9C%EA%B5%AD%EC%96%B4/${encodeURIComponent(query)}`, '_blank');
    } else {
      alert("검색어를 입력하세요."); // 검색어가 없을 때 경고
    }
  });
  // Button click handler to regenerate sentences
  generateBtn.addEventListener("click", () => generateRandomSentences());

  // Initial sentence generation
  generateRandomSentences();
});
