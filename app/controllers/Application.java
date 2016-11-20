package controllers;

import play.mvc.*;

import models.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Application extends Controller {

    @Before
    static void corsHeaders() {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Expose-Headers", "X-AUTH-TOKEN");
    }

    public static void login(String client) {
        if (client.equals("localhost")){
            renderTemplate("WiseHands/index.html");
        } else {
            redirect("http://wisehands.me");
        }
    }

    public static void index(String client) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();

        if (client.equals("localhost") || client.equals("wisehands.me")){
            renderTemplate("WiseHands/index.html");
        }

        if (shop == null) {
            notFound("The requested Shop is not available. Contact administrator");
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();


        Http.Header xforwardedHeader = request.headers.get("x-forwarded-for");
        String ip = "";
        if (xforwardedHeader != null){
            ip = xforwardedHeader.value();
        }

        String agent = request.headers.get("user-agent").value();
        System.out.println("User with ip " + ip + " and user-agent " + agent + " opened shop " + shop.shopName + " at " + dateFormat.format(date));

        boolean isGoogleCrawler = request.params.data.containsKey("_escaped_fragment_");
        if (isGoogleCrawler) {
            renderTemplate("Prerender/" + shop.uuid + "/index.html");

        }

        renderTemplate("Application/shop.html", shop);
    }

    public static void shop(String client) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            notFound("The requested Shop is not available. Contact administrator    ");
        }


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        Http.Header xforwardedHeader = request.headers.get("x-forwarded-for");
        String ip = "";
        if (xforwardedHeader != null){
            ip = xforwardedHeader.value();
        }
        String agent = request.headers.get("user-agent").value();
        System.out.println("User with ip " + ip + " and user-agent " + agent + " opened SHOP " + shop.shopName + " at " + dateFormat.format(date));


        render(shop);
    }

    public static void done(String client) {
        render();
    }

    public static void fail(String client) {
        render();
    }

    public static void admin(String client) {
        ShopDTO shop = ShopDTO.find("byDomain", client).first();
        if (shop == null) {
            notFound("The requested Shop is not available. Contact administrator    ");
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        Http.Header xforwardedHeader = request.headers.get("x-forwarded-for");
        String ip = "";
        if (xforwardedHeader != null){
            ip = xforwardedHeader.value();
        }

        String agent = request.headers.get("user-agent").value();
        System.out.println("User with ip " + ip + " and user-agent " + agent + " opened ADMIN " + shop.shopName + " at " + dateFormat.format(date));

        render();
    }

    public static void superAdmin(String client) {
        render();
    }


}
