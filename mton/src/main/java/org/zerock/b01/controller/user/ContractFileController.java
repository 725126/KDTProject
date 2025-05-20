package org.zerock.b01.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b01.controller.operation.repository.ContractFileRepository;
import org.zerock.b01.domain.operation.ContractFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/contract/file")
public class ContractFileController {
    private final ContractFileRepository contractFileRepository;

    @Value("C:\\upload")
    private String uploadDir; // 실제 저장 경로

    // 컨트롤러 (inline 보기용 PDF/Image)
    @GetMapping("/view/{fileId}")
    public ResponseEntity<Resource> viewContractFile(@PathVariable Long fileId) throws IOException {
        ContractFile contractFile = contractFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일 정보 없음"));

        Path path = Paths.get(contractFile.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다.");
        }

        // 파일 확장자 추출
        String filename = contractFile.getOriginalFileName().toLowerCase();
        MediaType contentType = filename.endsWith(".pdf") ? MediaType.APPLICATION_PDF :
                (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) ? MediaType.IMAGE_JPEG :
                        MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + URLEncoder.encode(contractFile.getOriginalFileName(), StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }
}
