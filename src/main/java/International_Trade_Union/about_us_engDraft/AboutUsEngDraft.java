package International_Trade_Union.about_us_engDraft;
/**устарел(недействителен), является черновиком, но его хеш используется в генезис блоке.
 Но с помощью новых законов можно добавить те части которые будут интересны и сделать их действительным законом, если
 данные части не противоречат действующему уставу и действующим законам.
 */
public interface AboutUsEngDraft {
    String aboutUs = "The purpose of the International Trade Union Corporation is to facilitate trade between people by providing " +
            "their ecosystem, as well as the provision of their products, which also includes money, legislation, etc. " +
            "In case of inconsistencies due to the translation of the English charter and Russian, proceed from the Russian charter, since " +
            "the translation was through Google translator from Russian into English.";

    String aboutUsCity = "immediate goals: 1. Popularize this system. 2. Create appropriate laws within this system" +
            "the current needs of this system. 3. Create an appropriate infrastructure for the development of our Corporation." +
            "4. Start open negotiations on partnerships with large TNCs. 5. Determine the first city where the headquarters will be" +
            "this corporation. ";

    String finish =

            "International Trade Union Corporation together with all of us to pave for the development of international trade," +
                    " developing advanced technologies and providing the best products and services. " +
                    " Creation of an advanced infrastructure that will create conditions for free international trade.";

    public static String getAboutUs(){
        return "\naboutUs: "+aboutUs + "\naboutUsCity:" + aboutUsCity + "\nfinish: " + finish ;
    }
}
