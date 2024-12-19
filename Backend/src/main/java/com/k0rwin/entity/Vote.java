package com.k0rwin.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "price_guess")
    private Double priceGuess;
    @ManyToOne
    @JoinColumn(name = "player_id",  nullable = false)
    private Player player;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Item item;

    public Vote(Double priceGuess, Player player, Item item) {
        this.priceGuess = priceGuess;
        this.player = player;
        this.item = item;
    }

}
