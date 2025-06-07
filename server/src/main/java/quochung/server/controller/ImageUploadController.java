package quochung.server.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageUploadController {

    private final WebClient.Builder webClientBuilder;

    @PostMapping("/upload-image")
    public ResponseEntity<Object> uploadImage(@RequestParam("image") MultipartFile image) {
        String apiKey = "c47e98631c8e40908e9a4b53c9a0d6c7"; // API key của bạn

        // Sử dụng MultipartBodyBuilder để tạo yêu cầu multipart/form-data
        String url = webClientBuilder.baseUrl("https://api.imgbb.com/1/upload").build()
                .post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build()) // Thêm API key vào URL
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("image", image.getResource()))// Thêm tệp hình ảnh vào body
                .retrieve()
                .bodyToMono(JsonNode.class) // Nhận kết quả dưới dạng JsonNode
                .map(response -> {
                    if (response.path("success").asBoolean()) {
                        return response.path("data").path("url").asText();
                    } else {
                        return "Lỗi tải ảnh lên";
                    }
                })
                .block();
        return ResponseEntity.ok(url);
    }
}
