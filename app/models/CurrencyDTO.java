package models;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.GenericGenerator;
import play.db.jpa.GenericModel;

import javax.persistence.*;

@Entity
public class CurrencyDTO extends GenericModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Expose
    public String uuid;

    @Expose
    public String ccy;

    @Expose
    public String base_ccy;

    @Expose
    public double buy;

    @Expose
    public double sale;

    @ManyToOne(cascade = CascadeType.ALL)
    public CurrencyShopDTO currencyShop;

}
