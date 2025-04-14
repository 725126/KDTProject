package org.zerock.b01.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice // 모든 @RestController에서 발생하는 예외를 전역적으로 처리하는 클래스임을 명시
@Log4j2 // Log4j2를 이용한 로깅 기능을 제공하는 어노테이션
public class CustomRestAdvice {

//    // BindException이 발생했을 때 실행되는 예외 처리 메서드
//    @ExceptionHandler(BindException.class) // BindException 예외를 처리하는 핸들러로 지정
//    @ResponseStatus(HttpStatus.EXPECTATION_FAILED) // HTTP 상태 코드를 417 Expectation Failed로 설정
//    public ResponseEntity<Map<String, String>> handleBindException(BindException e) {
//        log.error(e); // 예외 발생 시 로그에 기록
//
//        Map<String, String> errorMap = new HashMap<>(); // 오류 정보를 저장할 Map 객체 생성
//
//        if (e.hasErrors()) { // 예외 객체에 오류가 있는지 확인
//            BindingResult bindingResult = e.getBindingResult(); // 바인딩 결과를 가져옴
//
//            // 발생한 모든 필드 오류를 순회하며 errorMap에 추가
//            bindingResult.getFieldErrors().forEach(fieldError -> {
//                errorMap.put(fieldError.getField(), fieldError.getCode()); // 필드명과 오류 코드 저장
//            });
//        }
//
//        return ResponseEntity.badRequest().body(errorMap); // 400 Bad Request 응답과 함께 오류 정보 반환
//    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleBindException(BindException e) {

        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        if(e.hasErrors()){
            BindingResult bindingResult = e.getBindingResult();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getCode());
            });
        }

        return ResponseEntity.badRequest().body(errorMap);
    }

    //    @ExceptionHandler(BindException.class)
//    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
//    public ResponseEntity<Map<String, String>> handleBindException(BindException e) {
//
//        log.error(e);
//        Map<String, String> errorMap = new HashMap<>();
//
//        if (e.hasErrors()) {
//            BindingResult bindingResult = e.getBindingResult();
//
//            bindingResult.getFieldErrors().forEach(fieldError -> {
//                errorMap.put(fieldError.getField(), fieldError.getCode());
//            });
//        }
//
//        return ResponseEntity.badRequest().body(errorMap);
//    }
//
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleFKException(Exception e) {

        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("time", "" + System.currentTimeMillis());
        errorMap.put("msg", "constraint fails");
        return ResponseEntity.badRequest().body(errorMap);
    }

//    @ExceptionHandler(NoSuchElementException.class)
//    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
//    public ResponseEntity<Map<String, String>> handleNoSuchElement(Exception e) {
//
//        log.error(e);
//
//        Map<String, String> errorMap = new HashMap<>();
//
//        errorMap.put("time", ""+System.currentTimeMillis());
//        errorMap.put("msg",  "No Such Element Exception");
//        return ResponseEntity.badRequest().body(errorMap);
//    }

    @ExceptionHandler({
            NoSuchElementException.class,
            EmptyResultDataAccessException.class }) //추가
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleNoSuchElement(Exception e) {

        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("time", ""+System.currentTimeMillis());
        errorMap.put("msg",  "No Such Element Exception");
        return ResponseEntity.badRequest().body(errorMap);
    }
}

