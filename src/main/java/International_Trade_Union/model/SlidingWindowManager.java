package International_Trade_Union.model;
import International_Trade_Union.setings.Seting;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SlidingWindowManager {

    private static volatile SlidingWindowManager instance;
    private static final Object lock = new Object();
    private static ObjectMapper mapper = new ObjectMapper();
    private LinkedHashMap<Long, Map<String, Account>> windows;
    private String filePath;

    // Приватный конструктор для синглтона
    private SlidingWindowManager(String filePath) {
        this.filePath = filePath;
        this.windows = loadWindowsFromFile(filePath);
    }

    // Метод для получения единственного экземпляра класса
    public static SlidingWindowManager getInstance(String filePath) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SlidingWindowManager(filePath);
                }
            }
        }

        return instance;
    }

    public static SlidingWindowManager loadInstance(String filePath){
        return  instance = new SlidingWindowManager(filePath);
    }

    // Потокобезопасное добавление новой записи в скользящее окно
    public synchronized void addWindow(Long key, Map<String, Account> value) {
        windows.put(key, value);
        if (windows.size() > Seting.SLIDING_WINDOW_BALANCE) {
            windows.remove(windows.keySet().iterator().next());
        }
    }

    public synchronized void remove(Long key){
        windows.remove(key);
    }

    // Метод для получения записи по ключу
    public synchronized Map<String, Account> getWindow(Long key) {
        return windows.get(key);
    }

    // Метод для сохранения данных в файл
    public synchronized void saveWindowsToFile() {
        try {
            mapper.writeValue(new File(filePath), windows);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для загрузки данных из файла
    private LinkedHashMap<Long, Map<String, Account>> loadWindowsFromFile(String filePath) {
        try {
            LinkedHashMap<Long, Map<String, Account>> loadedWindows = mapper.readValue(
                    new File(filePath),
                    new TypeReference<LinkedHashMap<Long, Map<String, Account>>>() {}
            );
            return reapplySlidingWindowBehavior(loadedWindows, Seting.SLIDING_WINDOW_BALANCE);
        } catch (IOException e) {
            return reapplySlidingWindowBehavior(new LinkedHashMap<>(), Seting.SLIDING_WINDOW_BALANCE);
        }
    }

    // Метод для перезагрузки данных из файла в существующий объект
    public synchronized void reloadFromFile() {
        this.windows = loadWindowsFromFile(this.filePath);
    }

    // Метод для повторного применения поведения скользящего окна
    private LinkedHashMap<Long, Map<String, Account>> reapplySlidingWindowBehavior(
            LinkedHashMap<Long, Map<String, Account>> originalMap, int windowSize) {
        LinkedHashMap<Long, Map<String, Account>> slidingWindows = new LinkedHashMap<Long, Map<String, Account>>(windowSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Map<String, Account>> eldest) {
                return size() > windowSize;
            }
        };
        slidingWindows.putAll(originalMap);
        return slidingWindows;
    }

    // Метод для получения текущего состояния окна
    public synchronized LinkedHashMap<Long, Map<String, Account>> getWindows() {
        return windows;
    }
}
