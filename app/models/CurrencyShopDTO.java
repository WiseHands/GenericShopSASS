package models;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.GenericGenerator;
import play.db.jpa.GenericModel;
import util.CurrencySign;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CurrencyShopDTO extends GenericModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Expose
    public String uuid;

    @Expose
    public String currency;

    @OneToOne(cascade=CascadeType.ALL)
    public ShopDTO shop;

    @Expose
    @OneToMany(cascade=CascadeType.ALL)
    public List<CurrencyDTO> currencyList;

    public void addCurrency(CurrencyDTO currency){
        if (this.currencyList == null){
            this.currencyList = new ArrayList<CurrencyDTO>();
        }
        this.currencyList.add(currency);
    }

    public CurrencyShopDTO(ShopDTO shop) {
        this.shop = shop;
        this.currency = "UAH";
        this.currencyList = new ArrayList<CurrencyDTO>();
    }

    public String currencyFormat(){
        String currencySign = "";
        String currency = this.currency.toLowerCase();
        Class classCurrencySign = CurrencySign.class;
        Field field = null;
        try {
            field = classCurrencySign.getDeclaredField(currency);
            currencySign = field.getName();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return currencySign;
    }








}
