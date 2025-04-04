package com.jd_assistant.utils;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class PDFGenerator {

    public static ByteArrayInputStream generate(String questionHtml) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Thêm câu hỏi vào PDF
            document.add(new Paragraph("Interview Questions:"));
            document.add(new Paragraph(questionHtml).setFontSize(12).setMarginTop(10));

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public static byte[] exportToPdf(String content) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Xử lý nội dung xuống dòng, loại bỏ HTML/markdown nếu cần
            String cleanedContent = content
                    .replaceAll("\\*\\*", "") // bỏ bold
                    .replaceAll("(?m)^\\d+\\.\\s*", "") // bỏ số thứ tự nếu cần
                    .replaceAll("<[^>]*>", "") // bỏ thẻ HTML
                    .replaceAll("\r", ""); // chuẩn hóa xuống dòng

            // Tách từng dòng và thêm vào PDF
            Arrays.stream(cleanedContent.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .forEach(line -> document.add(new Paragraph(line)));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

