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

    public void sendItemExpiresMessage(Principal principal) throws ExecutionException, InterruptedException {
        String email = principal.getName();

        if (!pushRepository.hasKey(email)) {
            return;
        }

        String token = pushRepository.getToken(email);
        Message message = Message.builder()
                .putData("title", "아이템 소비기한 만료 알림")
                .putData("content", "등록하신 아이템의 소비기한이 만료되었습니다.")
                .setToken(token)
                .build();

        send(message);
    }

    public void sendItemOneDayLeftMessage(Principal principal) throws ExecutionException, InterruptedException {
        String email = principal.getName();

        if (!pushRepository.hasKey(email)) {
            return;
        }

        String token = pushRepository.getToken(email);
        Message message = Message.builder()
                .putData("title", "아이템 소비기한 임박 알림")
                .putData("content", "등록하신 아이템의 소비기한이 하루 남았습니다.")
                .setToken(token)
                .build();

        send(message);
    }

    public void send(Message message) throws ExecutionException, InterruptedException {
        System.out.println(FirebaseMessaging.getInstance().sendAsync(message).get());
    }

}
