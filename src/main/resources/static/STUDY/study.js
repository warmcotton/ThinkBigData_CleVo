document.addEventListener("DOMContentLoaded", async () => {
  const sentenceList = document.getElementById("sentence-list");
  const generateBtn = document.getElementById("generate-btn");

    function mapCategoryToTopic(categories) {
      const categoryMap = {
        TOPIC1: "hobby",
        TOPIC2: "business",
        TOPIC3: "travel",
        TOPIC4: "dailylife",
        TOPIC5: "shopping",
      };
      return categories.map((cat) => categoryMap[cat]).join(", ");
    }

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
      userNameElement.textContent = userData.name;

      return userData;
    } catch (error) {
      console.error("Error fetching user data:", error);
    }
  }

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
    const sentenceDto = localStorage.getItem("sentenceDto") ? JSON.parse(localStorage.getItem("sentenceDto")) : null;

    const userData = await getUserData();
        console.log(userData);
        if (!userData) return;

     let params = {};
    if (sentenceDto) {
      // Use sentenceDto data
      params = {
        topic: mapCategoryToTopic(sentenceDto.categories),
        length: sentenceDto.level === 1 ? "5" : sentenceDto.level === 2 ? "10" : "15",
        reference: sentenceDto.eng,
        score1: sentenceDto.accuracy,
        score2: sentenceDto.fluency,
      };
    } else {
      // Use user data
      params = {
        topic: mapCategoryToTopic(userData.category),
        length: userData.level === 1 ? "5" : userData.level === 2 ? "10" : "15",
      };
    }

    // Generate sentences using API
    const result = await generateSentences(params);
    console.log("Generated sentences result:", result);

    // Populate sentence list with generated sentences
    const sentences = result.sentences;
    const translations = result.translations;

    for (let i = 1; i <= Object.keys(sentences).length; i++) {
      const li = document.createElement("li");
      li.textContent = sentences[`sen${i}`];
      console.log("Adding sentence to list:", sentences[`sen${i}`]);  // 로그 추가
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
