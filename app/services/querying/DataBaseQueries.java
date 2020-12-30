package services.querying;

import models.*;
import play.db.jpa.JPA;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;

public class DataBaseQueries {

    public static double changePriceAccordingToCurrency(ProductDTO product, ShopDTO shop, String selectedCurrency){

        CurrencyDTO currency;

        CurrencyShopDTO currencyShop = CurrencyShopDTO.find("byShop", shop).first();
        if (!selectedCurrency.isEmpty()) {
            currencyShop.selectedCurrency = selectedCurrency;
            currencyShop.save();
        }
        boolean isSelectedEqualShopCurrency = currencyShop.selectedCurrency.equals(currencyShop.currencyShop);
        if (isSelectedEqualShopCurrency) {
            currency = CurrencyDTO.find("select c from CurrencyDTO c where c.base_ccy = ?1 and c.ccy = ?2", currencyShop.currencyShop, currencyShop.selectedCurrency).first();
        } else {
            currency = CurrencyDTO.find("select c from CurrencyDTO c where c.ccy = ?1", selectedCurrency).first();
            if (currency != null){
                product.price = product.price / currency.sale;
            }
        }
        shop.currencyShop = currencyShop;


        return product.formatPrice();
    }


    public static List<FeedbackDTO> getFeedbackList(ProductDTO product) {
        String query = "SELECT customerName, feedbackTime, quality, review, FeedbackCommentDTO.comment FROM FeedbackDTO" +
                " LEFT JOIN FeedbackCommentDTO" +
                " ON FeedbackDTO.feedbackComment_uuid = FeedbackCommentDTO.uuid" +
                " WHERE showReview = 1 and productUuid = '%s' order by feedbackTime desc";
        String feedbackListQuery = formatQueryString(query, product);
        List<Object[]> resultList = JPA.em().createNativeQuery(feedbackListQuery).getResultList();
        List<FeedbackDTO> feedbackResultList = new ArrayList<FeedbackDTO>();

        for (Object[] item: resultList){
            FeedbackDTO feedback = createFeedbackDTO(item);
            feedbackResultList.add(feedback);
        }

        return feedbackResultList;
    }

    private static FeedbackDTO createFeedbackDTO(Object[] item) {
        String customerName = (String) item[0];
        Long feedbackTime = Long.valueOf(String.valueOf(item[1]));
        String quality = (String) item[2];
        String review = (String) item[3];
        String comment = (String) item[4];
        System.out.println("FeedbackDTO => " + customerName + feedbackTime + quality + review + comment);
        FeedbackDTO feedback = new FeedbackDTO(quality, review, customerName, feedbackTime);
        feedback.comment = comment;
        return feedback;
    }

    private static String formatQueryString(String query, ProductDTO product) {
        String formattedQuery = String.format(
                query,
                product.uuid);
        return formattedQuery;
    }

}
