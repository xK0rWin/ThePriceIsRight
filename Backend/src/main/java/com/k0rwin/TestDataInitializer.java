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
        };
    }
}
