package com.Toyota.sale.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="categories")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "category")
    private List<Product> products;

    @OneToMany(mappedBy = "category")
    private List<Campaign> campaigns;

}
