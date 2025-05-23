package org.zerock.b01.controller.warehouse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.warehouse.DeliveryRequestItemDTO;
import org.zerock.b01.service.warehouse.DeliveryRequestItemService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/delivery-request")
@RequiredArgsConstructor
public class DeliveryRequestController {

  private final DeliveryRequestItemService deliveryRequestItemService;

  @Operation(description = "DeliveryRequestItem POST")
  @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> registerDeliveryRequestItems(@Valid @RequestBody List<DeliveryRequestItemDTO> dtoList,
                                                         BindingResult bindingResult) throws BindException {


    log.info("Received DTO List: {}", dtoList);

    // 입력값 검증
    if (bindingResult.hasErrors()) {
      throw new BindException(bindingResult);
    }

    Map<String, Object> resultMap = new HashMap<>();

    try {
      // Service에서 여러 항목을 저장
      List<DeliveryRequestItemDTO> savedItems = deliveryRequestItemService.registerDeliveryRequestItem(dtoList);

      resultMap.put("status", "success");
      resultMap.put("count", savedItems.size());
      resultMap.put("items", savedItems);  // 반환된 DTO는 drItemCode 포함
      resultMap.put("message", "납입지시 아이템 다중 등록 성공");

    } catch (Exception e) {
      resultMap.put("status", "failure");
      resultMap.put("message", e.getMessage());
      log.error("Error occurred while registering delivery request items in batch", e);
    }

    return resultMap;
  }

  @Operation(description = "GET 방식으로 납입지시등록조회")
  @GetMapping("/{drItemId}")
  private DeliveryRequestItemDTO getDeliveryRequestItemDTO(@PathVariable Long drItemId) {
    DeliveryRequestItemDTO deliveryRequestItemDTO = deliveryRequestItemService.readDeliveryRequestItem(drItemId);
    return deliveryRequestItemDTO;
  }

  @Operation(description = "DELETE 방식으로 납입지시등록 처리")
  @DeleteMapping("/{drItemId}")
  public Map<String,Long> removeDeliveryRequestItem(@PathVariable("drItemId") Long drItemId) {

    deliveryRequestItemService.removeDeliveryRequestItem(drItemId);

    Map<String,Long> resultMap = new HashMap<>();

    resultMap.put("drItemId", drItemId);

    return resultMap;
  }


  @Operation(description = "PUT 방식으로 납입지시등록 처리")
  @PutMapping(value = "/{drItemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> modifyDeliveryRequestItem(@PathVariable("drItemId") Long drItemId,
                                                    @RequestBody DeliveryRequestItemDTO deliveryRequestItemDTO) {
    deliveryRequestItemDTO.setDrItemId(drItemId);

    try {
      deliveryRequestItemService.modifyDeliveryRequestItem(deliveryRequestItemDTO);

      Map<String, Long> resultMap = new HashMap<>();
      resultMap.put("drItemId", drItemId);
      return ResponseEntity.ok(resultMap);

    } catch (IllegalStateException e) {
      // 여기서 에러 메시지를 JSON으로 반환
      Map<String, String> errorMap = new HashMap<>();
      errorMap.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(errorMap);
    }
  }

  @Operation(description = "GET 방식으로 최신 납입지시일자 조회")
  @GetMapping("/last-dr-item-due-date/{drId}")
  public Map<String, Object> getLatestDrItemDueDate(@PathVariable Long drId) {
    Map<String, Object> resultMap = new HashMap<>();

    try {
      // Service 호출하여 최신 dueDate 조회
      LocalDate lastDrItemDueDate = deliveryRequestItemService.getLastDrItemDueDate(drId);

      resultMap.put("status", "success");
      resultMap.put("drItemId", drId);
      resultMap.put("lastDrItemDueDate", lastDrItemDueDate);
      resultMap.put("message", "납입지시일자 조회 성공");

    } catch (Exception e) {
      resultMap.put("status", "failure");
      resultMap.put("message", e.getMessage());
      log.error("Error occurred while fetching latest due date", e);
    }

    return resultMap;
  }

}
