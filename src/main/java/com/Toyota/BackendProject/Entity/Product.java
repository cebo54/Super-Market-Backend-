package com.Toyota.BackendProject.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="products")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name",length = 50,nullable = false)
    private String name;
    @Column(name="price",length = 50,nullable = false)
    private double price;
    @Column(columnDefinition ="VARCHAR(255)",name="barcode")
    private String barcode;
    @Column(name="img")
    private byte[] img;
    @Column(name="stock",length = 20,nullable = false)
    private int stock;
    @Column(name="brand",length = 50,nullable = true)
    private String brand;
    @Column(name="description",length = 255)
    private String description;
    @Column(name="expiration_date",length = 10,nullable = false)
    private Date expiration_date;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="category_id")
    private Category category;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "product")
    private List<SoldProduct> SoldProducts;

    @Column(name="is_Active")
    private boolean isActive;


    public void setImgProp(byte[] img){
        this.img=img;
    }

}
