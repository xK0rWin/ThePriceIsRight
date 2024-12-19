package com.k0rwin.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Item {

    @Id
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "url")
    private String url;
    private Long id;
    @Column(name = "price")
    private Double price;
    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Vote> votes = new ArrayList<>();

    public Item(String name, String url, String status, Double price) {
        this.price = price;
        this.url = url;
        this.status = status;
        this.name = name;
    }
}
