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
      try {
        const response = await fetch("https://09fu7eqtjd.execute-api.us-east-1.amazonaws.com/joon/generate", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
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

  // Button click handler to regenerate sentences
  generateBtn.addEventListener("click", () => generateRandomSentences());

  // Initial sentence generation
  generateRandomSentences();
});
