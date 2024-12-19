package com.k0rwin.dto;

import com.k0rwin.entity.Item;
import com.k0rwin.entity.Player;
import com.k0rwin.entity.Vote;
import com.k0rwin.repository.ItemRepository;
import com.k0rwin.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VoteMapper {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ItemRepository itemRepository;

    public VoteDTO toDTO(Vote vote) {
        if (vote == null) {
            return null;
        }
        VoteDTO dto = new VoteDTO();
        dto.setId(vote.getId());
        dto.setPriceGuess(vote.getPriceGuess());
        dto.setItemName(vote.getItem().getName());
        dto.setUserName(vote.getPlayer().getUsername());
        return dto;
    }

    public Vote toEntity(VoteDTO dto) {
        if (dto == null) {
            return null;
        }
        Vote vote = new Vote();
        vote.setId(dto.getId());
        vote.setPriceGuess(dto.getPriceGuess());

        Player player = playerRepository.findById(dto.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getUserName()));
        Item item = itemRepository.findById(dto.getItemName())
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + dto.getItemName()));


        vote.setPlayer(player);
        vote.setItem(item);
        return vote;
    }
}
