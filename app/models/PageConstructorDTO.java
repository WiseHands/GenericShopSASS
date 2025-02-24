package models;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.GenericGenerator;
import play.db.jpa.GenericModel;

import javax.persistence.*;

@Entity
public class PageConstructorDTO  extends GenericModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Expose
    public String uuid;

    @Expose
    public String url;

    @Expose
    public String title;

    @Expose
    public Byte[] photoPage;

    @Expose
    @Lob
    @Column(length = 300000)
    public String body;

    @ManyToOne
    public ShopDTO shop;

    @Expose
    @OneToOne(cascade=CascadeType.ALL)
    public TranslationBucketDTO pageTitleTextTranslationBucket;

    @Expose
    @OneToOne(cascade=CascadeType.ALL)
    public TranslationBucketDTO pageBodyTextTranslationBucket;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public PageConstructorDTO(String url, String title, String body, ShopDTO shopDTO) {
        this.url = url;
        this.title = title;
        this.body = body;
        this.shop = shopDTO;
    }


    public Byte[] getPhotoPage() {
        return photoPage;
    }

    public void setPhotoPage(Byte[] photoPage) {
        this.photoPage = photoPage;
    }
}
