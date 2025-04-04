package com.jd_assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class JDService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public JDService(@Value("${genai.api.key}") String genAiApiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(API_URL + "?key=" + genAiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String generateQuestion(String jdText, String langCode) {
        String prompt = String.format("""
                Dựa trên mô tả công việc (JD) sau đây, hãy tạo 3 câu hỏi phỏng vấn bằng ngôn ngữ %s.
                JD: %s
                """, langCode.equals("vi") ? "tiếng Việt" : "English", jdText);

        return callGemini(prompt);
    }

    public String evaluateAnswer(String jdText, String question, String answer) {
        String prompt = String.format("""
                Dưới đây là một đoạn JD và một câu hỏi phỏng vấn từ JD đó. Ứng viên đã trả lời câu hỏi. 
                Hãy đánh giá câu trả lời dựa trên nội dung JD, chấm điểm trên thang 10, nhận xét kỹ năng, đưa ra gợi ý cải thiện nếu cần.

                JD: %s
                Câu hỏi: %s
                Câu trả lời ứng viên: %s

                Đánh giá:
                """, jdText, question, answer);

        return callGemini(prompt);
    }

    private String callGemini(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", prompt))
                    ))
            );

            String response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode json = objectMapper.readTree(response);
            return json.at("/candidates/0/content/parts/0/text").asText("Không có phản hồi từ AI.");
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi khi gọi Gemini API.";
        }
    }
}
