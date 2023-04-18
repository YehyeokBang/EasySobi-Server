package skhu.easysobi.push.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import skhu.easysobi.push.service.PushService;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

@Tag(name = "푸시")
@RestController
@RequiredArgsConstructor
public class PushController {

    private final PushService pushService;

    @PostMapping("/api/push")
    @Operation(
            summary = "푸시 알람 보내기",
            description = "푸시 테스트",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public void messageTest(Principal principal) throws ExecutionException, InterruptedException {
        pushService.sendTestMessage(principal);
    }

}
