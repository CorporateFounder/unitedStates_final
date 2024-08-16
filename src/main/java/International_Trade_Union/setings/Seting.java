package International_Trade_Union.setings;

import International_Trade_Union.governments.Directors;
import International_Trade_Union.utils.MyHost;
import International_Trade_Union.utils.UtilsUse;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface Seting {
    boolean IS_TEST = false;
    boolean IS_SECURITY = true;

    int HUNDRED_PERCENT = 100;
    // значение используется как константа года,
    // в данной системе отсутствует високосный год
    int YEAR = 360;

    int FIFTEEN_DAYS =  15;

    Directors directors = new Directors();

    //За какой период последних блоков учитывать для отбора акционеров.
    //Акционерами могут быть только с наибольшим количеством баланса
    //отправители и майнеры.
    int BOARDS_BLOCK = (int) (Seting.COUNT_BLOCK_IN_DAY * YEAR);


    //минимальное значение количество положительных голосов, для того чтобы избрать
    // Совет Директоров и Совет Корпоративных Верховных Судей,
    int ORIGINAL_LIMIT_MIN_VOTE = 1; //(int) (200 * Seting.COUNT_BLOCK_IN_DAY * 1 / 8);


    //прямая демократия, сколько голосов нужно, чтобы правило вступило в силу,
    //без необходимости правительства
    double ALL_STOCK_VOTE = 1.0;


    //Минимальное значение чтобы Совет Корпоративных Верховных Судей могла избрать Верховного Судью
    int ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES =  0;


    //Минимальное количество остатка голосов чтобы Совет Акционеров
    //утверждал вместе с остальными участниками в утверждении законов.
    int ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS = 100;  //100;

    //голос Генерального Исполнительного Директора
    int ORIGINAL_LIMIT_MIN_VOTE_GENERAL_EXECUTIVE_DIRECTOR = 1;


    int ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT =  57;
    int ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_VOTE = 4;

    //голос Верховного Судьи
    int ORIGINAL_LIMIT_MIN_VOTE_HIGHT_JUDGE = 1;


    //голос должностных лиц,
    int VOTE_GOVERNMENT = 1;


    //    процент который получает основатель от добычи
    Double FOUNDERS_REWARD = 2.0;

    //address for send rewards
    String BASIS_ADDRESS = "faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ";
    String BASIS_PASSWORD = "3hupFSQNWwiJuQNc68HiWzPgyNpQA2yy9iiwhytMS7rZyfPddNRwtvExeevhayzN6xL2YmTXN6NCA8jBhV9ge1w8KciHedGUMgZyq2T7rDdvekVNwEgf5pQrELv8VAEvQ4Kb5uviXJFuMyuD1kRAGExrZym5nppyibEVnTC9Uiw8YzUh2JmVT9iUajnVV3wJ5foMs";

    //сложность коррекция каждые n блоков
    int DIFFICULTY_ADJUSTMENT_INTERVAL = (int) (Seting.COUNT_BLOCK_IN_DAY / 2);
    int DIFFICULTY_ADJUSTMENT_INTERVAL_TEST = 10;

    long BLOCK_GENERATION_INTERVAL = Seting.BLOCK_TIME * 1000;// after Seting.BLOCK_TIME
    long BLOCK_GENERATION_INTERVAL_TEST = 0;


    long INTERVAL_TARGET = 600000;
    long INTERVAL_TARGET_TEST = 25000;

    // плата за обслуживание каждые 6 месяцев.
    Double ANNUAL_MAINTENANCE_FREE_DIGITAL_DOLLAR_YEAR = 0.4;
    //отрицательная ставка для цифровой акции
    double ANNUAL_MAINTENANCE_FREE_DIGITAL_STOCK_YEAR = 0.8;
    //каждые сколько месяцев снимать
    int HALF_YEAR = 2;

    //стоимость создания закона 3
    double COST_LAW = 0;
    //с чего начинается адрес пакета закона
    //сокращенно корпорация
    String NAME_LAW_ADDRESS_START = "LIBER";

    int HASH_COMPLEXITY_GENESIS = 1;

    //совет акционеров
    int BOARD_OF_SHAREHOLDERS = 100000;


    //ПОПРАВКА В УСТАВЕ
    //требования к поправкам
    String AMENDMENT_TO_THE_CHARTER = "AMENDMENT_TO_THE_CHARTER";

    //директора созданные Советом директоров
    String ADD_DIRECTOR = "ADD_DIRECTOR_";


    //бюджет должен формировать только палата представителей
    String BUDGET = "BUDGET";
    String EMISSION = "EMISSION";

    double EMISSION_BUDGET = 25000;

    //план также утверждается на четыре года и утверждается только палатой представителей
    //каждый план обязан содержать дату начала планирования с какого числа вступает в силу.
    //FOUR-YEAR PLAN
    String STRATEGIC_PLAN = "STRATEGIC_PLAN";


    //лимиты для ведения поправок
    //палата судей минимум 5 голосов
    int ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT = 5;// 5;
    //палата представителей 20% голосов
    int ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT =
           75;



    //    адресс основателя: здесь будет мой адрес. Сейчас заглушка
    String ADDRESS_FOUNDER_TEST = "nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43";
    String ADDRESS_FOUNDER = "nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43";
    //начальная сумма основателя
    Double FOUNDERS_REMUNERATION_DIGITAL_DOLLAR = 65000000.0;
    double FOUNDERS_REMNUNERATION_DIGITAL_STOCK = 65000000.0;

    String CORPORATE_CHARTER_DRAFT = "";

    //КЛЮЧЕВОЕ НАЗВАНИЕ ПАКЕТА ЧТО ЭТО УСТАВ, ДЕЙСТВУЮЩИЙ УСТАВ ПОДПИСАН ОСНОВАТЕЛЕМ.
    String ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME = "ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME";

    //КЛЮЧЕВОЕ НАЗВАНИЕ ДЛЯ КОДА КОТОРЫЙ СОПРОВОЖДАЕТСЯ С УСТАВОМ
    String ORIGINAL_CHARTER_CURRENT_ALL_CODE = "ORIGINAL_CHARTER_CURRENT_ALL_CODE";


    //подсчет голосов для должности в годах, учитываются только те голоса
    //которые не позже четырех лет для законов и должностей,
    //голоса отданные за законы должны обновляться каждые четыре года
    //как и за должности
    int POSITION_YEAR_VOTE = (int) Seting.COUNT_BLOCK_IN_DAY * YEAR * 2;
    //подсчет голосов для законов в годах
    int LAW_YEAR_VOTE = (int) Seting.COUNT_BLOCK_IN_DAY * YEAR * 2;

    //используется для утверждения бюджета и эмиссии
    int LAW_MONTH_VOTE = (int) (FIFTEEN_DAYS * Seting.COUNT_BLOCK_IN_DAY);


    // сколько секунд в сутках
    int DAY_SECOND = 86400;

    //    за сколько секунд добывается каждый блок
    int BLOCK_TIME = 150;


    //сколько блоков добывается в сутки
    double COUNT_BLOCK_IN_DAY = (DAY_SECOND / BLOCK_TIME);


//    String testPath = IS_TEST ? "D:/" : "D:/";
    String testPath = IS_TEST ? "" : "";

    String ORIGINAL_BLOCKCHAIN_FILE = testPath +"/resources/blockchain/";
    String ORIGINAL_BALANCE_FILE = testPath +"/resources/balance/";
    String ORIGINAL_BOARD_0F_SHAREHOLDERS_FILE = testPath +"/resources/federalGovernment/federalGovernment.txt";
    String ORIGINAL_ALL_CORPORATION_LAWS_FILE = testPath +"/resources/federalLaws/";
    String ORIGINAL_ACCOUNT = testPath +"/resources/minerAccount/minerAccount.txt";
    String ORIGINAL_CORPORATE_VOTE_FILE = testPath +"/resources/vote/";

    String ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE = testPath +"/resources/allLawsWithBalance/";
    String ORIGINAL_ALL_CLASSIC_LAWS = testPath +"/resources/allClassicLaws/";
    String ORGINAL_ALL_TRANSACTION_FILE =testPath + "/resources/transactions/";

    String ORIGINAL_ALL_SENDED_TRANSACTION_FILE = testPath +"/resources/sendedTransaction/";
    String ORIGINAL_POOL_URL_ADDRESS_FILE =testPath + "/resources/poolAddress/";
    String ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE = testPath +"/resources/pooAddressBlocked/";

    String TEMPORARY_BLOCKCHAIN_FILE = testPath +"/resources/tempblockchain/shortBlockchain.txt";

    //отчет об уничтоженных монетах
    String BALANCE_REPORT_ON_DESTROYED_COINS = testPath +"/resources/balanceReportOnDestroyedCoins/";
    String CURRENT_BUDGET_END_EMISSION =testPath + "/resources/budgetEndEmission/";
    String H2_DB = testPath +"/resources/h2DB/";
    String ORIGINAL_TEMPORARY_BLOCKS = testPath +"/resources/temporaryBlocks/";

    String ORIGINAL_TEMP_SHORT =testPath + "/resources/temporaryBlocks/short/shortBlockchain.txt";
    String ORIGINAL_TEMP_BALANCE =testPath + "/resources/temporaryBlocks/balance/";
    String ORIGINAL_TEMP_BLOCKCHAIN = testPath +"/resources/temporaryBlocks/blockchain/";
    String ORIGINAL_TEMPORARY_SHORT =testPath + "/resources/short/shortBlockchain.txt";

    String ERROR_FILE = testPath +"/resources/error/error.txt";
    String YOUR_SERVER =testPath + "/resources/server/server.txt";
    String SLIDING_WINDOWS_BALANCE = "/resources/sWindow/sWindows.txt";



    //адресса внешних сервисов
//    Set<String> ORIGINAL_ADDRESSES = Set.of("http://194.87.236.238:80");
    Set<String> ORIGINAL_ADDRESSES = IS_TEST ? new HashSet<>(Arrays.asList("http://localhost:8083")) : new HashSet<>(Arrays.asList("http://194.87.236.238:82"));
    Set<String> ORIGINAL_BLOCKED_ADDRESS = Set.of("http://154.40.38.130:80",
            "http://10.0.36.2:80", "http://localhost:8083");

    int SIZE_FILE_LIMIT = 2;

    //папки файла для тестирования с сохранениям файла

    String TEST_LAST_BLOCK = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/lastBlock/";
    String INDEX_TEST = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/index/index.txt";
    String TEST_BLOCKCHAIN_SAVED = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/network/";
    String TEST_BLOCKCHAIN_BALANCES = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/balances/";
    String TEST_FEDERAL_GOVERNMENT = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/federal government/federalGovernment.txt";
    String TEST_FEDERAL_LAWS = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/laws/";
    String TEST_CURRENT_LAWS = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/current laws/";
    String TEST_FEDERAL_VOTE = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/vote/";
    String TEST_ALL_FEDERAL_LAWS_WITH_BALANCE_FILE = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/allLawsWithBalance/";


    //Временный блокчейн для тестирования
    String TEST_LAST_BLOCK_TEMPORARY = "/src/test/java/unitted_states_of_mankind/resourceTestingFileWithoutSave/lastBlock/";
    String TEST_INDEX_TEMPORARY = "/src/test/java/unitted_states_of_mankind/resourceTestingFileWithoutSave/index/index.txt";
    String TEST_TEMPORARY_BLOCKCHAIN = "/src/testjava/unitted_states_of_mankind/resourceTestingFileWithoutSave/temporary blockchain/";
    String TEST_BLOCKCHAIN_BALANCES_TEMPORARY = "/src/test/java/unitted_states_of_mankind/resourceTestingFileWithoutSave/balances/";
    String TEST_FEDERAL_GOVERNMENT_TEMPORARY = "/src/test/java/unitted_states_of_mankind/resourceTestingFileWithoutSave/federal government/federalGovernment.txt";
    String TEST_FEDERAL_LAWS_TEMPORARY = "/src/test/java/unitted_states_of_mankind/resourceTestingFileWithoutSave/laws/";
    String TEST_CURRENT_LAWS_TEMPORARY = "/src/test/java/unitted_states_of_mankind/resourceTestingFileWithoutSave/current laws/";
    String TEST_FEDERAL_VOTE_TEMPORARY = "/src/test/java/unitted_states_of_mankind/resourceTestingFileSaved/vote/";
    String TEST_ALL_FEDERAL_LAWS_WITH_BALANCE_TEMPORARY = "/unitedStates/src/test/java/unitted_states_of_mankind/resourceTestingFileWithoutSave/allLawsWithBalance/";

    String TEST_FILE_WRITE_INFO = "/src/test/java/unitted_states_of_mankind/blockchainTwentyYearTest/";
    double DIGITAL_DOLLAR_REWARDS_BEFORE = 400.0;
    double DIGITAL_STOCK_REWARDS_BEFORE = 400.0;

    //добыча должна происходить по формуле (сложность * 30) если индекс не четный +1, если четное  + 0
    double MONEY = 30;
    double DIGITAL_DOLLAR_FOUNDER_REWARDS_BEFORE = Math.round(UtilsUse.countPercents(Seting.DIGITAL_DOLLAR_REWARDS_BEFORE, Seting.FOUNDERS_REWARD));
    double DIGITAL_REPUTATION_FOUNDER_REWARDS_BEFORE = Math.round(UtilsUse.countPercents(Seting.DIGITAL_STOCK_REWARDS_BEFORE, Seting.FOUNDERS_REWARD));


    //каким количеством порций отправлять блоки

    int DELETED_PORTION = 150;
    int PORTION_DOWNLOAD = 500;
    //for find cheater 51648
    //after fork 24281
    int CHECK_UPDATING_VERSION = 24281;

    //for find cheater 53391
    //after fork 24281
    int NEW_START_DIFFICULT = 24281;
    int CHANGE_MEET_DIFFICULTY = 24858;

    int NEW_CHECK_UTILS_BLOCK = 0;
    int SPECIAL_BLOCK_FORK = 24281;

    int PORTION_BLOCK_TO_COMPLEXCITY = 600;

    //version
    int VERSION = 39;
    String FORK_ADDRESS_SPECIAL = "jPjuyLStHTCzwYt9J7R5M7pGUKshfcmEbtE3zVvCBE52";
    double SPECIAL_FORK_BALANCE = 12000000;

    int v3MeetsDifficulty = 25434;
    int v4MeetsDifficulty = 26496;

    String ORIGINAL_HASH = "08b1e6634457a40d3481e76ebd377e76322706e4ea27013b773686f7df8f8a4c";

    //для проверки транзакции в майнинга повторяющихся блоков.
    int CHECK_DTO = 300;



    int FIXED_BITE = 2;

    //количество транзакций после которых будет проверяться вознаграждение.
    int TRANSACTIONS_COUNT_MINIMUM = 1000;

    //изменение алгоритма добычи
    int V28_CHANGE_ALGORITH_DIFF_INDEX = 133750;
    int V29_CHANGE_ALGO_DIFF_INDEX = 138488;

    //мультипликатор
    int MULTIPLIER = 29;
    int STANDART_FOR_TARGET = 100;
    //деление для вычисление дохода основателя
    int DOLLAR = 10;
    int STOCK = 10;
    String MAX_TARGET = "00000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

    int V30_INDEX_ALGO = 142081;
    int V30_INDEX_DIFF = 142425;
    int V30_1_FIXED_DIFF = 143182;
    int V31_DIFF_END_MINING = 150974;
    int V31_FIX_DIFF = 151940;
    int V32_FIX_DIFF = 167343;
    int V34_NEW_ALGO = 187200;
    double V34_MINING_REWARD = 0.2;
    int V34_MIN_DIFF = IS_TEST == true? 1 : 17 ;
    String MAX_TARGET_v30 = "00000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";



    //фиксированная награда за блок 5 монет
    double V28_REWARD = 5;
    double COEFFICIENT = 3;
    double ONE_HUNDRED_THOUSAND = 100000;


    int WAIGHT_MINING = 9;
    int WAIGHT_MINING_2 = 55;
    int WAIGHT_MINING_3 = 25;
    int WAIGHT_MINING_INDEX =247867;

    int TIME_CHECK_BLOCK = 233682;
    //test
    MyHost myhost = new MyHost("localhost", "first", "key");

    //отбриет случайное количество хостов для подлючения
    int RANDOM_HOSTS = 10;

    int DELETED_FILE_BLOCKED_HOST = 5;

    //проверяет ценность блока для отката
    int IS_BIG_DIFFERENT = 20;

//    int DUPLICATE_INDEX = 0;
    int DUPLICATE_INDEX = 243927;

    int START_BLOCK_DECIMAL_PLACES = 268765;
    int NEW_ALGO_MINING = 286892;
    int DUBLICATE_IN_ONE_BLOCK_TRANSACTIONS = 287971;

    int DECIMAL_PLACES = 10;
    int SENDING_DECIMAL_PLACES = 8;

    double MINIMUM = 0.00000001;


    int FROM_STRING_DOUBLE = 294006;
    int SLIDING_WINDOW_BALANCE = 30;
}
