package com.k0rwin.dto;

import com.k0rwin.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PlayerMapper {

    @Autowired
    private VoteMapper voteMapper;

    public PlayerDTO toDTO(Player user) {
        if (user == null) {
            return null;
        }
        PlayerDTO dto = new PlayerDTO();
        dto.setUsername(user.getUsername());
        dto.setScore(user.getScore());
        dto.setVotes(user.getVotes().stream()
                .map(voteMapper::toDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public Player toEntity(PlayerDTO dto) {
        if (dto == null) {
            return null;
        }
        Player user = new Player(dto.getUsername());
        user.setScore(dto.getScore());
        return user;
    }
}
