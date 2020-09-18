package services;

import liqp.Template;
import models.*;
import org.apache.commons.mail.HtmlEmail;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.Mail;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class MailSenderImpl implements MailSender {
    private static final boolean isDevEnv = Boolean.parseBoolean(Play.configuration.getProperty("dev.env"));
    private static final String gmailUsername = Play.configuration.getProperty("mail.smtp.user");
    private static final String gmailPass = Play.configuration.getProperty("mail.smtp.pass");

    static Properties mailServerProperties;
    static Session getMailSession;
    static MimeMessage generateMailMessage;

    public void sendEmail(ShopDTO shop, OrderDTO order, String status, String htmlContent) throws Exception {
        //System.out.println("MailSenderImpl " + isDevEnv + status + shop.contact.email);
//        if (!isDevEnv) {
        HtmlEmail email = new HtmlEmail();
        email.setHostName(shop.domain);
        email.setFrom("wisehandsme@gmail.com");
        //System.out.println("AddTo: " + shop.contact.email);
        email.addTo(shop.contact.email);
        email.setSubject(status);

        email.setHtmlMsg(htmlContent);
        email.setCharset("utf-8");
        Mail.send(email);
    }

    public void sendEmailLowShopBalance(ShopDTO shop, String status) throws Exception {
        //System.out.println("MailSenderImpl " + isDevEnv + status + shop.contact.email);
//        if (!isDevEnv) {
        HtmlEmail email = new HtmlEmail();
        email.setHostName(shop.domain);
        email.setFrom("wisehandsme@gmail.com");
        //System.out.println("AddTo: " + shop.contact.email);
        email.addTo(shop.contact.email);
        email.setSubject(status);

        String templateString = readAllBytesJava7("app/emails/email_low_shop_balance.html");
        Template template = Template.parse(templateString);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("shopName", shop.shopName);
        map.put("balance", shop.coinAccount.balance);

        Lang.change(shop.locale);
        String lowBalanceLabel = Messages.get("balance.transaction.low.shop.balance");
        map.put("lowBalanceLabel", lowBalanceLabel);

        String shopBalanceLabel = Messages.get("shop.balance");
        map.put("shopBalanceLabel", shopBalanceLabel);
        String currencyLabel = Messages.get("shop.balance.currency");
        map.put("currencyLabel", currencyLabel);

        String loginLabel = Messages.get("shop.login");
        map.put("loginLabel", loginLabel);

        String rendered = template.render(map);

        email.setHtmlMsg(rendered);
        email.setCharset("utf-8");
        Mail.send(email);
    }

    public void sendEmailForFeedbackToOrder(ShopDTO shop, OrderDTO order, String status, String clientLanguage) throws Exception {
        HtmlEmail email = new HtmlEmail();
        email.setHostName(shop.domain);
        email.setFrom("wisehandsme@gmail.com");
        email.addTo(order.email);
        email.setSubject(status);

        String templateString = readAllBytesJava7("app/emails/email_feedback_to_order.html");
        Template template = Template.parse(templateString);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("shopName", shop.shopName);
        map.put("orderUuid", order.uuid);
        String path = shop.domain;
        if(isDevEnv) {
            path = path + ":3334";
        }
        map.put("shopDomain", path);

        Lang.change(clientLanguage);// set user language

        String hiClient = Messages.get("feedback.main.label", order.name);
        map.put("hiClient", hiClient);

        String helpUs = Messages.get("feedback.email.text", shop.shopName);
        map.put("helpUs", helpUs);

        String writeFeedback = Messages.get("feedback.write.feedback");
        map.put("writeFeedback", writeFeedback);

        String rendered = template.render(map);

        email.setHtmlMsg(rendered);
        email.setCharset("utf-8");
        Mail.send(email);
    }


    public void sendEmailCommentForFeedback(ShopDTO shop, String customerMail, String customerName, ProductDTO product, String status) throws Exception {
        HtmlEmail email = new HtmlEmail();

        email.setHostName(shop.domain);
        email.setFrom("wisehandsme@gmail.com");
        email.addTo(customerMail);
        email.setSubject(status);

        String templateString = readAllBytesJava7("app/emails/email_comment_to_feedback.html");
        Template template = Template.parse(templateString);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("shopName", shop.shopName);
        map.put("productUuid", product.uuid);

        String path = shop.domain;
        if(isDevEnv) {
            path = path + ":3334";
        }
        map.put("shopDomain", path);

        Lang.change(shop.locale);
        String hiClient = Messages.get("feedback.main.label", customerName);
        map.put("hiClient", hiClient);

        String answer = Messages.get("feedback.email.answer", shop.shopName);
        map.put("answer", answer);

        String revise = Messages.get("feedback.email.revise");
        map.put("revise", revise);

        String rendered = template.render(map);

        email.setHtmlMsg(rendered);
        email.setCharset("utf-8");
        Mail.send(email);
    }

    public void sendNotificationToAdminAboutFeedback(ShopDTO shop, OrderDTO order, String status) throws Exception {
        HtmlEmail email = new HtmlEmail();

        email.setHostName(shop.domain);
        email.setFrom(order.email);
        email.addTo(shop.contact.email);
        email.setSubject(status);

        String templateString = readAllBytesJava7("app/emails/email_notification _about_new_feedback_to_admin.html");
        Template template = Template.parse(templateString);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("shopName", shop.shopName);
        map.put("orderUuid", order.uuid);

        String path = shop.domain;
        if(isDevEnv) {
            path = path + ":3334";
        }
        map.put("shopDomain", path);

        Lang.change(shop.locale);
        String feedback = Messages.get("feedback.email.feedback", order.name);
        map.put("feedback", feedback);

        String revise = Messages.get("feedback.email.revise");
        map.put("revise", revise);

        String rendered = template.render(map);

        email.setHtmlMsg(rendered);
        email.setCharset("utf-8");
        Mail.send(email);
    }

    public static String readAllBytesJava7(String filePath)
    {
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return content;
    }

    public void sendEmailToInvitedUser(ShopDTO shop, UserDTO user) throws Exception {
//        if (!isDevEnv) {
//            String loginUrl = "http://";
//            if(isDevEnv) {
//                loginUrl += "localhost:3334/";
//            } else {
//                loginUrl += "wisehands.me/";
//            }
//
//            HtmlEmail email = new HtmlEmail();
//            email.setHostName(shop.domain);
//            email.setFrom("noreply@" + shop.domain);
//            email.addTo(user.email);
//
//            String title = Messages.get("user.added.to.shop.email.title", shop.shopName);
//            email.setSubject(title);
//            String msg = Messages.get("user.added.to.shop.email", shop.shopName);
//            email.setMsg(msg);
//
//            Mail.send(email);
//        }
    }

    public void sendContactUsEmail(String message) throws Exception {
        HtmlEmail email = new HtmlEmail();
        email.setHostName("wstore.pro");
        email.setFrom("wisehandsme@gmail.com");
        email.addTo("bohdaq@gmail.com");
        System.out.println("AddTo: " + "bohdaq@gmail.com");
        email.setSubject("Нове повідомлення");

        String templateString = readAllBytesJava7("app/emails/contact_us_email_form.html");
        Template template = Template.parse(templateString);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("label", message);

        String rendered = template.render(map);

        email.setHtmlMsg(rendered);
        email.setCharset("utf-8");
        Mail.send(email);
    }
}
