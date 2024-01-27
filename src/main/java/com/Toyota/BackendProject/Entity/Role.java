package com.Toyota.BackendProject.Entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="roles")
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Name",nullable = false)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL
            ,mappedBy = "roles" )
    private List<User> users;
}
