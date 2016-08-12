package jobs;

import models.DeliveryDTO;
import models.ProductDTO;
import models.ShopDTO;
import models.UserDTO;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class DatastoreSetup extends Job {
    private static final String SVYAT = "sviatoslav.p5@gmail.com";
    private static final String BOGDAN = "bohdaq@gmail.com";
    private static final String VOVA = "patlavovach@gmail.com";

    private static final String HAPPYBAG_PUBLIC_LIQPAY_KEY = "i65251982315";
    private static final String HAPPYBAG_PRIVATE_LIQPAY_KEY = "NLsgd1zKW30EvBkPNeuQodXzmvcA7shcrQ7o0Mbs";


    private static final String PASSWORD = "rjylbnth";


    public void doJob() throws Exception {
        boolean isDevEnv = Boolean.parseBoolean(Play.configuration.getProperty("dev.env"));
        boolean isDBEmpty = UserDTO.findAll().size() == 0;
        if (isDBEmpty){
            UserDTO user = new UserDTO(SVYAT, PASSWORD);
            user.save();

            if (isDevEnv) {
                createShop(user, "wisehands", "localhost");
            } else {
                createShop(user, "HappyBag", "happybag.me");
            }

            user = new UserDTO(BOGDAN, PASSWORD);
            user.save();
            if (isDevEnv) {
                createShop(user, "wisehands", "localhost");
            } else {
                createShop(user, "HappyBag", "happybag.me");
            }

            user = new UserDTO(VOVA, PASSWORD);
            user.save();
            if (isDevEnv) {
                createShop(user, "wisehands", "localhost");
            } else {
                createShop(user, "HappyBag", "happybag.me");
            }

        }
    }

    private void createShop(UserDTO user, String shopName, String domain) {
        DeliveryDTO delivery = new DeliveryDTO(
                true, "Викликати кур’єра по Львову – 35 грн або безкоштовно (якщо розмір замовлення перевищує 500 грн.)",
                true, "Самовивіз",
                true, "Замовити доставку до найближчого відділення Нової Пошти у Вашому місті (від 35 грн.)"
                );
        delivery.save();

        ShopDTO shop = new ShopDTO(user, delivery, shopName, HAPPYBAG_PUBLIC_LIQPAY_KEY, HAPPYBAG_PRIVATE_LIQPAY_KEY, domain);
        shop.save();
        createProducts(shop, domain);
    }

    private void createProducts(ShopDTO shop, String domain) {
        ProductDTO productDTO = new ProductDTO(
                "Шоколадки з передбаченнями «ТОРБА ЩАСТЯ»",
                "Це чарівний мішечок. Всередині нього сім з любов’ю упакованих чудо-шоколадок з передбаченням. Кожна шоколадка вагою 12,5. Дуже смачний чорний шоколад з вмістом какао 73% та ніжною трюфельною начинкою. Люди кажуть, що дуже смачно. Кожна шоколадка з унікальним креативним та добрим побажанням, яке дублюється двома мовами українською та англійською. Різновид-понад 1500 штук, тому ймовірність повторів мізерно мала. І кожна Торба Щастя з своїм унікальним набором. Натуральна полотняна тканина дуже приємна на дотик та підкреслює цінність подарунку. І таке приємне відчуття розв’язати торбинку і витягнути шоколадку з побажанням-передбаченням, яке адресується саме Тобі. Ми самі не знаємо як таке трапляється, але магія, безперечно, присутня. Всі, хто куштує шоколад і читає побажання, відзначають, що воно призначалось саме йому. Тому, можемо дозволити собі стверджувати, що Торба Щастя, мабуть, найкращий солодкий подарунок, який не тільки чудово смакує, але й дарує позитивні емоції.",
                (double) 60,
                domain + "/item0.jpg",
                shop
        );
        productDTO.save();

        productDTO = new ProductDTO(
                "Шоколадка з передбаченням",
                "Дуже смачний чорний шоколад з вмістом какао 73% та ніжною трюфельною начинкою. Люди кажуть, що дуже смачно. Кожна шоколадка з унікальним креативним та добрим побажанням, яке дублюється двома мовами українською та англійською. Різновид-понад 1500 шт. , тому ймовірність повторів мізерно мала. Саме завдяки відмінному смаку і цим магічним порадам, які завжди потрапляють точно в ціль, ці шоколадки полюбились людьми. Завжди цікаво, що ж вони підкажуть цього разу.",
                (double) 7,
                domain + "/item1.jpg",
                shop
        );
        productDTO.save();

        productDTO = new ProductDTO(
                "Набір шоколадок «7 сторін моєї любові»",
                "Набір з семи шоколадок \"7 сторін моєї любові\". Дуже смачний чорний шоколад з ніжною трюфельною начинкою. В кожній шоколадці добрі слова адресовані Вашій коханій людині. Наприклад: «Від однієї твоєї посмішки зникає весь мій смуток», «Ти завжди радуєш мене просто тим, що ти є», «Немає нічого солодшого твоїх поцілунків», «Твоє щастя найважливіше для мене» та багато інших. Усі можуть адресуватись, як для «нього», так і для «неї». Різновид-понад 100 штук. І в кожному наборі своє унікальне різноманіття шоколадок. Тому, можемо дозволити собі стверджувати, що «7 сторін моєї любові», мабуть, найкращий солодкий подарунок, щоб виразити свої почуття.",
                (double) 50,
                domain + "/item2.jpg",
                shop
        );
        productDTO.save();

        productDTO = new ProductDTO(
                "Набір шоколадок «7 сторін Любові»",
                "Набір з семи шоколадок \"7 сторін любові\". Дуже смачний чорний шоколад з ніжною трюфельною начинкою. В кожній шоколадці слова на тему Любові. Наприклад: «Любов не ланцюги, а хвилююче море між берегами ваших душ.», «Любов - нескінченна дорога, по якій двоє йдуть назустріч один одному.», ««Ми» важливіше, ніж «Ти» чи «Я»!» Різновид-понад 100 штук. І в кожному наборі своє унікальне різноманіття шоколадок. Тому, можемо дозволити собі стверджувати, що «7 сторін любові», мабуть, найкращий солодкий подарунок.",
                (double) 50,
                domain + "/item3.jpg",
                shop
        );
        productDTO.save();

        productDTO = new ProductDTO(
                "Набір шоколадок «БУДДА»",
                "Набір з семи шоколадок. Дуже смачний чорний шоколад з ніжною трюфельною начинкою. В кожній шоколадці слова Будди. Наприклад: «Те, що ми є сьогодні, - це наслідок наших вчорашніх думок», «Хто не зрозумів свого минулого, змушений пережити його знову», «Щастя не залежить від того, хто ви є або що у вас є. Воно залежить виключно від того, що ви думаєте» та багато інших. І в кожному наборі своє різноманіття шоколадок з унікальними словами мудрості не підвладних часу.",
                (double) 50,
                domain + "/item4.jpg",
                shop
        );
        productDTO.save();

        productDTO = new ProductDTO(
                "Набір шоколадок «DRUZI»",
                "Набір з семи шоколадок. Дуже смачний чорний шоколад з ніжною трюфельною начинкою. В кожній шоколадці слова на тему Дружби. Наприклад: «Друзі - це ті рідкісні люди, які питаючи, як у тебе справи, дійсно чекають відповіді», «Справжній друг - це той, хто буде тримати тебе за руку, і відчувати твоє серце», «Друг - це той, кому ти можеш подзвонити о 4 годині ранку» та багато інших. І в кожному наборі своє різноманіття шоколадок з унікальними фразами. Тому, можемо дозволити собі стверджувати, що «DRUZI», мабуть, найкращий солодкий подарунок Вашому другу чи подрузі.",
                (double) 50,
                domain + "/item5.jpg",
                shop
        );
        productDTO.save();

        productDTO = new ProductDTO(
                "Набір шоколадок «Маленький принц»",
                "Набір з семи шоколадок. Дуже смачний чорний шоколад з ніжною трюфельною начинкою. В кожній шоколадці слова з книги Антуана де Сент-Екзюпері «Маленький принц». Наприклад: «Уважне одне лише серце. Найголовнішого очима не побачиш», «Ми відповідальні за тих, кого приручили...» та багато інших. І в кожному наборі своє різноманіття шоколадок з унікальними фразами. Це магічна книга, в якій кожного разу, знаходиш щось нове та близьке душі. Тому, можемо дозволити собі стверджувати, що «Маленький принц», мабуть, найкращий солодкий та добрий подарунок.",
                (double) 50,
                domain + "/item6.jpg",
                shop
        );
        productDTO.save();

        productDTO = new ProductDTO(
                "Набір шоколадок «Мафія»",
                "Набір з семи шоколадок. Дуже смачний чорний шоколад з трюфельною начинкою. В кожній шоколадці принципи лідерства менеджерів мафії. Наприклад: «Не пробуй глибини річки двома ногами», «Орел не полює на мух», «Одним зернятком не наповниш мішок…але можна почати», «Відкривай свій рот і свій гаманець з обережністю» та багато інших по- справжньому дієвих порад «сім’ї», перлини потаємної мудрості, які є наслідком відмінного розуміння людської природи, а не примітивними проявами кримінального характеру. В кожному наборі своє унікальне різноманіття шоколадок з порадами.",
                (double) 50,
                domain + "/item7.jpg",
                shop
        );
        productDTO.save();

        productDTO = new ProductDTO(
                "Печиво з передбаченням",
                "Від тепер печиво з передбаченням-це ще й дуже смачно та корисно! Хрумке імбирне печиво з найякісніших інгредієнтів випікається кондитерами, які люблять людей та свою справу. Кожна імбирка з унікальним креативним та добрим побажанням, яке дублюється двома мовами українською та англійською. Передбачення та побажання несуть найкращі позитивні думки та ідеї, які завжди потрапляють точно в ціль. Саме завдяки цій магії та відмінному смаку вони так полюбились людьми. Завжди цікаво, що ж вони підкажуть цього разу.",
                (double) 6,
                domain + "/item8.jpg",
                shop
        );
        productDTO.save();

        productDTO = new ProductDTO(
                "Печиво з передбаченнями «ІМБИРКИ ЗІ ЛЬВОВА»",
                "В місті Лева все переповнене магією та романтикою. Навіть, найбільші скептики починають вірити в чудеса, хоча б раз вдихнувши його повітря. І не дивно, що саме тут було створено печиво з передбаченнями абсолютно нового формату з дійсно якісним змістом та смаком, який задовільнить найвибагливіших гурманів. Від тепер печиво з передбаченням-це дуже смачно та корисно! Хрумке імбирне з найякісніших інгредієнтів випікається кондитерами, які люблять людей та свою справу. Кожна імбирка з унікальним креативним та добрим побажанням, яке дублюється двома мовами українською та англійською. Передбачення та побажання несуть найкращі позитивні думки та ідеї, які завжди потрапляють точно в ціль. Саме завдяки цій магії та відмінному смаку вони так полюбились людьми. Магія та романтика міста втілились в наборі печива з передбаченнями «ІМБИРКИ ЗІ ЛЬВОВА».",
                (double) 50,
                domain + "/item9.jpg",
                shop
        );
        productDTO.save();
    }

}
