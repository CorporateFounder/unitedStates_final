# Как сменить счет для майнинга

запустите локальный сервер и http://localhost:8082/
там внизу будет место куда должны вы вести ***change miner address)***
вводите туда ваш pub key, и нажмите кнопку change miner address

Код находится в контролере папки 
````
    src/main/java/International_Trade_Union/controllers/MainController.java
````
````
    @PostMapping("/setMinner")
    public ResponseEntity<String> setMinnerAddress(@RequestParam(value = "setMinner") String setMinner, RedirectAttributes redirectAttrs){
    System.out.println("MainController:  " + setMinner);
    UtilsFileSaveRead.save(setMinner, Seting.ORIGINAL_ACCOUNT, false);
    return new ResponseEntity<>("change address: " + setMinner, HttpStatus.OK);
    }
````

UtilsFileSaveRead.save() сохраняет новый публичный счет в файле
в папке
````
    resources/minerAccount/minerAccount.txt
````

[возврат на главную](../readme.md)