//package org.zerock.b01.domain.warehouse;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.zerock.b01.domain.BaseEntity;
//import org.zerock.b01.domain.operation.Material;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
//public class CompanyStorageItem extends BaseEntity {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long cstorageItemId;
//
//  @ManyToOne
//  @JoinColumn(name = "cstorage_id", nullable = false)
//  private CompanyStorage companyStorage;
//
//  @ManyToOne
//  @JoinColumn(name = "mat_id", nullable = false)
//  private Material material;
//
//  @Column(nullable = false)
//  private int cstorageQty;
//
//  @Enumerated(EnumType.STRING)
//  @Column(nullable = false)
//  private CompanyStorageStatus companyStorageStatus;
//
//}