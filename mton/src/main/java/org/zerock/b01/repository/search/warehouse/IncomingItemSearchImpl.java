package org.zerock.b01.repository.search.warehouse;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.operation.QContractMaterial;
import org.zerock.b01.domain.operation.QMaterial;
import org.zerock.b01.domain.operation.QOrdering;
import org.zerock.b01.domain.user.QPartner;
import org.zerock.b01.domain.warehouse.*;

import java.time.LocalDate;
import java.util.List;

public class IncomingItemSearchImpl extends QuerydslRepositorySupport implements IncomingItemSearch {

  public IncomingItemSearchImpl() {super(IncomingTotal.class);}

}
