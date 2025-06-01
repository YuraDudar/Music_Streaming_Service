package ru.SberPo666.stream_service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StreamingController {
    private final TrackRepository trackRepository;
    private final S3Service s3Service;
    private final KafkaService kafkaService;

    @GetMapping("/{id}/range-stream")
    public void streamWithRange(@PathVariable UUID id,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                @AuthenticationPrincipal Jwt jwt) throws IOException {
        Track track = trackRepository.findById(id).orElse(null);
        if (track == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        String rangeHeader = request.getHeader(HttpHeaders.RANGE);
        ResponseInputStream<GetObjectResponse> s3Object = s3Service.getS3Object(track.getS3key(), rangeHeader);
        GetObjectResponse objectResponse = s3Object.response();

        long contentLength = objectResponse.contentLength();
        String contentRange = objectResponse.contentRange();

        if (rangeHeader != null && contentRange != null) {
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
            response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
        } else {
            response.setStatus(HttpStatus.OK.value());
        }

        response.setContentType("audio/mpeg");
        response.setContentLengthLong(contentLength);

        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = s3Object.read(buffer)) != -1) {
            response.getOutputStream().write(buffer, 0, bytesRead);
        }
        s3Object.close();
        kafkaService.sendListeningMetric(id);
        if(jwt != null)
            kafkaService.sendHistory(id, jwt.getClaim("user_id"));
    }

}
