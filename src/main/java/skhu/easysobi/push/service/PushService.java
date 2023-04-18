package skhu.easysobi.push.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skhu.easysobi.push.repository.PushRepository;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PushService {

    private final PushRepository pushRepository;

    public void sendDeleteInventoryMessage(Principal principal, String inventoryName) throws ExecutionException, InterruptedException {
        String email = principal.getName();

        if (!pushRepository.hasKey(email)) {
            return;
        }

        String token = pushRepository.getToken(email);
        Message message = Message.builder()
                .putData("title", "보관함 삭제 알림")
                .putData("content", inventoryName + " 보관함이 삭제 처리되었습니다.")
                .setToken(token)
                .build();

        send(message);
    }

    public void sendTestMessage(Principal principal) throws ExecutionException, InterruptedException {
        String email = principal.getName();

        if (!pushRepository.hasKey(email)) {
            return;
        }

        String token = pushRepository.getToken(email);
        Message message = Message.builder()
                .putData("title", "푸시 테스트")
                .putData("content", "메시지 내용입니다.")
                .setToken(token)
                .build();

        send(message);
    }

    public void send(Message message) throws ExecutionException, InterruptedException {
        System.out.println(FirebaseMessaging.getInstance().sendAsync(message).get());
    }

}
