package com.k0rwin.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Player {

    @Id
    @Column(name = "name", unique = true)
    private String username;
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Vote> votes = new ArrayList<>();
    @Column(name = "score")
    private Double score;

    public Player(String username) {
        this.score = 0.;
        this.username = username;
    }

    public Player(List<Vote> votes, Double score, String username) {
        this.votes = votes;
        this.score = score;
        this.username = username;
    }
}
