package International_Trade_Union.CorporateCharterDraft;
/**устарел(недействителен), является черновиком, но его хеш используется в генезис блоке.
 Но с помощью новых законов можно добавить те части которые будут интересны и сделать их действительным законом, если
 данные части не противоречат действующему уставу и действующим законам.
 */
public class CorporateCharter {
     public  static  String getAllConstitution(){
        String corporateCharter = PreambleDraft.ARTICLE_0 + "\n";
        //Статья I: Законодательная власть
        String articleOneLegistrature = ArticleOneLegistratureDraft.SECTION_1;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_2;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_3;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_4;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_5;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_6;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_7;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_8;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_9;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_10;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_11;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_12;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_13;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_14;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_15;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_16;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_17;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_18;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_19;
        articleOneLegistrature += "\n" + ArticleOneLegistratureDraft.CHAPTER_20;
        corporateCharter += "\n" + articleOneLegistrature;

        //Часть II: Исполнительная власть
        String spectialPosition = ArticleTwoSpecialPositionDraft.SECTION_1;
        spectialPosition += "\n" + ArticleTwoSpecialPositionDraft.CHAPTER_2;
        spectialPosition += "\n" + ArticleTwoSpecialPositionDraft.CHAPTER_3;
        spectialPosition += "\n" + ArticleTwoSpecialPositionDraft.CHAPTER_4;
        corporateCharter += "\n" + spectialPosition;

        //Статья III: Судебная власть
        String articleThreeJudiciary = ArticleThreeJudiciaryDraft.Article_3_Judiciary;
        articleThreeJudiciary += "\n" + ArticleThreeJudiciaryDraft.CHAPTER_2;
        corporateCharter += "\n" + articleThreeJudiciary;


        //Статья 4: Процесс внесения поправок
        String articleFiveAmendmentProcess = ArticleFourAmendmentProcessDraft.CHAPTER_1;
        corporateCharter += "\n" + articleFiveAmendmentProcess;


        //Статья 5: Конституционное верховенство
        String articleSixConstitutionalSupremacy = ArticleFiveCharterSupremacyDraft.CHAPTER_1;
        articleSixConstitutionalSupremacy += "\n" + ArticleFiveCharterSupremacyDraft.CONSTITUTIONAL_SUPERMACY;
        corporateCharter += "\n" + articleSixConstitutionalSupremacy;

        //Статья 6: Билль о правах
        String articleSevenBillOfRights = ArticleSixBillOfRightsDraft.SECTION_1;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_2;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_3;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_4;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_5;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_6;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_7;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_8;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_9;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_10;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_11;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_12;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_13;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_14;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_15;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_16;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_17;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_18;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_19;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_20;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_21;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_22;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_23;
        articleSevenBillOfRights += "\n" + ArticleSixBillOfRightsDraft.CHAPTER_24;
        corporateCharter += "\n" + articleSevenBillOfRights;


        return corporateCharter;
    }
}
