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
    private int page = 0; // ✅ 0부터 시작

    @Builder.Default
    private int size = 15;

    @Builder.Default
    private int code = 1;

    private String type; // 검색의 종류: t, c, w, tc, tw, twc

    private String keyword;

    //

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

    private String prdplanId;

    private String outgoingCode;

    private String matType;

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

    private LocalDate prdplanEndStart;

    private LocalDate prdplanEndEnd;

    private LocalDate outgoingDateStart;

    private LocalDate outgoingDateEnd;

    private LocalDate outgoingCompletedAtStart;

    private LocalDate outgoingCompletedAtEnd;

    private LocalDate updateDateStart;
    private LocalDate updateDateEnd;

    private String incomingStatus;

    private String incomingItemStatus;

    private String outgoingStatus;

    //
    private String updateReason;

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

    public void setPage(int page) {
        this.page = page < 0 ? 0 : page; // 0도 허용하도록 수정
    }

    public void setSize(int size) {
        this.size = size <= 0 ? 10 : size;
    }

    // 🔹 split 해서 여러 검색 조건(type=tc → t,c)
    public String[] getTypes() {
        if (type == null || type.isEmpty()) return null;
        return type.split("");
    }

    // 🔹 정렬 기준 props를 기준으로 페이징 객체 생성
    public Pageable getPageable(String... props) {
        return PageRequest.of(this.page, this.size, Sort.by(props).ascending());
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(this.page, this.size, sort);
    }

    // 🔹 검색 파라미터 포함된 URL 링크 생성
    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();

            builder.append("page=").append(this.page);
            builder.append("&size=").append(this.size);
            builder.append("&code=").append(this.code);

            if (type != null && !type.isEmpty()) builder.append("&type=").append(type);

            if (keyword != null && !keyword.isEmpty()) {
                try {
                    builder.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // 무시
                }
            }

            link = builder.toString();
        }
        return link;
    }
//    public String[] getTypes() {
//        if (type == null || type.isEmpty()) {
//            return null;
//        }
//
//        return type.split("");
//    }
//
//    // String... = String[]
//    // String[]보다 더 유연하게 사용가능
//    // 직접 String[]을 생성할 필요 없이 여러 인자 전달 가능
//    public Pageable getPageable(String... props) {
//        return PageRequest.of(this.page - 1, this.size, Sort.by(props).ascending() );
//
//    }
//
//    private String link;
//
//    public String getLink() {
//        if (link == null) {
//            StringBuilder builder = new StringBuilder();
//
//            builder.append("page=" + this.page);
//            builder.append("&size=" + this.size);
//            builder.append("&code=" + this.code);
//
//            if (type != null && type.length() > 0) {
//                builder.append("&type=" + type);
//            }
//
//            if (keyword != null) {
//                try {
//                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
//                } catch (UnsupportedEncodingException e) {
//
//                }
//            }
//            link = builder.toString();
//        }
//        return link;
//    }
}
