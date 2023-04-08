package skhu.easysobi.common;

import lombok.RequiredArgsConstructor;
import skhu.easysobi.inventory.domain.Category;
import skhu.easysobi.inventory.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class ExpDate {

    // 소비기한 계산하는 메서드
    public static LocalDateTime calcExpDate(LocalDateTime mfgDate, CategoryRepository categoryRepository, Long categoryNum) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryNum(categoryNum);

        // 카테고리가 존재하는 경우
        if (optionalCategory.isPresent()) {
            // 해당 카테고리의 소비기한 일수를 알아내어 제조일자에 더한 후
            // 해당 아이템의 소비기한 설정하기
            Category category = optionalCategory.get();
            int exp = category.getExp();

            // 소비기한 구하기
            return mfgDate.plusDays(exp);
        } else {
            throw new IllegalStateException("카테고리를 찾을 수 없습니다");
        }
    }
}
