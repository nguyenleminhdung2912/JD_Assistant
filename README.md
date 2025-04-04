# JD Assistant

JD Assistant là một ứng dụng web giúp tạo ra các câu hỏi phỏng vấn dựa trên mô tả công việc (JD). Ứng dụng sử dụng Google Generative AI để tạo câu hỏi và đánh giá câu trả lời của ứng viên. Công cụ này không chỉ dành cho lập trình mà còn phù hợp với mọi lĩnh vực khác nhau.

## Yêu Cầu

- Java 17 trở lên
- Maven 3.8.x
- IDE (IntelliJ IDEA, VSCode, Eclipse, v.v.)
- Tài khoản Google Cloud (để lấy API key)

## Cài Đặt

1. **Clone Repository hoặc tải về mã nguồn:**

    ```bash
    git clone https://github.com/nguyenleminhdung2912/JD_Assistant.git
    cd JD_Assistant
    ```
    
2. **Khởi tạo dự án và cài đặt các dependencies của Maven:**

    Chạy lệnh dưới để cài đặt các phụ thuộc của dự án:

    ```bash
    mvn install
    ```
3. **Cấu hình API Key:**

    Vào `application.properties` và thay ${GEMINI_API_KEY} bằng API Key của bạn:

    ```bash
    mvn install
    ```
4. **Khởi tạo dự án và cài đặt các dependencies của Maven:**

    Chạy lệnh dưới để cài đặt các phụ thuộc của dự án:

    ```ini
    genai.api.key=${GEMINI_API_KEY}
    ```
    Thay `${GEMINI_API_KEY}` bằng API key của bạn. API key lấy tại: https://aistudio.google.com/apikey

## Cấu Trúc Dự Án

```
src/
├── controller/
│   └── JDController.java
├── service/
│   └── JDService.java
├── model/
│   └── JDRequest.java
│   └── EvaluationRequest.java
├── config/
│   └── GenAIConfig.java
├── util/
│   └── CooldownManager.java
└── resources/
    └── templates/
        └── jd.html
    └── application.properties
```

## Cách Sử Dụng

1. **Mở trang web:** Sau khi chạy server, mở trình duyệt và truy cập [http://localhost:8080/](http://localhost:8080/).
2. **Nhập JD hoặc Upload PDF:** Bạn có thể nhập trực tiếp mô tả công việc (JD) hoặc upload file PDF chứa JD.
3. **Chọn ngôn ngữ phỏng vấn:** Sử dụng dropdown để chọn ngôn ngữ phỏng vấn (ví dụ: Tiếng Việt, English).
4. **Tạo câu hỏi:** Nhấn nút "Tạo câu hỏi" để tạo ra một câu hỏi phỏng vấn dựa trên JD.
5. **Trả lời câu hỏi:** Nhập câu trả lời vào ô thoại bên dưới câu hỏi và bấm nút "Trả lời" để nhận đánh giá từ Gemini và học điểm cần cải thiện.
