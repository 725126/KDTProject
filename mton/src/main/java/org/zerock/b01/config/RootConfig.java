package org.zerock.b01.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zerock.b01.domain.operation.ProcurementPlan;
import org.zerock.b01.domain.operation.tablehead.ProcurementPlanTableHead;
import org.zerock.b01.dto.operation.ProcurementPlanDTO;

import java.util.HashMap;

@Configuration
public class RootConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // 필드 기반 매칭 활성화
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)  // private 필드 접근 가능하도록 설정
                .setMatchingStrategy(MatchingStrategies.STRICT); // 엄격한 매칭 적용

        modelMapper.addMappings(new PropertyMap<ProcurementPlan, ProcurementPlanDTO>() {
            @Override
            protected void configure() {
                map(source.getPrdplan().getPrdplanId(), destination.getPrdplanId());
                map(source.getMaterial().getMatId(), destination.getMaterialId());
                map(source.getMaterial().getMatName(), destination.getMaterialName());
            }
        });
        return modelMapper;
    }
}
