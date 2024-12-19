package com.k0rwin;

import com.k0rwin.entity.Item;
import com.k0rwin.entity.Player;
import com.k0rwin.repository.ItemRepository;
import com.k0rwin.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class TestDataInitializer {

    private final PlayerRepository playerRepository;
    private final ItemRepository itemRepository;

    public TestDataInitializer(PlayerRepository playerRepository, ItemRepository itemRepository) {
        this.playerRepository = playerRepository;
        this.itemRepository = itemRepository;
    }

    @Bean
    public CommandLineRunner initTestData() {
        return args -> {

            Player p1 = new Player("Kevin");
            playerRepository.save(p1);

            Player p2 = new Player("Admin");
            playerRepository.save(p2);

            Item item1 = new Item("Laptop","https://cdn.pixabay.com/photo/2023/01/08/18/11/plants-7705865_1280.jpg", "OPEN",  232.);
            itemRepository.save(item1);

            System.out.println("Test data initialized");
        };
    }
}
