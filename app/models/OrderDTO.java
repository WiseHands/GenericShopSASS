package models;

import com.google.gson.annotations.Expose;
import enums.OrderState;
import org.hibernate.annotations.GenericGenerator;
import play.db.jpa.GenericModel;

import javax.persistence.*;
import java.util.List;

@Entity
public class OrderDTO extends GenericModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Expose
    public String uuid;

    @Expose
    public String name;

    @Expose
    public String phone;

    @Expose
    public String address;

    @Expose
    public String deliveryType;

    @Expose
    public String departmentNumber;

    @Expose
    public Long time;

    @Expose
    public Double total;

    @Expose
    public String comment;

    @Expose
    @Enumerated(EnumType.STRING)
    public OrderState state;

    @Expose
    @OneToMany(cascade = CascadeType.ALL)
    public List<OrderItemDTO> items;

    @ManyToOne(cascade=CascadeType.ALL)
    public ShopDTO shop;

    public OrderDTO(String name, String phone, String address, String deliveryType, String departmentNumber, String comment, ShopDTO shop) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.deliveryType = deliveryType;
        this.departmentNumber = departmentNumber;
        this.time = System.currentTimeMillis();
        this.state = OrderState.NEW;
        this.comment = comment;
        this.shop = shop;
    }

    private String returnIfNotNull(String target){
        if(target != null){
            return target;
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return  "Name: " + this.name + "\n" +
                "Phone: " + this.phone +  "\n" +
                "Delivery: " + this.deliveryType + "\n" +
                "Address: " + returnIfNotNull(this.address) + "\n" +
                "Department: " + returnIfNotNull(this.departmentNumber) + "\n" +
                "Total: " + total + "\n" +
                "Details: " + "http://" + this.shop.domain + "/admin#/details/" + this.uuid;
    }
}
