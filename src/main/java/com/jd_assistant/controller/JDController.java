package com.jd_assistant.controller;

import com.jd_assistant.model.request.EvaluationRequest;
import com.jd_assistant.service.JDService;
import com.jd_assistant.utils.CooldownManager;
import com.jd_assistant.utils.PDFGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class JDController {

    @Autowired
    private JDService jdService;

    @GetMapping("")
    public String renderJDPages(Model model) {
        model.addAttribute("title", "JD Assistant");
        return "jd";
    }

    @GetMapping("/jd")
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

        String clientIp = request.getRemoteAddr();
        if (CooldownManager.isCoolingDown(clientIp)) {
            model.addAttribute("error", "Vui lòng chờ " + CooldownManager.getRemaining(clientIp) + " giây.");
            return "jd";
        }
        CooldownManager.update(clientIp);

        if (lang == null) lang = "vi";
        String questionHtml = jdService.generateQuestion(jdText, lang);
        model.addAttribute("questionHtml", questionHtml);
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
}
