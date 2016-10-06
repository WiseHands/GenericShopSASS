package models;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.GenericGenerator;
import play.db.jpa.GenericModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class UserDTO extends GenericModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Expose
    public String uuid;

    @Expose
    public String googleId;

    @Expose
    public String name;

    @Expose
    public String phone;

    @Expose
    public String givenName;

    @Expose
    public String familyName;

    @Expose
    public String locale;


    @Expose
    public String profileUrl;

    @Expose
    public String email;

    public String password;

    @Expose
    @ManyToMany(cascade=CascadeType.ALL)
    public List<ShopDTO> shopList;

    @Column
    public String token;

    public UserDTO() {
        this.token = UUID.randomUUID().toString();
        this.shopList = new ArrayList<ShopDTO>();
    }

    public UserDTO(String email, String password, String phone) {
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.token = UUID.randomUUID().toString();
        this.shopList = new ArrayList<ShopDTO>();
    }

}
