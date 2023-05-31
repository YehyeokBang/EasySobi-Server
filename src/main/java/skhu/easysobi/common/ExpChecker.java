package skhu.easysobi.common;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import skhu.easysobi.inventory.domain.UserInventory;
import skhu.easysobi.inventory.repository.UserInventoryRepository;
import skhu.easysobi.push.service.PushService;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class ExpChecker {

    private final PushService pushService;
    private final UserInventoryRepository userInventoryRepository;

    // 오전 9시 && 오후 9시 마다 실행
    @Scheduled(cron = "0 0 9,21 * * *")
    public void expCheck() throws ExecutionException, InterruptedException {
        String[] emailList = getEmailsOfUsersWithExpiredItemsInInventory();

        pushService.sendExpiredItemMessage(emailList);
    }

    private String[] getEmailsOfUsersWithExpiredItemsInInventory() {
        List<UserInventory> list = userInventoryRepository.findAllByExpiredItemsInInventory();
        String[] emailList = new String[list.size()];
        int index = 0;
        for (UserInventory ui : list) {
            emailList[index++] = ui.getUser().getEmail();
        }
        return emailList;
    }

}
