# Apteka
## Как запустить
- Клонировать репозиторий
- Создать БД с именем Apteki в Postgres
- Отредактировать файл application.properties и вставить туда свои логин и пароль
```
spring.datasource.username = postgres
spring.datasource.password = postgres
```
- **Поменять путь до папки static в классе ImageEncoder**
- Запустить через IDEA (порт по умолчанию 4000)
## Автозаполнение
Выполнять ресты для автозаполнения нужно в *определенно том порядке*, в котором они здесь перечислены. Удобнее всего через постман      
**GET */api/medicaments/autocompleteCategories*** - автозаполнение категорий   
**GET */api/medicaments/autocompleteReleaseForms*** - автозаполнение форм выпуска   
**GET */api/medicaments/autocompleteManufacturers*** - автозаполнение производителей   
**GET */api/medicaments/autocompleteMedicaments*** - автозаполнение лекарств   
**GET */api/medicaments/autocompletePharmacies*** - автозаполнение аптек   
**GET */api/medicaments/autocompleteAvailabilities*** - автозаполнение наличия в аптеках (ЕЩЕ НЕ ЗАКОНЧЕНО, ЕСТЬ ПАРУ ШТУК ДЛЯ ПРИМЕРА - для Катионорма и Иммудона)   

В случае, если ресты для автозаполнения будут меняться, проще всего дропнуть бд и заполнить заново, чтобы все ок сохранилось

## Документация по Rest API
### Работа с пользователями
- **Регистрация**    
POST */api/user/registration*     
*Body:*
```
{
    "username": "vazyukova",
    "email": "gvazyukova@mail.ru",
    "password": "12345"
}
```
- **Авторизация**    
POST */api/user/auth*
```
{
    "username": "vazyukova",
    "email": "gvazyukova@mail.ru",
    "password": "12345"
}
```
### Работа с напоминаниями
- **Создать напоминание**    
POST */api/remainders/saveRemainder*
```
{
    "startDate":"2022-11-11", //пока что определенно такой формат ГГГГ-ММ-ДД
    "endDate": "2022-11-24",
    "count": "2.0",
    "medicamentId": 55,
    "username": "vazyukova"
}
```
- **Получить все напоминания пользователя** 
GET */api/remainders/byUser/vazyukova*

### Работа с лекарствами
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
В качестве ответа получаем дозировки и количество лекарства(в упаковке). Информацию о самом лекарстве можно получить по методу byId      
Пример ответа     
```
    {
    "medicamentCounts": [
        {
            "id": 56,
            "count": "24 шт"
        },
        {
            "id": 57,
            "count": "40 шт"
        }
    ],
    "medicamentDosages": [
        {
            "id": 58,
            "dosage": "2,7 мг"
        }
    ]
}
```
Пример того, как это должно выглядеть на фронте https://apteka.ru/product/kostaroks-60-mg-14-sht-tabletki-pokrytye-plenochnoj-obolochkoj-5f325452a477ec00015f2861/          
При клике на определенную пару количество - дозировка внизу должна выводиться табличка с наличием в аптеках (запрос для этого ниже)     

- **Получить информацию о наличии лекарства в аптеках по Id медикамента**    
GET */api/medicaments/medicamentAvailabilities/?medicamentCountId=777&medicamentDosageId=888*     

Пока все :)      
## Полный список лекарств, которые есть в бд
катионорм https://apteka.ru/product/kationorm-10-ml-emulsiya-gl-kapli-5e32663cca7bdc000192ba56/     
    имудон https://apteka.ru/product/imudon-24-sht-tabletki-dlya-rassasyvaniya-5e3270e3f5a9ae000140c6ea/      
    акридерм гк https://apteka.ru/product/akriderm-gk-maz-15-gr-5e32761e65b5ab0001657b51/     
    комбилипен https://apteka.ru/product/kombilipen-rastvor-dlya-vnutrimyshechnogo-vvedeniya-2-ml-ampuly-5-sht-5e326bbbf5a9ae0001409f9c/      
    костарокс https://apteka.ru/product/kostaroks-60-mg-14-sht-tabletki-pokrytye-plenochnoj-obolochkoj-5f325452a477ec00015f2861/     
    глюкофаж https://apteka.ru/product/glyukofazh-long-750-mg-30-sht-tabletki-s-prolongirovannym-vysvobozhdeniem-62b2e457a8387752a786a74f/     
    дексометазон https://apteka.ru/product/deksametazon-4-mgml-rastvor-dlya-inekczij-1-ml-ampuly-10-sht-5e326c3ef5a9ae000140a419/     
    генеролон https://apteka.ru/product/generolon-5-sprej-60-ml-5e3279aef5a9ae0001410c49/    
    прадакса https://apteka.ru/product/pradaksa-110-mg-30-sht-kapsuly-5e32731c65b5ab00016564c7/      
    левомицетин https://apteka.ru/product/levomiczetin-reneval-1-25-ml-tyubikkapelnicza-rastvor-dlya-naruzhnogo-primeneniya-spirtovoj-62874a178b308b33b75ba41c/     
    аспаркам https://apteka.ru/product/asparkam-56-sht-tabletki-5e327871ca7bdc0001934b3a/     
    долгит https://apteka.ru/product/dolgit-5-krem-50-gr-5e3264ef65b5ab000164f807/     
    тамсулозин https://apteka.ru/product/tamsulozin-reneval-04mg-n30-kaps-kishechnorastvor-prolong-vysvob-62fa07ac3d4271b40e195992/?q=%D1%82%D0%B0%D0%BC%D1%81%D1%83%D0%BB%D0%BE%D0%B7%D0%B8%D0%BD+%D1%80%D0%B5%D0%BD%D0%B5%D0%B2%D0%B0%D0%BB      
    нейронтин https://apteka.ru/product/nejrontin-600-mg-50-sht-tabletki-pokrytye-plenochnoj-obolochkoj-5e32752665b5ab00016572ea/      
    смекта https://apteka.ru/product/smekta-3-gr-376-20-sht-paket-poroshok-dlya-prigotovleniya-suspenzii-dlya-priema-vnutr-vkus-apelsinovyj-61bb0aada564f07fddccd5d1/     
    арбидол https://apteka.ru/product/arbidol-25-mg5-ml-poroshok-dlya-prigotovleniya-suspenzii-flakon-37-gr-5e326dab65b5ab0001653c2c/     
    гиотриф https://apteka.ru/product/giotrif-20-mg-30-sht-tabletki-pokrytye-plenochnoj-obolochkoj-630f805d260509b64b8d8f0f/      
    чемеричная вода https://apteka.ru/product/chemerichnaya-voda-rastvor-dlya-naruzhnogo-primeneniya-100-ml-flakon-5e326cf365b5ab0001653662/     
    нош-па https://apteka.ru/product/no-shpa-40-mg-24-sht-blister-tabletki-pokrytye-plenochnoj-obolochkoj-6294818724641c0e2859b26a/      
    витамин С https://apteka.ru/product/vitamin-s-1200-mg-10-sht-shipuchie-tabletki-massoj-38-g-5e327138f5a9ae000140c9df/      
    ринсулин https://apteka.ru/product/rinsulin-npx-100-meml-5-sht-kartridzh-suspenziya-dlya-podkozhnogo-vvedeniya-ispolnenie-kartridzh-3-ml-5e32641565b5ab000164f36f/     
    буденофальк https://apteka.ru/product/budenofalk-3-mg-20-sht-kapsuly-5e32775eca7bdc000193413c/      
    Надропарин кальция https://apteka.ru/product/nadroparin-kalcziya-9500-me-anti-xaml-5-sht-shpricz-rastvor-dlya-inekczij-03-ml-609bccfcdf443e8756caa958/       
    лозартан https://apteka.ru/product/lozartan-125-mg-30-sht-tabletki-pokrytye-plenochnoj-obolochkoj-5e327514ca7bdc0001932d24/      
    дюфастон https://apteka.ru/product/dyufaston-10-mg-20-sht-tabletki-pokrytye-plenochnoj-obolochkoj-5e326e5465b5ab00016540f3/      
    итулси https://apteka.ru/product/itulsi-100-mg-21-sht-blister-kapsuly-62c6bf2fb631a206e0a2653a/
    немозол https://apteka.ru/product/nemozol-015-ml-flakon-suspenziya-20-ml-5e3266dff5a9ae0001407793/


