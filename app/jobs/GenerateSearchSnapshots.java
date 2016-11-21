package jobs;

import models.*;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@OnApplicationStart
public class GenerateSearchSnapshots extends Job {
    private static final boolean isDevEnv = Boolean.parseBoolean(Play.configuration.getProperty("dev.env"));

    private static final Executor executorMan = Executors.newFixedThreadPool(10);


    public void doJob() throws Exception {

        if (!isDevEnv) {
            List<ShopDTO> allShops = ShopDTO.findAll();
            for (ShopDTO shop: allShops){
                generateSnapshotsForShop(shop);
            }
        }


    }

    private void generateSnapshotsForShop(ShopDTO shop) {
        String shopUuid = shop.uuid;


        String createShopDirectory = "mkdir -p app/views/Prerender/" + shopUuid + "/product" ;
        System.out.println(createShopDirectory);
        executeCommand(createShopDirectory);

        String createCategoriesDirectory = "mkdir -p app/views/Prerender/" + shopUuid + "/category" ;
        System.out.println(createCategoriesDirectory);
        executeCommand(createCategoriesDirectory);


        List<ProductDTO> products = ProductDTO.find("byShop", shop).fetch();
        List<String> urls = new ArrayList<String>();
        for (ProductDTO product : products) {
            String url = "http://" + shop.domain + "/#!/product/" + product.uuid;
            urls.add(url);

            final String command = "phantomjs makesnap.js " + shop.domain + " product/" + product.uuid + " > app/views/Prerender/" + shopUuid + "/product/"  + product.uuid;
            System.out.println("command: " + command);

            Runnable task = new Runnable() {
                public void run() {
                    System.out.println("run command: " + command);
                    executeCommand(command);
                }
            };
            executorMan.execute(task);
        }
        List<CategoryDTO> categories = CategoryDTO.find("byShop", shop).fetch();
        for (CategoryDTO category : categories) {
            String url = "http://" + shop.domain + "/#!/category/" + category.uuid;
            urls.add(url);

            final String command = "phantomjs makesnap.js " + shop.domain + " category/" + category.uuid + " > app/views/Prerender/" + shopUuid + "/category/"  + category.uuid;
            System.out.println("command: " + command);

            Runnable task = new Runnable() {
                public void run() {
                    System.out.println("run command: " + command);
                    executeCommand(command);
                }
            };
            executorMan.execute(task);
        }

        urls.add("http://" + shop.domain + "/#!/contacts");
        System.out.println(urls);
    }

    private void executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(output.toString());

    }

}
