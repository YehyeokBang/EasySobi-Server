package skhu.easysobi.push.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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

        Notification notification = Notification.builder()
                .setTitle("보관함 삭제 알림")
                .setBody(inventoryName + "보관함이 삭제 처리되었습니다.")
                .build();

        String token = pushRepository.getToken(email);
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();

        send(message);
    }

    public void sendTestMessage(Principal principal) throws ExecutionException, InterruptedException {
        String email = principal.getName();

        if (!pushRepository.hasKey(email)) {
            return;
        }

        Notification notification = Notification.builder()
                .setTitle("푸시 테스트")
                .setBody("메시지 내용입니다.")
                .build();

        String token = pushRepository.getToken(email);
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();

        send(message);
    }

    public void send(Message message) throws ExecutionException, InterruptedException {
        System.out.println(FirebaseMessaging.getInstance().sendAsync(message).get());
    }

}
