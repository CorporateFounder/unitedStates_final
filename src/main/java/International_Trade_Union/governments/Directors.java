package International_Trade_Union.governments;

import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsUse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Data

public class Directors {
    public Directors() {

        directors = new ArrayList<>();
        Director CORPORATE_COUNCIL_OF_REFEREES = new Director("CORPORATE_COUNCIL_OF_REFEREES", 55, false, false, false, true, false);
        Director BOARD_OF_DIRECTORS = new Director("BOARD_OF_DIRECTORS", 301, false, false, false, true, false);

        Director GENERAL_EXECUTIVE_DIRECTOR = new Director("GENERAL_EXECUTIVE_DIRECTOR", 1, false, true, false, false, true);
        Director HIGH_JUDGE = new Director("HIGH_JUDGE",1, false, false, true, false, false);
        Director FRACTION = new Director("FRACTION", 100, false, false, false, true, false);
        directors.add(CORPORATE_COUNCIL_OF_REFEREES);
        directors.add(BOARD_OF_DIRECTORS);
        directors.add(FRACTION);

        directors.add(GENERAL_EXECUTIVE_DIRECTOR);
        directors.add(HIGH_JUDGE);

    }


    public Director getDirector(String str){
       List<Director> director = directors
               .stream()
               .filter(t->t.getName().equals(str))
               .collect(Collectors.toList());
       return director.get(0);

    }
    private List<Director> directors;

    public boolean contains(String str) {
        str = str.toUpperCase();
        directors = directors.stream().filter(UtilsUse.distinctByKey(Director::getName)).collect(Collectors.toList());
        List<String> strings = directors.stream().map(t->t.getName()).collect(Collectors.toList());
       return strings.contains(str);
    }

    public boolean isCabinets(String str) {
        List<String> list = directors.stream()
                .filter(t->t.isOfficeOfDirectors())
                .filter(UtilsUse.distinctByKey(Director::getName))
                .map(t->t.getName())
                .collect(Collectors.toList());
        return list.contains(str.toUpperCase(Locale.ROOT));
    }


    //должности которые добавляются только советом директоров
    public void addByBoardOfDirectors(String str) {
        if (str.startsWith(Seting.ADD_DIRECTOR)) {
            Director director = new Director(str, 1, false, true, false, false, true);
            directors.add(director);
            directors = directors.stream().filter(UtilsUse.distinctByKey(Director::getName)).collect(Collectors.toList());
        }

    }

    public void addAllByBoardOfDirectors(List<String> strings) {
        strings = strings.stream()
                .filter(t -> t.startsWith(Seting.ADD_DIRECTOR))
                .distinct()
                .collect(Collectors.toList());
        for (String s : strings) {
            Director director = new Director(s, 1, false, true, false, false, true);
            directors.add(director);
        }
        directors = directors.stream().filter(UtilsUse.distinctByKey(Director::getName)).collect(Collectors.toList());

    }


    public List<String> getNames() {
        return directors.stream().map(t -> t.getName()).collect(Collectors.toList());
    }


    public  boolean isElectedByStocks(String str){
        List<String> list = directors.stream()
                .filter(t->t.isElectedByStocks())
                .map(t->t.getName())
                .collect(Collectors.toList());

        return list.contains(str.toUpperCase(Locale.ROOT));
    }
    public boolean isElectedCEO(String str){
        List<String> list = directors.stream()
                .filter(t->t.isElectedByCEO())
                .map(t->t.getName())
                .collect(Collectors.toList());

        return list.contains(str.toUpperCase(Locale.ROOT));
    }

    public  boolean isElectedByBoardOfDirectors(String  str){
        List<String> list = directors.stream()
                .filter(t->t.isElectedByBoardOfDirectors())
                .map(t->t.getName())
                .collect(Collectors.toList());

        return list.contains(str.toUpperCase(Locale.ROOT));
    }

    public boolean isElectedBYCorporateCouncilOfReferees(String  str){
        List<String> list = directors.stream()
                .filter(t->t.isElectedByCorporateCouncilOfReferees())
                .map(t->t.getName())
                .collect(Collectors.toList());

        return list.contains(str.toUpperCase(Locale.ROOT));
    }

    public boolean isofficeOfDirectors(String str){
        List<String> list = directors.stream()
                .filter(t->t.isOfficeOfDirectors())
                .map(t->t.getName())
                .collect(Collectors.toList());

        return list.contains(str.toUpperCase(Locale.ROOT));
    }
}
