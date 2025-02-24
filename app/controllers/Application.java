package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import enums.FeedbackRequestState;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.*;

import models.*;
import services.querying.DataBaseQueries;
import services.translaiton.LanguageForShop;
import services.translaiton.Translation;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Application extends Controller {

    public static final boolean isDevEnv = Boolean.parseBoolean(Play.configuration.getProperty("dev.env"));
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static final int PAGE_SIZE = 6;

    @Before
    static void corsHeaders() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Accept, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");
    }

    public static void main(String client) {
        if(client.equals("wisehands.me") || isDevEnv) {
            String googleOauthClientId = Play.configuration.getProperty("google.oauthweb.client.id");
            String googleMapsApiKey = Play.configuration.getProperty("google.maps.api.key");
            String googleAnalyticsId = Play.configuration.getProperty("google.analytics.id");
            renderTemplate("WiseHands/index.html", googleOauthClientId, googleMapsApiKey, googleAnalyticsId);
        }
        redirect("https://wisehands.me/login", true);
    }

    private static String returnUrlForDev(String client, String language) {
        String protocol = "";
        String port = "";
        if(isDevEnv){
            protocol = "http://";
            port = ":3334";
        } else {
            protocol = "https://";
        }
        return protocol + client + port + "/" + language + "/shop";
    }

    public static void allowCors(){
        ok();
    }

    public static void login(String client) {
        if(client.equals("wisehands.me") || isDevEnv) {
            String googleOauthClientId = Play.configuration.getProperty("google.oauthweb.client.id");
            String googleMapsApiKey = Play.configuration.getProperty("google.maps.api.key");
            String googleAnalyticsId = Play.configuration.getProperty("google.analytics.id");
            renderTemplate("WiseHands/login.html", googleOauthClientId, googleMapsApiKey, googleAnalyticsId);
        }
        redirect("https://wisehands.me/login", true);
    }

    public static void uaSignup(String client) {
        String googleOauthClientId = Play.configuration.getProperty("google.oauthweb.client.id");
        String googleMapsApiKey = Play.configuration.getProperty("google.maps.api.key");
        String googleAnalyticsId = Play.configuration.getProperty("google.analytics.id");
        renderTemplate("Application/uaSignup.html", googleOauthClientId, googleMapsApiKey, googleAnalyticsId);
    }

    public static void uaSignin(String client) {
        String googleOauthClientId = Play.configuration.getProperty("google.oauthweb.client.id");
        String googleMapsApiKey = Play.configuration.getProperty("google.maps.api.key");
        String googleAnalyticsId = Play.configuration.getProperty("google.analytics.id");
        renderTemplate("Application/uaSignin.html", googleOauthClientId, googleMapsApiKey, googleAnalyticsId);
    }

    public static void uaShopLocation(String client) {
        String googleOauthClientId = Play.configuration.getProperty("google.oauthweb.client.id");
        String googleMapsApiKey = Play.configuration.getProperty("google.maps.api.key");
        String googleAnalyticsId = Play.configuration.getProperty("google.analytics.id");
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String language = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        renderTemplate("Application/uaShopLocation.html", googleOauthClientId, googleMapsApiKey, googleAnalyticsId, language);
    }

    public static void wisehands(String client) {
        String googleOauthClientId = Play.configuration.getProperty("google.oauthweb.client.id");
        String googleMapsApiKey = Play.configuration.getProperty("google.maps.api.key");
        String googleAnalyticsId = Play.configuration.getProperty("google.analytics.id");
        renderTemplate("WiseHands/index.html", googleOauthClientId, googleMapsApiKey, googleAnalyticsId);
    }

    public static void languageChooser(String client) {
        System.out.println("client info in languageChooser() => " + client);
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }

        if (client.equals("wisehands.me")){
            String googleOauthClientId = Play.configuration.getProperty("google.oauthweb.client.id");
            String googleMapsApiKey = Play.configuration.getProperty("google.maps.api.key");
            String googleAnalyticsId = Play.configuration.getProperty("google.analytics.id");
            renderTemplate("WiseHands/index.html", googleOauthClientId, googleMapsApiKey, googleAnalyticsId);
        }
        if (client.equals("wstore.pro")){
            String googleOauthClientId = Play.configuration.getProperty("google.oauthweb.client.id");
            String googleMapsApiKey = Play.configuration.getProperty("google.maps.api.key");
            String googleAnalyticsId = Play.configuration.getProperty("google.analytics.id");
            renderTemplate("Application/landing.html", googleOauthClientId, googleMapsApiKey, googleAnalyticsId);
        }

        generateCookieIfNotPresent(shop);

        Http.Header acceptLanguage = request.headers.get("accept-language");
        String language = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        Lang.change(language);
        System.out.println("LanguageForShop " + language);

        ProductDTO dishOfDay = ProductDTO.find("select p from ProductDTO p where p.isDishOfDay = ?1 and p.shop = ?2", true, shop).first();

        List<ProductDTO> products;
        String query = "select p from ProductDTO p, CategoryDTO c where p.category = c and p.shop = ?1 " +
                "and c.isHidden = ?2 and p.isActive = ?3 and p.isDishOfDay = false order by p.sortOrder asc";
        products = ProductDTO.find(query, shop, false, true).fetch();

        List<PageConstructorDTO> pageList = PageConstructorDTO.find("byShop", shop).fetch();
        List<PageConstructorDTO> translationPageList = new ArrayList<PageConstructorDTO>();
        for(PageConstructorDTO _page: pageList){
            _page = Translation.setTranslationForPage(language, _page);
            translationPageList.add(_page);
        }
        shop.pagesList = translationPageList;
        List<ProductDTO> productList = new ArrayList<ProductDTO>();

        String qr_uuid = "";
        if (request.params.get("qr_uuid") != null){
            qr_uuid = request.params.get("qr_uuid");
        }

        for (ProductDTO product : products) {
            product = Translation.setTranslationForProduct(language, product);
            productList.add(product);
            if(qr_uuid == null || qr_uuid.isEmpty()){
                int totalPriceForDefaultAdditions = DataBaseQueries.getTotalPriceForDefaultAdditions(product.uuid);
                product.priceWithAdditions = (double) totalPriceForDefaultAdditions;
            } else {
                DataBaseQueries.hideDefaultAddition(product);
            }

        }
        products = productList;


        List<CategoryDTO> categories = shop.getActiveCategories(language);
        Translation.setTranslationForShop(language, shop);

        if(client.equals("americano.lviv.ua")){
            renderTemplate("app/views/shopLanding/shopLanding.html", language, qr_uuid);
        }
        renderTemplate("Application/shop.html", shop, dishOfDay, products, language, categories, qr_uuid);
    }

    public static void index(String client, String language) {
        System.out.println("client info in index() =>" + client);
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        generateCookieIfNotPresent(shop);

        Date date = new Date();
        Http.Header xforwardedHeader = request.headers.get("x-forwarded-for");
        String ip = "";
        if (xforwardedHeader != null){
            ip = xforwardedHeader.value();
        }
        String agent = request.headers.get("user-agent").value();
        System.out.println("User with ip " + ip + " and user-agent " + agent + " opened shop " + shop.shopName + " at " + dateFormat.format(date));

        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        System.out.println("languageFromHeader" + languageFromHeader);
        language = LanguageForShop.setLanguageForShop(language, languageFromHeader);

        if (shop.isTemporaryClosed) {
            renderTemplate("Application/temporaryClosed.html", shop);
        }
        CoinAccountDTO coinAccount = CoinAccountDTO.find("byShop", shop).first();
        if (coinAccount != null && coinAccount.balance < 0) {
            renderTemplate("Application/closedDueToInsufficientFunds.html", shop);
        }

        List<ProductDTO> products;
        String query = "select p from ProductDTO p, CategoryDTO c where p.category = c " +
                "and p.shop = ?1 and c.isHidden = ?2 " +
                "and p.isActive = ?3 and p.isDishOfDay = false order by p.sortOrder asc";
        products = ProductDTO.find(query, shop, false, true).fetch();

        List<PageConstructorDTO> pageList = PageConstructorDTO.find("byShop", shop).fetch();
        List<PageConstructorDTO> translationPageList = new ArrayList<PageConstructorDTO>();
        for(PageConstructorDTO _page: pageList){
            _page = Translation.setTranslationForPage(language, _page);
            translationPageList.add(_page);
        }
        shop.pagesList = translationPageList;
        List<ProductDTO> productList = new ArrayList<ProductDTO>();


        String qr_uuid = "";
        if (request.params.get("qr_uuid") != null){
            qr_uuid = request.params.get("qr_uuid");
        }

        for (ProductDTO product : products) {
            product = Translation.setTranslationForProduct(language, product);
            productList.add(product);
            if(qr_uuid == null || qr_uuid.isEmpty()){
                int totalPriceForDefaultAdditions = DataBaseQueries.getTotalPriceForDefaultAdditions(product.uuid);
                product.priceWithAdditions = Double.valueOf(totalPriceForDefaultAdditions);
            } else {
                DataBaseQueries.hideDefaultAddition(product);
            }
        }
        products = productList;

        ProductDTO dishOfDay = ProductDTO.find("select p from ProductDTO p where p.isDishOfDay = ?1 and p.shop = ?2", true, shop).first();


        List<CategoryDTO> categories = shop.getActiveCategories(language);
        Translation.setTranslationForShop(language, shop);

        if(client.equals("americano.lviv.ua")){
            renderTemplate("app/views/shopLanding/shopLanding.html", language);
        }

        System.out.println("DEBUG renderTemplate Application/shop.html");
        renderTemplate("Application/shop.html", shop, dishOfDay, products, language, categories, qr_uuid);
    }

    public static void shopDefault(String client) {
        shop(client, "en");
    }
    public static void shop(String client, String language) {
        System.out.println("client info in shop() => " + client);

        Date date = new Date();

        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        generateCookieIfNotPresent(shop);

        Http.Header xforwardedHeader = request.headers.get("x-forwarded-for");
        String ip = "";
        if (xforwardedHeader != null){
            ip = xforwardedHeader.value();
        }
        String agent = request.headers.get("user-agent").value();
        System.out.println("User with ip " + ip + " and user-agent " + agent + " opened shop " + shop.shopName + " at " + dateFormat.format(date));

        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        System.out.println("languageFromHeader" + languageFromHeader);
        language = LanguageForShop.setLanguageForShop(language, languageFromHeader);

        if (shop.isTemporaryClosed) {
            renderTemplate("Application/temporaryClosed.html", shop);
        }

        CoinAccountDTO coinAccount = CoinAccountDTO.find("byShop", shop).first();
        if (coinAccount != null && coinAccount.balance < 0) {
            renderTemplate("Application/closedDueToInsufficientFunds.html", shop);
        }

        List<PageConstructorDTO> pageList = PageConstructorDTO.find("byShop", shop).fetch();
        List<PageConstructorDTO> translationPageList = new ArrayList<PageConstructorDTO>();
        for(PageConstructorDTO _page: pageList){
            _page = Translation.setTranslationForPage(language, _page);
            translationPageList.add(_page);
        }
        shop.pagesList = translationPageList;

        String qr_uuid = "";
        if (request.params.get("qr_uuid") != null){
            qr_uuid = request.params.get("qr_uuid");
        }

        List<ProductDTO> products;
        String query = "select p from ProductDTO p, CategoryDTO c where p.category = c " +
                "and p.shop = ?1 and c.isHidden = ?2 " +
                "and p.isActive = ?3 and p.isDishOfDay = false order by p.sortOrder asc";
        products = ProductDTO.find(query, shop, false, true).fetch();
        List<ProductDTO> productList = new ArrayList<ProductDTO>();
        for (ProductDTO product : products) {
            product = Translation.setTranslationForProduct(language, product);
            productList.add(product);
            if(qr_uuid == null || qr_uuid.isEmpty()){
                int totalPriceForDefaultAdditions = DataBaseQueries.getTotalPriceForDefaultAdditions(product.uuid);
                product.priceWithAdditions = (double) totalPriceForDefaultAdditions;
            } else {
                DataBaseQueries.hideDefaultAddition(product);
            }
        }
        products = productList;

        ProductDTO dishOfDay = ProductDTO.find("select p from ProductDTO p where p.isDishOfDay = ?1 and p.shop = ?2", true, shop).first();

        System.out.println("dishOfDay in Shop => " + dishOfDay);

        List<CategoryDTO> categories = shop.getActiveCategories(language);
        Translation.setTranslationForShop(language, shop);

        renderTemplate("Application/shop.html", shop, dishOfDay, products, language, categories, qr_uuid);
    }

    public static void categoryOld(String client, String uuid) {
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String language = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        redirect("https://" + client + "/" + language + "/category/" + uuid, false);
    }

    public static void category(String client, String uuid, String language){
        System.out.println("client info in category() => " + client);

        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }

        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        language = LanguageForShop.setLanguageForShop(language, languageFromHeader);

        CategoryDTO category = CategoryDTO.findById(uuid);
        List<CategoryDTO> categories = shop.getActiveCategories(language);

        ProductDTO dishOfDay = ProductDTO.find("select p from ProductDTO p where p.isDishOfDay = ?1 and p.shop = ?2", true, shop).first();

        List<ProductDTO> products;
        String query = "select p from ProductDTO p, CategoryDTO c where p.category = c and p.shop = ?1 " +
                "and c.isHidden = ?2 and p.isActive = ?3 " +
                "and p.isDishOfDay = false and p.categoryUuid = ?4 order by p.sortOrder asc";
        products = ProductDTO.find(query, shop, false, true, category.uuid).fetch();

        String qr_uuid = request.params.get("qr_uuid") != null ? request.params.get("qr_uuid") : "";

        List<ProductDTO> productList = new ArrayList<ProductDTO>();
        for (ProductDTO product : products) {
            product = Translation.setTranslationForProduct(language, product);
            productList.add(product);
            if(qr_uuid == null || qr_uuid.isEmpty()){
                int totalPriceForDefaultAdditions = DataBaseQueries.getTotalPriceForDefaultAdditions(product.uuid);
                product.priceWithAdditions = (double) totalPriceForDefaultAdditions;
            } else {
                DataBaseQueries.hideDefaultAddition(product);
            }

        }
        Translation.setTranslationForShop(language, shop);

        List<PageConstructorDTO> pageList = PageConstructorDTO.find("byShop", shop).fetch();
        List<PageConstructorDTO> translationPageList = new ArrayList<PageConstructorDTO>();
        for(PageConstructorDTO _page: pageList){
            _page = Translation.setTranslationForPage(language, _page);
            translationPageList.add(_page);
        }

        shop.pagesList = translationPageList;

        String urlToMain = "/" + language + "/shop";
        String urlToCategory = "/" + language + "/category/" + category.uuid;
        if (qr_uuid != null){
            urlToMain += "?qr_uuid=" + qr_uuid;
            urlToCategory  += "?qr_uuid=" + qr_uuid;
        }

        System.out.println("request.params qr_uuid.isEmpty() in category => " + qr_uuid);
        renderTemplate("Application/category.html", shop, category, categories, productList, dishOfDay,
                language, qr_uuid, urlToMain, urlToCategory);
    }

    public static void productOld(String client, String uuid) {
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String language = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        redirect("https://" + client + "/" + language + "/product/" + uuid, false);
    }

    public static void product(String client, String uuid, String language){
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null){
            shop = ShopDTO.find("byDomain", "localhost").first();
        }

        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        language = LanguageForShop.setLanguageForShop(language, languageFromHeader);

        List<PageConstructorDTO> pageList = PageConstructorDTO.find("byShop", shop).fetch();
        List<PageConstructorDTO> translationPageList = new ArrayList<PageConstructorDTO>();
        for(PageConstructorDTO _page: pageList){
            _page = Translation.setTranslationForPage(language, _page);
            translationPageList.add(_page);
        }
        shop.pagesList = translationPageList;

        ProductDTO product = ProductDTO.findById(uuid);
        CategoryDTO category = product.category;
        List<CategoryDTO> categories = shop.getActiveCategories(language);
        product.feedbackList = DataBaseQueries.getFeedbackList(product);

        String additionsListQuery = "select s from SelectedAdditionDTO s where s.isSelected = 1 and s.isDefault = 0 and s.productUuid = ?1";
        product.selectedAdditions = SelectedAdditionDTO.find(additionsListQuery, product.uuid).fetch();

        String qr_uuid = null;
        int totalPriceForDefaultAdditions = 0;
        List<SelectedAdditionDTO> defaultAdditions = new ArrayList<>();
        if (request.params.get("qr_uuid") != null){
            qr_uuid = request.params.get("qr_uuid");
            DataBaseQueries.hideDefaultAddition(product);
        }
        if(qr_uuid == null || qr_uuid.isEmpty()){
            totalPriceForDefaultAdditions = DataBaseQueries.getTotalPriceForDefaultAdditions(product.uuid);
            product.priceWithAdditions = Double.valueOf(totalPriceForDefaultAdditions);
            defaultAdditions = DataBaseQueries.checkIsAdditionDefaultToProduct(product);
            product.defaultAdditions = defaultAdditions;
        }
        System.out.println("request.params qr_uuid.isEmpty() in Product => " + qr_uuid);

        Translation.setTranslationForProduct(language, product);
        Translation.setTranslationForShop(language, shop);

        String mainShopUrl = "/" + language + "/shop";
        String urlToCategory = "/" + language + "/category/" + product.category.uuid;
        if (qr_uuid != null){
            mainShopUrl += "?qr_uuid=" + qr_uuid;
            urlToCategory  += "?qr_uuid=" + qr_uuid;
        }

        render(product, category, categories, shop, language,
               defaultAdditions, totalPriceForDefaultAdditions, qr_uuid, mainShopUrl, urlToCategory);
    }

    public static void shopHeader(String client){
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        String qr_uuid = "";
        if (request.params.get("qr_uuid") != null){
            qr_uuid = request.params.get("qr_uuid");
        }
        renderTemplate("tags/footer-shop.html", shop, qr_uuid);
    }

    public static void footerShop(String client){
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String language = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        System.out.println("footer-shop language => " + language);
        Translation.setTranslationForShop(language, shop);
        renderTemplate("tags/footer-shop.html", shop, language);
    }

    public static void shopNetworks(String client) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        ShopNetworkDTO network = shop.getNetwork();
        network.retrieveShopList();
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String language = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        render(shop, network, language);
    }

    public static void allProductsInShop(String client) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        Date date = new Date();
        Http.Header xforwardedHeader = request.headers.get("x-forwarded-for");
        String ip = "";
        if (xforwardedHeader != null){
            ip = xforwardedHeader.value();
        }
        String agent = request.headers.get("user-agent").value();
        System.out.println("User with ip " + ip + " and user-agent " + agent + " opened SHOP " + shop.shopName + " at " + dateFormat.format(date));
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String language = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        render(shop, language);
    }

    public static void selectAddress(String client, String language) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        language = LanguageForShop.setLanguageForShop(language, languageFromHeader);
        System.out.println("LanguageForShop" + language);

        List<PageConstructorDTO> pageList = PageConstructorDTO.find("byShop", shop).fetch();
        List<PageConstructorDTO> translationPageList = new ArrayList<PageConstructorDTO>();
        for(PageConstructorDTO _page: pageList){
            _page = Translation.setTranslationForPage(language, _page);
            translationPageList.add(_page);

        }
        List<CategoryDTO> categories = shop.getActiveCategories(language);
        shop.pagesList = translationPageList;
        render(shop, categories, language);
    }

    public static void pageOld(String client, String uuid) {
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String language = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        redirect("https://" + client + "/" + language + "/page/" + uuid, false);
    }

    public static void page(String client, String uuid, String language) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        System.out.println("page language from params " + language);
        language = LanguageForShop.setLanguageForShop(language, languageFromHeader);

        PageConstructorDTO page = PageConstructorDTO.findById(uuid);
        page = Translation.setTranslationForPage(language, page);
        List<PageConstructorDTO> pageList = PageConstructorDTO.find("byShop", shop).fetch();
        List<PageConstructorDTO> translationPageList = new ArrayList<PageConstructorDTO>();
        for(PageConstructorDTO _page: pageList){
            _page = Translation.setTranslationForPage(language, _page);
            translationPageList.add(_page);
        }
        shop.pagesList = translationPageList;
        List<CategoryDTO> categories = shop.getActiveCategories(language);
        Translation.setTranslationForShop(language, shop);
        System.out.println("page language " + language);
        render(shop, page, pageList, language, categories);
    }

    private static void generateCookieIfNotPresent(ShopDTO shop) {
        String agent = request.headers.get("user-agent").value();
        if (shop != null) {
            System.out.println("generateCookieIfNotPresent => " + shop.shopName);
            if (shop.shopName.isEmpty()) {
                System.out.println("generateCookieIfNotPresent => " + shop.domain);
            }
        }

        Http.Cookie userTokenCookie = request.cookies.get("JWT_TOKEN");
        if(userTokenCookie == null && shop != null) {
            ShoppingCartDTO shoppingCart = new ShoppingCartDTO();
            shoppingCart.shopUuid = shop.uuid;
            shoppingCart.save();
            System.out.println("prepare to generateTokenForCookie => " + agent);
            String token = generateTokenForCookie(shoppingCart.uuid, agent);
            System.out.println("generateTokenForCookie => " + token);
            //String duration = "30mn";
            //response.setCookie("JWT_TOKEN", token, duration);
            Integer maxAge = 1800;
            response.setCookie("JWT_TOKEN", token, shop.domain, "/", maxAge, false);
        }
    }

    public static String generateTokenForCookie(String shoppingCartId, String userAgent) {
        String token = "";
        try {
            String encodingSecret = Play.configuration.getProperty("jwt.secret");
            Algorithm algorithm = Algorithm.HMAC256(encodingSecret);

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            token = JWT.create()
                    .withIssuedAt(now)
                    .withSubject(shoppingCartId)
                    .withClaim("userAgent", userAgent)
                    .withIssuer("wisehands")
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return token;
    }

    public static void orderFeedback(String client, String uuid) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        OrderDTO order = OrderDTO.find("byUuid",uuid).first();
        boolean isSendRequest = order.feedbackRequestState.equals(FeedbackRequestState.REQUEST_SENT);
        String shopName = shop.shopName;

        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String language = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        List<OrderItemDTO> orderItemList = order.items;
        for(OrderItemDTO _orderItem: orderItemList){
            ProductDTO product = ProductDTO.findById(_orderItem.productUuid);
            product = Translation.setTranslationForProduct(language, product);
            _orderItem.name = product.name;
            _orderItem.description = product.description;
        }

        String currency = "UAH";
        renderTemplate("Application/orderFeedback.html", shop, order, isSendRequest, currency, language, shopName);
    }

    public static void shoppingCart(String client, String uuid, String language){
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null){
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        List<PageConstructorDTO> pageList = PageConstructorDTO.find("byShop", shop).fetch();
        List<PageConstructorDTO> translationPageList = new ArrayList<PageConstructorDTO>();
        for(PageConstructorDTO _page: pageList){
            _page = Translation.setTranslationForPage(language, _page);
            translationPageList.add(_page);
        }
        shop.pagesList = translationPageList;
        shop.pagesList = pageList;
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        language = LanguageForShop.setLanguageForShop(language, languageFromHeader);
        Translation.setTranslationForShop(language, shop);

        String qr_uuid = null;
        if (request.params.get("qr_uuid") != null){
            qr_uuid = request.params.get("qr_uuid");
        }

        List<CategoryDTO> categories = shop.getActiveCategories(language);

        render(shop, language, categories, qr_uuid);
    }

    public static void done(String client) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }

        DeliveryDTO delivery = shop.delivery;

        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String language = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        String orderMessage = Messages.get("page.done.delivery.order.message");
        System.out.println("orderMessage " + delivery.orderMessage);
        if(delivery.orderMessage == null || delivery.orderMessage.equals("")) {
            delivery.orderMessage = orderMessage;
            delivery = delivery.save();
        }

        String qr_uuid = "";
        if (request.params.get("qr_uuid") != null){
            qr_uuid = request.params.get("qr_uuid");
        }


        render(delivery, language, qr_uuid);
    }

    public static void fail(String client) {
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String languageForShop = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        render(languageForShop);
    }

    public static void admin(String client) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        Date date = new Date();
        Http.Header xforwardedHeader = request.headers.get("x-forwarded-for");
        String ip = "";
        if (xforwardedHeader != null){
            ip = xforwardedHeader.value();
        }
        String agent = request.headers.get("user-agent").value();
        System.out.println("User with ip " + ip + " and user-agent " + agent + " opened ADMIN " + shop.shopName + " at " + dateFormat.format(date));
        VisualSettingsDTO visualSettings = VisualSettingsDTO.find("byShop", shop).first();
        render(shop, visualSettings);
    }

    public static void superAdmin(String client) {
        render();
    }

    public static void marketing(String client) {
        render();
    }

    public static void sitemap(String client) throws IOException {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        Date date = new Date();
        Http.Header xforwardedHeader = request.headers.get("x-forwarded-for");
        String ip = "";
        if (xforwardedHeader != null){
            ip = xforwardedHeader.value();
        }
        String agent = request.headers.get("user-agent").value();
        System.out.println("User with ip " + ip + " and user-agent " + agent + " opened sitemap " + shop.shopName + " at " + dateFormat.format(date));
        renderTemplate("Prerender/" + shop.uuid + "/" + "sitemap.xml");
    }

    public static void manifestAdmin(String client) throws IOException {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            shop = ShopDTO.find("byDomain", "localhost").first();
        }
        render(shop);
    }

    public static void landing(String client){
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String languageForShop = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        render(languageForShop);
    }

    public static void uaContract(String client){
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String languageForShop = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        render(languageForShop);
    }

    public static void privacy(String client){
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String languageForShop = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        render(languageForShop);
    }

    public static void uaWizard(String client){
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String languageForShop = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        renderTemplate("Application/uaNewWizard.html", languageForShop);
    }

    public static void serverError(String client){
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String languageFromHeader = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        String languageForShop = LanguageForShop.setLanguageForShop(null, languageFromHeader);
        render(languageForShop);
    }

    public static void userDashboard(String client) {
        renderTemplate("wstore/userDashboard.html");
    }

    public static void termsofservice(String client){
        renderTemplate("WiseHands/termsofservice.html");
    }

    public static void privacypolicy(String client){
        renderTemplate("WiseHands/privacypolicy.html");
    }

    public static void cookiespolicy(String client){
        renderTemplate("WiseHands/cookiespolicy.html");
    }

    public static void refunds(String client){
        renderTemplate("WiseHands/refunds.html");
    }

    public static void hireFrontendDevelopers(String client){
        renderTemplate("WiseHands/Services/hireFrontendDevelopers.html");
    }
    public static void hireBackendDevelopers(String client){
        renderTemplate("WiseHands/Services/hireBackendDevelopers.html");
    }
    public static void hireDevopsDevelopers(String client){
        renderTemplate("WiseHands/Services/hireDevopsDevelopers.html");
    }
    public static void hireMobileDevelopers(String client){
        renderTemplate("WiseHands/Services/hireMobileDevelopers.html");
    }
    public static void hireReactDevelopers(String client){
        renderTemplate("WiseHands/Services/hireReactDevelopers.html");
    }
    public static void hireAngularDevelopers(String client){
        renderTemplate("WiseHands/Services/hireAngularDevelopers.html");
    }
    public static void hireVuejsDevelopers(String client){
        renderTemplate("WiseHands/Services/hireVuejsDevelopers.html");
    }
    public static void hirePolymerDevelopers(String client){
        renderTemplate("WiseHands/Services/hirePolymerDevelopers.html");
    }
    public static void hireGoDevelopers(String client){
        renderTemplate("WiseHands/Services/hireGoDevelopers.html");
    }

    public static void hireJavaDevelopers(String client){
        renderTemplate("WiseHands/Services/hireJavaDevelopers.html");
    }
    public static void hireNodejsDevelopers(String client){
        renderTemplate("WiseHands/Services/hireNodejsDevelopers.html");
    }
    public static void hirePythonDevelopers(String client){
        renderTemplate("WiseHands/Services/hirePythonDevelopers.html");
    }
    public static void hireAwsDevelopers(String client){
        renderTemplate("WiseHands/Services/hireAwsDevelopers.html");
    }
    public static void hireGooglecloudDevelopers(String client){
        renderTemplate("WiseHands/Services/hireGooglecloudDevelopers.html");
    }
    public static void hireAzureDevelopers(String client){
        renderTemplate("WiseHands/Services/hireAzureDevelopers.html");
    }
    public static void hireKubernetesDevelopers(String client){
        renderTemplate("WiseHands/Services/hireKubernetesDevelopers.html");}
    public static void hireIosDevelopers(String client){
        renderTemplate("WiseHands/Services/hireIosDevelopers.html");
    }

    public static void hireAndroidDevelopers(String client){
        renderTemplate("WiseHands/Services/hireAndroidDevelopers.html");
    }
    public static void shopLanding(){
        Http.Header acceptLanguage = request.headers.get("accept-language");
        String language = LanguageForShop.getLanguageFromAcceptHeaders(acceptLanguage);
        renderTemplate("app/views/shopLanding/shopLanding.html", language);
    }
    public static void qrCode(String client){
        renderTemplate("Application/qrCode.html");
    }
    public static void contactForm(String client){
        renderTemplate("Application/contactForm.html");
    }
}