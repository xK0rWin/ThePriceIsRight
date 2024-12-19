package com.k0rwin.dto;

import com.k0rwin.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ItemMapper {

    @Autowired
    private VoteMapper voteMapper;

    public ItemDTO toDTO(Item item) {
        if (item == null) {
            return null;
        }
        ItemDTO dto = new ItemDTO();
        dto.setName(item.getName());
        dto.setPrice(item.getPrice());
        dto.setUrl(item.getUrl());
        dto.setStatus(item.getStatus());
        dto.setVotes(item.getVotes().stream()
                .map(voteMapper::toDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public Item toEntity(ItemDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Item(dto.getName(), dto.getUrl(), dto.getStatus(), dto.getPrice());
    }
}
