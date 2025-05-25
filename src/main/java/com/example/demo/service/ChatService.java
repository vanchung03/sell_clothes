package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.ProductVariant;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;

    public ChatService(ProductRepository productRepository, ProductVariantRepository variantRepository) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
    }

    public Mono<String> getChatResponse(String userMessage) {
        String context = buildProductContext();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                .build();

        Map<String, Object> requestBody = Map.of(
                "model", "llama3-70b-8192",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "Bạn là trợ lý tư vấn cho shop quần áo. Tất cả câu trả lời của bạn phải bằng tiếng Việt. " +
                                        "Dưới đây là thông tin sản phẩm:\n" + context),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class).flatMap(body -> {
                            System.err.println("Lỗi từ API: " + body); // In ra body lỗi để debug
                            return Mono.error(new RuntimeException("Lỗi từ máy chủ chat: " + body));
                        })
                )
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (List<Map<String, Object>>) response.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        var message = (Map<String, Object>) choices.get(0).get("message");
                        return message.get("content").toString();
                    }
                    return "Xin lỗi, hiện tại tôi chưa có thông tin sản phẩm phù hợp.";
                })
                .onErrorResume(e -> {
                    // Trả lời mặc định khi lỗi kết nối hoặc lỗi API
                    return Mono.just("Có lỗi xảy ra khi kết nối với trợ lý. Vui lòng thử lại sau.\nChi tiết: " + e.getMessage());
                });
    }


    private String buildProductContext() {
        StringBuilder sb = new StringBuilder();
        List<Product> products = productRepository.findAll();

        // Sử dụng dấu * để định dạng rõ ràng hơn
        sb.append("Danh sách sản phẩm:\n");

        for (Product p : products) {
            sb.append("* Sản phẩm: ").append(p.getName()).append("\n")
                    .append("   Mô tả: ").append(p.getDescription()).append("\n")
                    .append("   Giá: ").append(p.getPrice()).append(" VNĐ\n")
                    .append("   Link chi tiết: ").append("http://localhost:4200/product-detail/").append(p.getProductId()).append("\n");

            List<ProductVariant> variants = variantRepository.findByProduct_ProductId(p.getProductId());
            if (!variants.isEmpty()) {
                sb.append("   Các biến thể:\n");
                for (ProductVariant v : variants) {
                    sb.append("    - Size: ").append(v.getSize())
                            .append(", Màu: ").append(v.getColor())
                            .append(", Giá: ").append(v.getPrice()).append(" VNĐ")
                            .append(", Tồn kho: ").append(v.getStockQuantity()).append("\n");
                }
            } else {
                sb.append("   (Không có biến thể nào)\n");
            }

            sb.append("\n"); // Dòng trống giữa các sản phẩm
        }
        return sb.toString();
    }

}
