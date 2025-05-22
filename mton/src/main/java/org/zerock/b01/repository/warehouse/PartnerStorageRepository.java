package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.PartnerStorage;
import org.zerock.b01.domain.user.Partner;

import java.util.List;
import java.util.Optional;

public interface PartnerStorageRepository extends JpaRepository<PartnerStorage, Long> {

  @Query("SELECT ps.material.matId, SUM(ps.sstorageQty) " +
          "FROM PartnerStorage ps ")
  List<Object[]> sumPartnerQtyByMaterial();

  Optional<PartnerStorage> findByPartnerAndMaterial(Partner partner, Material material);
}
