package International_Trade_Union.CorporateCharterEngDraft;
/**устарел(недействителен), является черновиком, но его хеш используется в генезис блоке.
 Но с помощью новых законов можно добавить те части которые будут интересны и сделать их действительным законом, если
 данные части не противоречат действующему уставу и действующим законам.
 */
public class CorporateCharter {
    public  static  String getAllConstitution(){
        String сorporateCharter = PreambleEngDraft.ARTICLE_0 + "\n";
        //Статья I: Законодательная власть
        String articleOneLegistrature = ArticleOneLegistratureEngDraft.SECTION_1;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_2;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_3;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_4;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_5;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_6;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_7;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_8;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_9;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_10;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_11;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_12;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_13;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_14;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_15;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_16;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_17;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_18;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_19;
        articleOneLegistrature += "\n" + ArticleOneLegistratureEngDraft.CHAPTER_20;
        сorporateCharter += "\n" + articleOneLegistrature;

        //Часть II: Исполнительная власть
        String articleTwoExecutiveBranch = ArticleTwoSpecialPositionEngDraft.SECTION_1;
        articleTwoExecutiveBranch += "\n" + ArticleTwoSpecialPositionEngDraft.CHAPTER_2;
        articleTwoExecutiveBranch += "\n" + ArticleTwoSpecialPositionEngDraft.CHAPTER_3;
        articleTwoExecutiveBranch += "\n" + ArticleTwoSpecialPositionEngDraft.CHAPTER_4;
        сorporateCharter += "\n" + articleTwoExecutiveBranch;

        //Статья III: Судебная власть
        String articleThreeJudiciary = ArticleThreeJudiciaryEngDraft.Article_3_Judiciary;
        articleThreeJudiciary += "\n" + ArticleThreeJudiciaryEngDraft.CHAPTER_2;

        сorporateCharter += "\n" + articleThreeJudiciary;


        //Статья V: Процесс внесения поправок
        String articleFiveAmendmentProcess = ArticleFourAmendmentProcessEngDraft.CHAPTER_1;
        сorporateCharter += "\n" + articleFiveAmendmentProcess;


        //Статья VI: Конституционное верховенство
        String articleSixConstitutionalSupremacy = ArticleFiveCharterSupremacyEngDraft.CHAPTER_1;
        articleSixConstitutionalSupremacy += "\n" + ArticleFiveCharterSupremacyEngDraft.CONSTITUTIONAL_SUPERMACY;
        сorporateCharter += "\n" + articleSixConstitutionalSupremacy;

        //Статья VII: Билль о правах
        String articleSevenBillOfRights = ArticleSixBillOfRightsEngDraft.SECTION_1;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_2;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_3;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_4;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_5;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_6;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_7;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_8;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_9;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_10;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_11;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_12;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_13;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_14;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_15;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_16;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_17;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_18;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_19;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_20;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_21;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_22;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_23;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsEngDraft.CHAPTER_24;

        сorporateCharter += "\n" + articleSevenBillOfRights;

        return сorporateCharter;
    }
}
