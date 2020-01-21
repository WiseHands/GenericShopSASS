package models;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.GenericGenerator;
import play.db.jpa.GenericModel;

import javax.persistence.*;
import java.util.List;

@Entity
public class ProductDTO extends GenericModel {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Expose
    public String uuid;

    @Expose
    public String name;

    @Column( length = 100000 )
    @Expose
    public String description;

    @Expose
    public Double price;

    @Expose
    public String fileName;

    @ManyToOne
    public ShopDTO shop;

    @ManyToOne
    public CategoryDTO category;

    @Expose
    public String categoryName;

    @Expose
    public String categoryUuid;

    @Expose
    public Integer sortOrder;

    @Expose
    public Double oldPrice;

    @Expose
    public Boolean isActive;

    @Expose
    public Integer wholesaleCount;

    @Expose
    public Double wholesalePrice;

    @Expose
    @OneToOne
    public ProductImage mainImage;

    @Expose
    @OneToMany(orphanRemoval = true)
    public List<ProductPropertyDTO> properties;

    @Expose
    @OneToMany(orphanRemoval = true)
    public List<ProductImage> images;

    public ProductDTO(String name, String description, Double price, List<ProductImage> images, ShopDTO shop, Integer wholesaleCount, Double wholesalePrice) {
        this(name, description, price, images, shop, null, wholesaleCount, wholesalePrice);
    }
    public ProductDTO(String name, String description, Double price, List<ProductImage> images, ShopDTO shop, CategoryDTO category, Integer wholesaleCount, Double wholesalePrice) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.images = images;
        if (this.images != null) {
            this.mainImage = images.get(0);
            this.mainImage.save();
        }
        this.shop = shop;
        this.category = category;
        if (category != null) {
            this.categoryName = category.name;
            this.categoryUuid = category.uuid;
        }
        this.wholesaleCount = wholesaleCount;
        this.wholesalePrice = wholesalePrice;
    }

    public String formatDecimal() {
        Double number = this.price;
        float epsilon = 0.004f; // 4 tenths of a cent
        if (Math.abs(Math.round(number) - number) < epsilon) {
            return String.format("%10.0f", number); // sdb
        } else {
            return String.format("%10.2f", number); // dj_segfault
        }
    }
}
