package com.Toyota.rapor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="campaigns")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name",length = 50,nullable = false)
    private String name;
    @Column(name="description",length = 255)
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "campaign",cascade = CascadeType.ALL)
    private List<SoldProduct>soldProducts;

}
