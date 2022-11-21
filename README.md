# Apteka
## Как запустить
- Клонировать репозиторий
- Создать БД с именем Apteki в Postgres
- Отредактировать файл application.properties и вставить туда свои логин и пароль
```
spring.datasource.username = postgres
spring.datasource.password = postgres
```
- Запустить через IDEA (порт по умолчанию 6666)
## Автозаполнение
Выполнять ресты для автозаполнения нужно в *определенно том порядке*, в котором они здесь перечислены. Удобнее всего через постман      
**GET */api/medicaments/autocompleteCategories*** - автозаполнение категорий   
**GET */api/medicaments/autocompleteReleaseForms*** - автозаполнение форм выпуска   
**GET */api/medicaments/autocompleteManufacturers*** - автозаполнение производителей   
**GET */api/medicaments/autocompleteMedicaments*** - автозаполнение лекарств   
**GET */api/medicaments/autocompletePharmacies*** - автозаполнение аптек   
**GET */api/medicaments/autocompleteAvailabilities*** - автозаполнение наличия в аптеках (ЕЩЕ НЕ ЗАКОНЧЕНО, ЕСТЬ ПАРУ ШТУК ДЛЯ ПРИМЕРА)   

В случае, если ресты для автозаполнения будут меняться, проще всего дропнуть бд и заполнить заново, чтобы все ок сохранилось

## Документация по Rest API
- **Получить список всех категорий**    
GET */api/medicaments/categories*

- **Получить список всех лекарств**    
GET */api/medicaments/all*

- **Получить список всех производителей**    
GET */api/medicaments/manufacturers*

- **Получить список всех производителей**    
GET */api/medicaments/manufacturers*

- **Получить список всех стран производителей**    
GET */api/medicaments/сountries*

- **Получить список всех форм выпуска**    
GET */api/medicaments/releaseforms*

- **Получить лекарство по Id**    
GET */api/medicaments/byId/{id}*

- **Поиск**    
POST */api/medicaments/search*     
*Body:*
```
    "name": "Название лекарства",
    "category":"Категория",
    "activeSubstance":"Действующее вещество",
    "manufacturerName":"Название производителя",
    "country":"Страна производителя",
    "releaseForm":"Форма выпуска",
    "dosage":"Дозировка"
```
Все параметры необязательные - то есть можно указать только имя или имя и категорию. Указываем не id, а имя параметра поиска    
Ответ возвращается в подобном формате (для каждого лекарства)    
*Response:*
```
    "medicament": {
        //вся информация о лекарстве (пока без картинок)
    },
    "minPrice":"234.0, //минимальная цена среди всех наличий в аптеках
    "pharmacyCount": 3 //количество аптек, в которых есть это лекарство
```
- **Получить подробную информацию о лекарстве по Id медикамента**    
GET */api/medicaments/medicamentInfo/{id}*     
В качестве ответа получаем дозировки и количество лекарства(в упаковке). Информация о самом лекарстве содержится в объектах MedicamentCount и MedicamentDosage (возможно потом переделаю, но пусть пока так)    
Пример того, как это должно выглядеть на фронте https://apteka.ru/product/kostaroks-60-mg-14-sht-tabletki-pokrytye-plenochnoj-obolochkoj-5f325452a477ec00015f2861/          
При клике на определенную пару количество - дозировка внизу должна выводиться табличка с наличием в аптеках (запрос для этого ниже)     

- **Получить подробную информацию о лекарстве по Id медикамента**    
GET */api/medicaments/medicamentAvailabilities/?medicamentCountId=777&medicamentDosageId=888*     

Пока все :)

