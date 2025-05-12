package org.zerock.b01.dto.warehouse;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.b01.domain.warehouse.DeliveryRequestItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequestDTO {

  private Long drId;

  private String orderId;

  private String matName;

  private LocalDate orderDate;

  private LocalDate orderEnd;

  private int orderQty;

  private int drTotalQty;

  private String drStatus;

}
