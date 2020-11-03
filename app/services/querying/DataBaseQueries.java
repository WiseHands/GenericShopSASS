package services.querying;

import models.AdditionDTO;
import models.FeedbackDTO;
import models.ProductDTO;
import models.SelectedAdditionDTO;
import play.db.jpa.JPA;

import java.util.ArrayList;
import java.util.List;

public class DataBaseQueries {


//    public static List<OrderDTO> getOrderList

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

    public static List<SelectedAdditionDTO> checkIsAdditionDefaultToProduct(ProductDTO product) {
        List<SelectedAdditionDTO> additionList = new ArrayList<>();
        String additionIsDefaultQuery = "select a from SelectedAdditionDTO a where a.isDefault = 1 and a.productUuid = ?1";
        additionList = SelectedAdditionDTO.find(additionIsDefaultQuery, product.uuid).fetch();
        if (!additionList.isEmpty()){
            return additionList;
        } else {
            return additionList;
        }

    }

}
