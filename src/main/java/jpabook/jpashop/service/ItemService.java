package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    
    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){
        /**
         1. itemRepository.findOne는 em.find로 가져온것이기때문에 영속성컨텍스트에 영속되어있다.
         2. set으로 변경하였다면
         3. @Transactional에 의하여 commit 이 될 것이고
         4. flush가 발생하여
                영속성컨텍스트에 1차캐시안에 엔티티와 스냅샷을 비교하여
                변경된 점을 감지하여 [쓰기 지연 SQL 저장소]에 쿼리를 저장한 후,
                SQL문을 DB에 보낸다.
                이대로 변경됨!
         */
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
    }

    public Item findOne(Long id){
        return itemRepository.findOne(id);
    }
    
    public List<Item> findItems(){
        return itemRepository.findAll();
    }
}
