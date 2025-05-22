package org.zerock.b01.controller.user;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
public class CustomErrorController implements ErrorController {

    // 403(접근 제한) 오류 페이지 처리
    @GetMapping("/error/access-denied")
    public String accessDenied() {
        return "page/user/error/access-denied";
    }

    // 404(잘못된 경로) 오류 페이지 처리
    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.valueOf(status.toString());

            log.error(statusCode);

            if (statusCode == 404) {
                return "page/user/error/404-page";
            }
        }
        return "page/user/error/default";
    }
}
