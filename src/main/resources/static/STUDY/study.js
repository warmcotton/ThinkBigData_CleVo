document.addEventListener("DOMContentLoaded", async () => {
  const sentenceList = document.getElementById("sentence-list");
  const generateBtn = document.getElementById("generate-btn");

  const fallbackSentences = [
    {
      "sentences": {
        "sen1": "Eating breakfast in the morning helps me start the day",
        "sen2": "Reading a good book before bed helps me sleep better",
        "sen3": "Having a cup of tea in the evening helps me relax and reflect on the day",
        "sen4": "Listening to music helps me relax and unwind after a long day",
        "sen5": "Going for a walk in the park is relaxing and refreshing"
      },
      "translations": {
        "sen_trans_1": "아침에 아침 식사를 하면 하루를 시작하기가 좋아요.",
        "sen_trans_2": "잘 자는데 도움이 되는 좋은 책을 읽는 것이에요.",
        "sen_trans_3": "저녁에 차 한잔 마시면 편안해지고 하루를 돌아보기 좋아요.",
        "sen_trans_4": "긴 하루 끝에 음악을 들으면 편해지고 스트레스를 풀 수 있어요.",
        "sen_trans_5": "공원에서 산책하면 편안하고 상쾌해요."
      }
    }
  ];
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

    return fallbackSentences;
  }

//    async function generateSentences(params) {
//      try {
//        const response = await fetch("https://09fu7eqtjd.execute-api.us-east-1.amazonaws.com/joon/generate", {
//          method: "POST",
//          headers: {
//            "Content-Type": "application/json",
//          },
//          body: JSON.stringify(params),
//        });
//        if (!response.ok) throw new Error("Failed to generate sentences");
//        return await response.json();
//      } catch (error) {
//        console.error("Error generating sentences:", error);
//      }
//      }


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
    const result = await generateSentences({});
    console.log("Generated sentences result:", result);
    if (!result || !result[0] || !result[0].sentences) return;

    // Populate sentence list with generated sentences
    const sentences = result[0].sentences;
    const translations = result[0].translations;
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
