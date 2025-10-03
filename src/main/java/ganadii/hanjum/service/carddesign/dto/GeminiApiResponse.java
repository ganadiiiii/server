package ganadii.hanjum.service.carddesign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Gemini API response DTOs for type-safe parsing
 */
public class GeminiApiResponse {

    public record Response(
            List<Candidate> candidates
    ) {}

    public record Candidate(
            Content content
    ) {}

    public record Content(
            List<Part> parts
    ) {}

    public record Part(
            @JsonProperty("inline_data")
            InlineData inlineData,
            @JsonProperty("inlineData")
            InlineData inlineDataAlt  // Alternative field name support
    ) {
        public InlineData getInlineData() {
            return inlineData != null ? inlineData : inlineDataAlt;
        }
    }

    public record InlineData(
            String data,
            String mimeType
    ) {}
}
