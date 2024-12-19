package com.k0rwin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {

    @JsonProperty("user_name")
    private String username;

    @JsonProperty("votes")
    private List<VoteDTO> votes;

    @JsonProperty("score")
    private Double score;
}
