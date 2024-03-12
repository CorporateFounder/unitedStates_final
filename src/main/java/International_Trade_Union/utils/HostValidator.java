package International_Trade_Union.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Класс для проверки валидности хоста
public class HostValidator {

    // Регулярное выражение для проверки URL-адреса
    // Метод, который возвращает true, если строка является допустимым хостом
    public static boolean isValidHost(String host) {
        // Регулярное выражение, которое соответствует строке, начинающейся с http:// или https://
        // и заканчивающейся хостом с портом, доменом или доменом с портом
        String regex = "^(http|https)://([\\w\\d.-]+)(:\\d+)?$";
        // Создаем объект Pattern с регулярным выражением
        Pattern pattern = Pattern.compile(regex);
        // Создаем объект Matcher с строкой хоста
        Matcher matcher = pattern.matcher(host);
        // Возвращаем результат сопоставления
        return matcher.matches();
    }
    // Пример использования:
    public static void main(String[] args) {
        String testHost1 = "192.168.1.1";
        String testHost2 = "example.com";
        String testHost3 = "localhost";
        String testHost4 = "http://194.87.236.238:82";
        String testHost5 = "https://example.com:443";
        String testHost6 = "http://localhost:8080";
        String testHost7 = "https://example.com";

        System.out.println(testHost1 + " - " + isValidHost(testHost1));
        System.out.println(testHost2 + " - " + isValidHost(testHost2));
        System.out.println(testHost3 + " - " + isValidHost(testHost3));
        System.out.println(testHost4 + " - " + isValidHost(testHost4));
        System.out.println(testHost5 + " - " + isValidHost(testHost5));
        System.out.println(testHost6 + " - " + isValidHost(testHost6));
        System.out.println(testHost7 + " - " + isValidHost(testHost7));
    }
}
