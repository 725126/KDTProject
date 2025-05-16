package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 9;

    @Builder.Default
    private int code = 1;

    private String type; // 검색의 종류: t, c, w, tc, tw, twc

    private String keyword;

    private String orderId;

    private String matName;

    private LocalDate orderDateStart;

    private LocalDate orderDateEnd;

    private LocalDate orderEndStart;

    private LocalDate orderEndEnd;

    private String drItemCode;

    private String incomingCode;

    private String pCompany;

    private String matId;

    private String incomingStatus;

    private LocalDate drItemDueDateStart;

    private LocalDate drItemDueDateEnd;

    private LocalDate creDateStart;

    private LocalDate creDateEnd;

    private LocalDate deliveryPartnerItemDateStart;

    private LocalDate deliveryPartnerItemDateEnd;

    private LocalDate incomingFirstDateStart;

    private LocalDate incomingFirstDateEnd;

    private LocalDate modifyDateStart;

    private LocalDate modifyDateEnd;

    private LocalDate incomingCompletedAtStart;

    private LocalDate incomingCompletedAtEnd;

    private String incomingItemStatus;

    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }

        return type.split("");
    }

    // String... = String[]
    // String[]보다 더 유연하게 사용가능
    // 직접 String[]을 생성할 필요 없이 여러 인자 전달 가능
    public Pageable getPageable(String... props) {
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).ascending() );

    }

    private String link;

    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();

            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);
            builder.append("&code=" + this.code);

            if (type != null && type.length() > 0) {
                builder.append("&type=" + type);
            }

            if (keyword != null) {
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {

                }
            }
            link = builder.toString();
        }
        return link;
    }
}
