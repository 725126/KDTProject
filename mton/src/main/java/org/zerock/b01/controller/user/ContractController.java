package org.zerock.b01.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.b01.controller.operation.repository.ContractFileRepository;
import org.zerock.b01.controller.operation.repository.ContractMaterialRepository;
import org.zerock.b01.controller.operation.repository.ContractRepository;
import org.zerock.b01.controller.operation.repository.MaterialRepository;
import org.zerock.b01.domain.operation.Contract;
import org.zerock.b01.domain.operation.ContractFile;
import org.zerock.b01.domain.operation.ContractMaterial;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.dto.operation.ContractDTO;
import org.zerock.b01.dto.operation.ContractMaterialViewDTO;
import org.zerock.b01.repository.user.PartnerRepository;
import org.zerock.b01.security.CustomUserDetails;
import org.zerock.b01.service.operation.ContractService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/procurement/contract")
public class ContractController {

    private final MaterialRepository materialRepository;
    private final PartnerRepository partnerRepository;
    private final ContractRepository contractRepository;
    private final ContractMaterialRepository contractMaterialRepository;
    private final ContractFileRepository contractFileRepository;


    @Value("C:\\upload")
    private String uploadDir; // 실제 저장 경로

//    // 계약 정보
//    @GetMapping("")
//    public String contractGet(Model model) {
//        List<ContractMaterialViewDTO> list = contractService.getAllContractMaterialViews();
//        model.addAttribute("contractList", list);
//
//
//        return "page/operation/procurement/contract";
//    }

    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<?> submitContract(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("data") ContractDTO dto,
            @RequestPart("contractFile") MultipartFile contractFile) {

        log.info("현재 로그인한 사용자: {}", userDetails.getUsername());

        log.info("등록된 계약 정보: {}", dto);
        log.info("등록된 계약서 파일명: {}", contractFile.getOriginalFilename());

        // Contract 저장
        Partner partner = partnerRepository.findById(dto.getPartnerId())
                .orElseThrow(() -> new IllegalArgumentException("협력업체 없음"));

        Contract contract = Contract.builder()
                .conId(dto.getConCode())
                .partner(partner)
                .conDate(dto.getStartDate())
                .conEnd(dto.getEndDate())
                .build();

        contractRepository.save(contract);

        // ContractMaterial 저장
        for (int i = 0; i < dto.getMaterialCodes().size(); i++) {
            String matId = dto.getMaterialCodes().get(i);
            Material material = materialRepository.findById(matId)
                    .orElse(null); // 삭제된 자재도 고려해서 nullable 처리

            ContractMaterial contractMaterial = ContractMaterial.builder()
                    .cmtId(contract.getConId() + "-" + i)
                    .contract(contract)
                    .material(material)
                    .cmtPrice(dto.getMaterialPrices().get(i))
                    .cmtQty(dto.getMaterialQtys().get(i))
                    .cmtReq(dto.getMaterialSchedules().get(i))
                    .cmtExplains(dto.getMaterialExplains().get(i))
                    .build();

            contractMaterialRepository.save(contractMaterial);
        }

        // 계약서 파일 저장 (파일 시스템 or DB Blob)
        try {
            String originalFilename = contractFile.getOriginalFilename();

            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new IllegalArgumentException("잘못된 파일명입니다.");
            }

            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));

            if (!List.of(".pdf", ".jpg", ".jpeg").contains(ext.toLowerCase())) {
                throw new IllegalArgumentException("허용되지 않는 파일 형식입니다.");
            }

            String savedFileName = UUID.randomUUID() + ext;

            Path targetPath = Paths.get(uploadDir).resolve(savedFileName);
            Files.createDirectories(targetPath.getParent()); // 경로 없으면 생성
            Files.copy(contractFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 4. DB에 파일 정보 저장
            ContractFile contractFileEntity = ContractFile.builder()
                    .contract(contract)
                    .originalFileName(originalFilename)
                    .savedFileName(savedFileName)
                    .filePath(targetPath.toString())
                    .build();

            contractFileRepository.save(contractFileEntity);
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "파일 저장 실패"));
        }


        return ResponseEntity.ok().body(Map.of("status", "success", "message", "계약 등록 완료"));
    }

    @PostMapping("/find/mt-names")
    public ResponseEntity<List<String>> getMaterialNames(@RequestBody List<String> materialCodes) {
        List<String> names = materialRepository.findNamesByMatId(materialCodes);
        return ResponseEntity.ok(names);
    }

    @GetMapping("/company-name/{id}")
    public ResponseEntity<String> getCompanyName(@PathVariable("id") Long partnerId) {
        String company = partnerRepository.findCompanyNameById(partnerId)
                .orElse("회사명을 찾을 수 없습니다.");
        return ResponseEntity.ok(company);
    }

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
