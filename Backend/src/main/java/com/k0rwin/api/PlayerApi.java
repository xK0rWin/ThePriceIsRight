package com.k0rwin.api;

import com.k0rwin.dto.ItemDTO;
import com.k0rwin.dto.PlayerDTO;
import com.k0rwin.dto.PlayerMapper;
import com.k0rwin.dto.VoteMapper;
import com.k0rwin.entity.Item;
import com.k0rwin.entity.Player;
import com.k0rwin.repository.ItemRepository;
import com.k0rwin.repository.PlayerRepository;
import com.k0rwin.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/player")
public class PlayerApi {
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    PlayerMapper playerMapper;

    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity<List<PlayerDTO>> getUsers() {
        List<Player> players = playerRepository.findAll();
        return new ResponseEntity<>(players.stream().map(playerMapper::toDTO).collect(Collectors.toList()), HttpStatus.OK);
    }
}
