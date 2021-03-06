package com.jpa.jpa3.repository;

import com.jpa.jpa3.domain.item.Item;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

//@Repository
public class ItemRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Item item) {
        if (item.getId() == null)
            em.persist(item);
        else
            em.merge(item);
    }

    public Item findById(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> finaAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
