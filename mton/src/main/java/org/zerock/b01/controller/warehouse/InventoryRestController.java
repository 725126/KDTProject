package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.dto.warehouse.CompanyStorageDTO;
import org.zerock.b01.service.warehouse.CompanyStorageService;

@RestController
@Log4j2
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryRestController {

  private final CompanyStorageService companyStorageService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody CompanyStorageDTO dto) {
    try {
      CompanyStorage saved = companyStorageService.createCompanyStorage(dto);
      return ResponseEntity.ok(saved);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 등록된 창고 ID입니다.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 중 오류가 발생했습니다.");
    }
  }

}
