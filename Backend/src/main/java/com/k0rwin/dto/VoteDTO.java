package com.k0rwin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("price_guess")
    private Double priceGuess;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("item_name")
    private String itemName;
}
