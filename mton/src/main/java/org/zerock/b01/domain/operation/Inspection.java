package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inspection {
    @Id
    private String insId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Ordering ordering;

    @Column(nullable = false)
    private LocalDate insStart;

    @Column(nullable = false)
    private LocalDate insEnd;

    @Column(nullable = false)
    private int insQty;

    @Column(nullable = false)
    private int insTotal;

    @Column(nullable = false)
    private String insStat;

    public void changeQty(int qty) {
        this.insQty = qty;

        if (this.insQty < 0) {
            this.insQty = 0;
        } else if (this.insQty >= this.insTotal) {
            this.insQty = this.insTotal;
            this.insStat = "완료";
        } else {
            this.insStat = "진행중";
        }
    }

    public void changeQty(String qty) {
        try {
            switch (qty.trim().substring(0, 1)) {
                case "+":
                    this.insQty += Integer.parseInt(qty.trim().substring(1));
                    break;
                case "-":
                    this.insQty -= Integer.parseInt(qty.trim().substring(1));
                    break;
                default:
                    this.insQty = Integer.parseInt(qty.trim());
            }

            if (this.insQty < 0) {
                this.insQty = 0;
            } else if (this.insQty >= this.insTotal) {
                this.insQty = this.insTotal;
                this.insStat = "완료";
            } else {
                this.insStat = "진행중";
            }
        } catch (NumberFormatException e) {
            System.out.println("Inspection ChangeQty Error");
        }
    }
}
