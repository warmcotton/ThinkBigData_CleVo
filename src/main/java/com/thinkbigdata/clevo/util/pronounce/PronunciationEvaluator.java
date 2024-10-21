package com.thinkbigdata.clevo.util.pronounce;

import com.thinkbigdata.clevo.CleVoApplication;
import com.thinkbigdata.clevo.exception.PronounceEvaluationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PronunciationEvaluator {
    public Result evaluatePronunciation(String pronSentence, String originalSentence) throws PronounceEvaluationException {
        // 1. 입력된 문자열을 음소로 변환
        List<String> pronPhonemes = toPhonemes(pronSentence);
        List<String> originalPhonemes = toPhonemes(originalSentence);

        // 2. PER (Phoneme Error Rate) 계산을 위한 레벤슈타인 거리 계산
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int distance = levenshtein.apply(pronPhonemes.toString(), originalPhonemes.toString());
        double per = (double) distance / originalPhonemes.size();

        // 3. 점수 계산 (score2)
        double score2;
        if (per <= 0.03) {
            score2 = 5.0;
        } else if (per <= 0.10) {
            score2 = 4.5;
        } else if (per <= 0.15) {
            score2 = 4.0;
        } else if (per <= 0.25) {
            score2 = 3.5;
        } else if (per <= 0.40) {
            score2 = 3.0;
        } else if (per <= 0.60) {
            score2 = 2.5;
        } else if (per <= 0.70) {
            score2 = 2.0;
        } else if (per <= 0.80) {
            score2 = 1.5;
        } else if (per <= 0.90) {
            score2 = 1.0;
        } else {
            score2 = 1.0;
        }

        // 4. 오류 음소 판별 및 빈도 계산
        Map<String, Integer> phonemeErrors = new HashMap<>();
        for (int i = 0; i < pronPhonemes.size(); i++) {
            if (i >= originalPhonemes.size() || !pronPhonemes.get(i).equals(originalPhonemes.get(i))) {
                String phoneme = pronPhonemes.get(i);
                if (!phoneme.isBlank()) { // 공백 제외
                    phonemeErrors.put(phoneme, phonemeErrors.getOrDefault(phoneme, 0) + 1);
                }
            }
        }

        // 5. 빈도 높은 순으로 오류 음소 정렬 후 최대 5개 추출 (오류 음소가 originalPhonemes 대비 pronPhonemes에 있을 경우 우선 선택)
        String vulnerable = phonemeErrors.entrySet().stream()
                .sorted((e1, e2) -> {
                    boolean e1InOriginal = originalPhonemes.contains(e1.getKey());
                    boolean e2InOriginal = originalPhonemes.contains(e2.getKey());
                    if (e1InOriginal && !e2InOriginal) {
                        return -1;
                    } else if (!e1InOriginal && e2InOriginal) {
                        return 1;
                    } else if (e1InOriginal && e2InOriginal) {
                        return e2.getValue().compareTo(e1.getValue());
                    } else if (e1.getValue() >= 3 && e2.getValue() < 3) {
                        return -1;
                    } else if (e1.getValue() < 3 && e2.getValue() >= 3) {
                        return 1;
                    } else {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                })
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));

        // 6. 결과 반환
        return new Result(score2, vulnerable);
    }

    private static List<String> toPhonemes(String sentence) throws PronounceEvaluationException {
        try (InputStream in = CleVoApplication.class.getResourceAsStream("/cmudict-0.7b");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Map<String, String> dictionary = reader.lines()
                    .filter(line -> !line.startsWith(";;;"))
                    .map(line -> line.split("  "))
                    .filter(parts -> parts.length == 2)
                    .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));

            return Stream.of(sentence.toUpperCase().split(" "))
                    .map(word -> dictionary.getOrDefault(word, ""))
                    .flatMap(phonemes -> Stream.of(phonemes.split(" ")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PronounceEvaluationException("Error reading CMU Pronouncing Dictionary", e);
        }
    }

    public static class Result {
        private final double score2;
        private final String vulnerable;

        public Result(double score2, String vulnerable) {
            this.score2 = score2;
            this.vulnerable = vulnerable;
        }

        public double getScore2() {
            return score2;
        }

        public String getVulnerable() {
            return vulnerable;
        }
    }

}
