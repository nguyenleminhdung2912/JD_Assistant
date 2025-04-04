package com.jd_assistant.controller;

import com.jd_assistant.model.request.EvaluationRequest;
import com.jd_assistant.service.JDService;
import com.jd_assistant.utils.CooldownManager;
import com.jd_assistant.utils.PDFGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class JDController {

    @Autowired
    private JDService jdService;

    @GetMapping("")
    public String renderJDPage(Model model) {
        model.addAttribute("title", "JD Assistant");
        return "jd";
    }

    @PostMapping("/generate-question")
    public String generateQuestion(
            @RequestParam(value = "jdText", required = false) String jdText,
            @RequestParam(value = "interviewLanguage", required = false) String lang,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest request,
            Model model
    ) throws IOException {

        if (file != null && !file.isEmpty()) {
            try (PDDocument document = PDDocument.load(file.getBytes())) {
                PDFTextStripper stripper = new PDFTextStripper();
                jdText = stripper.getText(document);
            }
        }

        if (jdText == null || jdText.isEmpty()) {
            model.addAttribute("error", "JD text is required.");
            return "jd";
        }

        // Xử lý JD text để chuyển chữ in đậm thành in hoa và loại bỏ dấu **
        jdText = formatJDText(jdText);

        // Các xử lý tiếp theo
        String clientIp = request.getRemoteAddr();
        if (CooldownManager.isCoolingDown(clientIp)) {
            model.addAttribute("error", "Vui lòng chờ " + CooldownManager.getRemaining(clientIp) + " giây.");
            return "jd";
        }
        CooldownManager.update(clientIp);

        if (lang == null) lang = "vi";
        String questionHtml = jdService.generateQuestion(jdText, lang);
        model.addAttribute("questionHtml", questionHtml);
        model.addAttribute("jdText", jdText);  // Thêm JD text đã được xử lý vào model
        return "jd";
    }

    @PostMapping("/evaluate-answer")
    public String evaluateAnswer(
            @RequestParam("jdText") String jdText,
            @RequestParam("question") String question,
            @RequestParam("answer") String answer,
            Model model
    ) {
        String evaluationResult = jdService.evaluateAnswer(jdText, question, answer);
        model.addAttribute("evaluationResult", evaluationResult);
        model.addAttribute("questionHtml", question); // Hiển thị lại câu hỏi
        model.addAttribute("evaluationRequest", new EvaluationRequest(jdText, question, answer)); // Đưa dữ liệu câu trả lời vào model
        return "jd";
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam("content") String content
    ) {
        byte[] pdfBytes = PDFGenerator.exportToPdf(content);
        if (pdfBytes == null) {
            return ResponseEntity.internalServerError().body(null);
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"questions.pdf\"")
                .header("Content-Type", "application/pdf")
                .body(pdfBytes);
    }

    public String formatJDText(String jdText) {
        // Regex tìm tất cả các phần được bao quanh bởi **
        Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher matcher = pattern.matcher(jdText);

        // Tạo một StringBuffer để lưu chuỗi đã được thay thế
        StringBuffer formattedText = new StringBuffer();

        // Duyệt qua tất cả các match và thay thế bằng chữ in hoa
        while (matcher.find()) {
            // Chuyển phần giữa ** thành chữ in hoa
            String replacement = matcher.group(1).toUpperCase();
            matcher.appendReplacement(formattedText, replacement);
        }

        // Thêm phần còn lại của chuỗi vào kết quả
        matcher.appendTail(formattedText);

        return formattedText.toString();
    }
}

