package com.example.apteki.controller;

import com.example.apteki.ImageEncoder;
import com.example.apteki.model.*;
import com.example.apteki.payload.MedicamentInfo;
import com.example.apteki.payload.SearchRequest;
import com.example.apteki.payload.SearchResponse;
import com.example.apteki.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicaments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MedicamentController {
    CategoryRepository categoryRepository;
    ManufacturerRepository manufacturerRepository;
    MedicamentRepository medicamentRepository;
    ReleaseFormRepository releaseFormRepository;
    MedicamentAvailabilityRepository medicamentAvailabilityRepository;
    MedicamentCountRepository medicamentCountRepository;
    MedicamentDosageRepository medicamentDosageRepository;
    PharmacyRepository pharmacyRepository;
    PharmacyAddressRepository pharmacyAddressRepository;
    MedicamentImageRepository medicamentImageRepository;

    public MedicamentController(CategoryRepository categoryRepository,
            ManufacturerRepository manufacturerRepository,
            MedicamentRepository medicamentRepository,
            ReleaseFormRepository releaseFormRepository,
            MedicamentAvailabilityRepository medicamentAvailabilityRepository,
            MedicamentCountRepository medicamentCountRepository,
            MedicamentDosageRepository medicamentDosageRepository,
            PharmacyRepository pharmacyRepository,
            PharmacyAddressRepository pharmacyAddressRepository,
            MedicamentImageRepository medicamentImageRepository)
    {
        this.categoryRepository = categoryRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.medicamentRepository = medicamentRepository;
        this.releaseFormRepository = releaseFormRepository;
        this.medicamentAvailabilityRepository = medicamentAvailabilityRepository;
        this.medicamentCountRepository = medicamentCountRepository;
        this.medicamentDosageRepository = medicamentDosageRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyAddressRepository = pharmacyAddressRepository;
        this.medicamentImageRepository = medicamentImageRepository;
    }

    @GetMapping(value="/all")
    public ResponseEntity<List<Medicament>> getAllMedicaments(){
        List<Medicament> medicaments = medicamentRepository.findAll();
        if (medicaments == null) {
            return ResponseEntity.notFound().build();
        } else {
            return new ResponseEntity<>(medicaments, HttpStatus.OK);
        }
    }

    @GetMapping(value="/categories")
    public ResponseEntity<List<Category>> getAllCategory(){
        List<Category> categories = categoryRepository.findAll();
        if (categories == null) {
            return ResponseEntity.notFound().build();
        } else {
            return new ResponseEntity<>(categories, HttpStatus.OK);
        }
    }

    @GetMapping(value="/manufacturers")
    public ResponseEntity<List<Manufacturer>> getAllManufacturers(){
        List<Manufacturer> manufacturers = manufacturerRepository.findAll();
        if (manufacturers == null) {
            return ResponseEntity.notFound().build();
        } else {
            return new ResponseEntity<>(manufacturers, HttpStatus.OK);
        }
    }

    @GetMapping(value="/countries")
    public ResponseEntity<List<String>> getAllCountries(){
        List<String> countries = manufacturerRepository.findAll().stream()
                .map(Manufacturer::getCountry)
                .collect(Collectors.toList());
        if (countries == null) {
            return ResponseEntity.notFound().build();
        } else {
            return new ResponseEntity<>(countries, HttpStatus.OK);
        }
    }

    @GetMapping(value="/releaseforms")
    public ResponseEntity<List<ReleaseForm>> getAllReleaseForms()
    {
        List<ReleaseForm> releaseForms = releaseFormRepository.findAll();
        if (releaseForms == null)
        {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(releaseForms, HttpStatus.OK);
    }

    @GetMapping(value="byId/{id}")
    public ResponseEntity<Medicament> getMedicamentById(@PathVariable(name="id") int id){
        Medicament medicament = medicamentRepository.findById(id).get();
        if (medicament == null)
        {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(medicament, HttpStatus.OK);
    }

    @PostMapping(value = "/search")
    public ResponseEntity<List<SearchResponse>> search(@RequestBody SearchRequest searchRequest)
    {
        List<Medicament> sortedMedicaments = medicamentRepository.findAll();
        if (searchRequest.getName() != null) {
            sortedMedicaments = sortedMedicaments.stream()
                    .filter(medicament -> medicament.getName().toLowerCase().contains(searchRequest.getName().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (searchRequest.getCategory() != null)
        {
            sortedMedicaments = sortedMedicaments.stream()
                    .filter(medicament -> medicament.getCategory().getName().toLowerCase().contains(searchRequest.getCategory().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (searchRequest.getActiveSubstance() != null)
        {
            sortedMedicaments = sortedMedicaments.stream()
                    .filter(medicament -> medicament.getActiveSubstance().toLowerCase().contains(searchRequest.getActiveSubstance().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (searchRequest.getManufacturerName() != null)
        {
            sortedMedicaments = sortedMedicaments.stream()
                    .filter(medicament -> medicament.getManufacturer().getName().toLowerCase().contains(searchRequest.getManufacturerName().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (searchRequest.getCountry() != null)
        {
            sortedMedicaments = sortedMedicaments.stream()
                    .filter(medicament -> medicament.getManufacturer().getCountry().toLowerCase().contains(searchRequest.getCountry().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (searchRequest.getReleaseForm() != null)
        {
            sortedMedicaments = sortedMedicaments.stream()
                    .filter(medicament -> medicament.getReleaseForm().getName().toLowerCase().contains(searchRequest.getReleaseForm().toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<SearchResponse> searchResults = sortedMedicaments.stream()
                .map(this::convertMedicamentToSearchResponse)
                .collect(Collectors.toList());

        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }

    @GetMapping(value = "/medicamentInfo/{id}")
    public ResponseEntity<MedicamentInfo> getMedicamentInfo(@PathVariable(name="id") int id)
    {
        Medicament medicament = medicamentRepository.getReferenceById(id);
        List<MedicamentCount> medicamentCounts = medicamentCountRepository.findByMedicament(medicament);
        List<MedicamentDosage> medicamentDosages = medicamentDosageRepository.findByMedicament(medicament);

        MedicamentInfo medicamentInfo = new MedicamentInfo(medicamentCounts, medicamentDosages);

        System.out.println(ImageEncoder.encodeFileToBase64Binary("imudon.png"));
        return new ResponseEntity<>(medicamentInfo, HttpStatus.OK);
    }

    @GetMapping(value = "/medicamentAvailabilities")
    public ResponseEntity<List<MedicamentAvailability>> getMedicamentAvailabilities(@RequestParam(name = "medicamentCountId") int medicamentCountId,
                                                                                    @RequestParam(name = "medicamentDosageId") int medicamentDosageId)
    {
        List<MedicamentAvailability> medicamentAvailabilities = medicamentAvailabilityRepository.findByMedicamentCountAndMedicamentDosage(
                medicamentCountRepository.getReferenceById(medicamentCountId),
                medicamentDosageRepository.getReferenceById(medicamentDosageId)
        );

        return new ResponseEntity<>(medicamentAvailabilities, HttpStatus.OK);
    }

    @GetMapping(value = "/autocompleteCategories")
    public ResponseEntity<List<Category>> autocompleteCategories()
    {
        categoryRepository.save(new Category("Препараты для лечения органов чувств")); //иммудон, катионорм, левомицетин
        categoryRepository.save(new Category("Дерматологические средства")); //акридерм, генеролон
        categoryRepository.save(new Category("Витамины"));//комбилипен, витамин С
        categoryRepository.save(new Category("Препараты для лечения костно-мыщечной системы"));//костарокс, долгит
        categoryRepository.save(new Category("Диабет"));//глюкофаж, ринсулин
        categoryRepository.save(new Category("Гормоны для системного применения"));//дексаметазон, буденофальк
        categoryRepository.save(new Category("Препараты для кроветворения и крови"));//прадакса, Надропарин кальция
        categoryRepository.save(new Category("Препараты для сердечно-сосудистой системы"));//аспаркам, лозартам
        categoryRepository.save(new Category("Препараты для мочеполовой системы и половые гормоны"));//тамсулозин, дюфастон
        categoryRepository.save(new Category("Препараты для пищеварительного тракта и обмена веществ"));//смекта, нош-па
        categoryRepository.save(new Category("Противоопухолевые препараты и иммуномодуляторы"));//гиотриф, итулси
        categoryRepository.save(new Category("Противопаразитарные препараты"));//чемеричная вода, немозол
        categoryRepository.save(new Category("Профилактика ОРВИ и гриппа"));// солодки сироп, арбидол
        categoryRepository.save(new Category("Препараты для нервной системы"));// нейротин

        return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/autocompleteReleaseForms")
    public ResponseEntity<List<ReleaseForm>> autocompleteReleaseForms()
    {
        List<ReleaseForm> releaseForms = new ArrayList<>();
        releaseForms.add(new ReleaseForm("Капсулы"));//прадакса, тамсулозин, нейротин, буденофальк, итулси
        releaseForms.add(new ReleaseForm("Таблетки")); //костарокс, глюкофаж, аспаркам, гиотриф, нош-па, лозартан, дюфастон
        releaseForms.add(new ReleaseForm("Таблетки для рассасывания")); //Имудон
        releaseForms.add(new ReleaseForm("Таблетки шипучие")); //Витамин С
        releaseForms.add(new ReleaseForm("Сироп"));//сироп солодки
        releaseForms.add(new ReleaseForm("Спрей")); //генеролон
        releaseForms.add(new ReleaseForm("Мазь")); //акридерм
        releaseForms.add(new ReleaseForm("Крем")); //долгит
        releaseForms.add(new ReleaseForm("Порошок")); //смекта, арбидол
        releaseForms.add(new ReleaseForm("Капли")); // катионорм
        releaseForms.add(new ReleaseForm("Раствор для в/м введения")); // комбилипен, дексаметазон, Надропарин кальция
        releaseForms.add(new ReleaseForm("Суспензия для п/к введения")); // ринсулин
        releaseForms.add(new ReleaseForm("Суспензия для приема внутрь")); // немозол
        releaseForms.add(new ReleaseForm("Раствор для нар. применения")); // катионорм, левометицин, чемеричная вода

        for (ReleaseForm releaseForm : releaseForms)
        {
            releaseFormRepository.save(releaseForm);
        }

        return new ResponseEntity<>(releaseFormRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/autocompleteManufacturers")
    public ResponseEntity<List<Manufacturer>> autocompleteManufacturers()
    {
        List<Manufacturer> manufacturers = new ArrayList<>();
        manufacturers.add(new Manufacturer("Фармстандарт-Томскхимфарм ОАО", "Россия")); //имудон
        manufacturers.add(new Manufacturer("Акрихин ХФК АО", "Россия")); //акридерм
        manufacturers.add(new Manufacturer("Фармстандарт-Уфимский витаминный завод,ОАО", "Россия")); //комбилипен
        manufacturers.add(new Manufacturer("ЛАБОРАТУАР ФАРМАСТЕР", "Франция")); //катионорм
        manufacturers.add(new Manufacturer("Салютас Фарма ГмбХ", "Германия")); //костарокс
        manufacturers.add(new Manufacturer("Мерк Хелскеа КГаА", "Германия")); //глюкофаж
        manufacturers.add(new Manufacturer("Эллара ООО", "Россия")); //дексаметазон
        manufacturers.add(new Manufacturer("БЕЛУПО", "Республика Хорватия")); //генеролон
        manufacturers.add(new Manufacturer("Берингер Ингельхайм Фарма ГмбХ и Ко.КГ", "Германия")); //прадакса, гиотриф
        manufacturers.add(new Manufacturer("Обновление ПФК  АО", "Россия")); //левомицетин, аспаркам, тамсулозин
        manufacturers.add(new Manufacturer("Долоргит ГмбХ и Ко.КГ", "Германия")); //долгит
        manufacturers.add(new Manufacturer("Пфайзер Фармасьютикалз ЭлЭлСи", "Пуэрто-Рико")); //долгит, пфайзер, нейротин
        manufacturers.add(new Manufacturer("Бофур Ипсен Индастри", "Франция")); //смекта
        manufacturers.add(new Manufacturer("Фармстандарт-Лексредства ОАО", "Россия")); //арбидол
        manufacturers.add(new Manufacturer("ТВЕРСКАЯ ФАРМАЦЕВТИЧЕСКАЯ ФАБРИКА ОАО", "Россия")); //чемеричная вода
        manufacturers.add(new Manufacturer("Кировская фармацевтическая фабрика АО", "Россия")); //сироп солодки
        manufacturers.add(new Manufacturer("Опелла Хелскеа Венгрия Лтд.", "Венгрия")); //нош-па
        manufacturers.add(new Manufacturer("Эвалар", "Россия")); //витамин С
        manufacturers.add(new Manufacturer("ГЕРОФАРМ", "Россия")); //ринсулин
        manufacturers.add(new Manufacturer("Лозан Фарма", "Германия")); //буденофальк
        manufacturers.add(new Manufacturer("Московский эндокринный завод", "Россия")); //Надропарин кальция
        manufacturers.add(new Manufacturer("Пранафарм ООО", "Россия")); //Надропарин кальция, лозартам
        manufacturers.add(new Manufacturer("Верофарм АО", "Россия")); //дюфастон
        manufacturers.add(new Manufacturer("Ипка Лабораториз Лимитед", "Индия")); //немозол

        for (Manufacturer manufacturer : manufacturers)
        {
            manufacturerRepository.save(manufacturer);
        }
        return new ResponseEntity<>(manufacturerRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/autocompleteMedicaments")
    public ResponseEntity<List<Medicament>> autocompleteMedicaments()
    {
        //катионорм
        medicamentRepository.save(new Medicament("Катионорм", "Тилоксапол",
                categoryRepository.findByName("Препараты для лечения органов чувств"),
                manufacturerRepository.findByName("ЛАБОРАТУАР ФАРМАСТЕР"),
                releaseFormRepository.findByName("Капли")
        ));
        medicamentCountRepository.save(new MedicamentCount("10 мл", medicamentRepository.findByName("Катионорм").get(0)));

        //имудон
        Medicament imudon = medicamentRepository.save(new Medicament("Имудон", "Смесь лизатов бактерий",
                categoryRepository.findByName("Препараты для лечения органов чувств"),
                manufacturerRepository.findByName("Фармстандарт-Томскхимфарм ОАО"),
                releaseFormRepository.findByName("Таблетки для рассасывания")
        ));
        medicamentCountRepository.save(new MedicamentCount("24 шт", medicamentRepository.findByName("Имудон").get(0)));
        medicamentCountRepository.save(new MedicamentCount("40 шт", medicamentRepository.findByName("Имудон").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("2,7 мг", medicamentRepository.findByName("Имудон").get(0)));

        medicamentImageRepository.save(new MedicamentImage(1, "imudon.png", imudon));

        //акридерм гк
        medicamentRepository.save(new Medicament("Акридерм ГК", "Бетаметазон",
                categoryRepository.findByName("Дерматологические средства"),
                manufacturerRepository.findByName("Акрихин ХФК АО"),
                releaseFormRepository.findByName("Мазь")
        ));
        medicamentCountRepository.save(new MedicamentCount("15 гр", medicamentRepository.findByName("Акридерм ГК").get(0)));
        medicamentCountRepository.save(new MedicamentCount("30 гр", medicamentRepository.findByName("Акридерм ГК").get(0)));

        //комбилипен
        medicamentRepository.save(new Medicament("Комбилипен", "Цианокобаламин",
                categoryRepository.findByName("Витамины"),
                manufacturerRepository.findByName("Фармстандарт-Уфимский витаминный завод,ОАО"),
                releaseFormRepository.findByName("Раствор для в/м введения")
        ));
        medicamentCountRepository.save(new MedicamentCount("5 шт", medicamentRepository.findByName("Комбилипен").get(0)));
        medicamentCountRepository.save(new MedicamentCount("10 шт", medicamentRepository.findByName("Комбилипен").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("2 мл", medicamentRepository.findByName("Комбилипен").get(0)));

        //костарокс
        medicamentRepository.save(new Medicament("Костарокс", "Эторикоксиб",
                categoryRepository.findByName("Препараты для лечения костно-мыщечной системы"),
                manufacturerRepository.findByName("Салютас Фарма ГмбХ"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("7 шт", medicamentRepository.findByName("Костарокс").get(0)));
        medicamentCountRepository.save(new MedicamentCount("14 шт", medicamentRepository.findByName("Костарокс").get(0)));
        medicamentCountRepository.save(new MedicamentCount("28 шт", medicamentRepository.findByName("Костарокс").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("60 мг", medicamentRepository.findByName("Костарокс").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("90 мг", medicamentRepository.findByName("Костарокс").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("120 мл", medicamentRepository.findByName("Костарокс").get(0)));

        //Глюкофаж
        medicamentRepository.save(new Medicament("Глюкофаж", "Метформин",
                categoryRepository.findByName("Диабет"),
                manufacturerRepository.findByName("Мерк Хелскеа КГаА"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("30 шт", medicamentRepository.findByName("Глюкофаж").get(0)));
        medicamentCountRepository.save(new MedicamentCount("60 шт", medicamentRepository.findByName("Глюкофаж").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("750 мг", medicamentRepository.findByName("Глюкофаж").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("1000 мг", medicamentRepository.findByName("Глюкофаж").get(0)));

        //Дексаметазон
        medicamentRepository.save(new Medicament("Дексаметазон", "Дексаметазон",
                categoryRepository.findByName("Гормоны для системного применения"),
                manufacturerRepository.findByName("Эллара ООО"),
                releaseFormRepository.findByName("Раствор для в/м введения")
        ));
        medicamentCountRepository.save(new MedicamentCount("10 шт", medicamentRepository.findByName("Дексаметазон").get(0)));
        medicamentCountRepository.save(new MedicamentCount("25 шт", medicamentRepository.findByName("Дексаметазон").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("1 мл", medicamentRepository.findByName("Дексаметазон").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("2 мл", medicamentRepository.findByName("Дексаметазон").get(0)));

        //Генеролон
        medicamentRepository.save(new Medicament("Генеролон", "Миноксидил",
                categoryRepository.findByName("Дерматологические средства"),
                manufacturerRepository.findByName("БЕЛУПО"),
                releaseFormRepository.findByName("Спрей")
        ));
        medicamentCountRepository.save(new MedicamentCount("3 шт", medicamentRepository.findByName("Генеролон").get(0)));
        medicamentCountRepository.save(new MedicamentCount("1 шт", medicamentRepository.findByName("Генеролон").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("2%", medicamentRepository.findByName("Генеролон").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("5%", medicamentRepository.findByName("Генеролон").get(0)));

        //Прадакса
        medicamentRepository.save(new Medicament("Прадакса", "Дабигатрана этексилат",
                categoryRepository.findByName("Препараты для кроветворения и крови"),
                manufacturerRepository.findByName("Берингер Ингельхайм Фарма ГмбХ и Ко.КГ"),
                releaseFormRepository.findByName("Капсулы")
        ));
        medicamentCountRepository.save(new MedicamentCount("30 шт", medicamentRepository.findByName("Прадакса").get(0)));
        medicamentCountRepository.save(new MedicamentCount("60 шт", medicamentRepository.findByName("Прадакса").get(0)));
        medicamentCountRepository.save(new MedicamentCount("180 шт", medicamentRepository.findByName("Прадакса").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("75 мг", medicamentRepository.findByName("Прадакса").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("110 мг", medicamentRepository.findByName("Прадакса").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("150 мг", medicamentRepository.findByName("Прадакса").get(0)));

        //Левомицетин Реневал
        medicamentRepository.save(new Medicament("Левомицетин Реневал", "Хлорамфеникол",
                categoryRepository.findByName("Препараты для лечения органов чувств"),
                manufacturerRepository.findByName("Обновление ПФК  АО"),
                releaseFormRepository.findByName("Раствор для нар. применения")
        ));
        medicamentCountRepository.save(new MedicamentCount("25 мл", medicamentRepository.findByName("Левомицетин Реневал").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("1%", medicamentRepository.findByName("Левомицетин Реневал").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("3%", medicamentRepository.findByName("Левомицетин Реневал").get(0)));

        //аспаркам
        medicamentRepository.save(new Medicament("Аспаркам", "Калия и магния аспарагинат",
                categoryRepository.findByName("Препараты для сердечно-сосудистой системы"),
                manufacturerRepository.findByName("Обновление ПФК  АО"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("24 шт", medicamentRepository.findByName("Аспаркам").get(0)));
        medicamentCountRepository.save(new MedicamentCount("56 шт", medicamentRepository.findByName("Аспаркам").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("500 мг", medicamentRepository.findByName("Аспаркам").get(0)));

        //долгит
        medicamentRepository.save(new Medicament("Долгит", "Ибупрофен",
                categoryRepository.findByName("Препараты для лечения костно-мыщечной системы"),
                manufacturerRepository.findByName("Пфайзер Фармасьютикалз ЭлЭлСи"),
                releaseFormRepository.findByName("Крем")
        ));
        medicamentCountRepository.save(new MedicamentCount("50 гр", medicamentRepository.findByName("Долгит").get(0)));
        medicamentCountRepository.save(new MedicamentCount("100 гр", medicamentRepository.findByName("Долгит").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("5%", medicamentRepository.findByName("Долгит").get(0)));

        //тамсулозин реневал
        medicamentRepository.save(new Medicament("Тамсулозин Реневал", "Тамсулозин",
                categoryRepository.findByName("Препараты для мочеполовой системы и половые гормоны"),
                manufacturerRepository.findByName("Обновление ПФК  АО"),
                releaseFormRepository.findByName("Капсулы")
        ));
        medicamentCountRepository.save(new MedicamentCount("30 шт", medicamentRepository.findByName("Тамсулозин Реневал").get(0)));
        medicamentCountRepository.save(new MedicamentCount("90 шт", medicamentRepository.findByName("Тамсулозин Реневал").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("400 мг", medicamentRepository.findByName("Тамсулозин Реневал").get(0)));


        //Нейронтин
        medicamentRepository.save(new Medicament("Нейронтин", "Габапентин",
                categoryRepository.findByName("Препараты для нервной системы"),
                manufacturerRepository.findByName("Пфайзер Фармасьютикалз ЭлЭлСи"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("50 шт", medicamentRepository.findByName("Тамсулозин Реневал").get(0)));
        medicamentCountRepository.save(new MedicamentCount("100 шт", medicamentRepository.findByName("Тамсулозин Реневал").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("600 мг", medicamentRepository.findByName("Тамсулозин Реневал").get(0)));

        //смекта
        medicamentRepository.save(new Medicament("Смекта", "Смектит диоктаэдрический",
                categoryRepository.findByName("Препараты для пищеварительного тракта и обмена веществ"),
                manufacturerRepository.findByName("Бофур Ипсен Индастри"),
                releaseFormRepository.findByName("Порошок")
        ));
        medicamentCountRepository.save(new MedicamentCount("20 шт", medicamentRepository.findByName("Смекта").get(0)));
        medicamentCountRepository.save(new MedicamentCount("30 шт", medicamentRepository.findByName("Смекта").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("3 г", medicamentRepository.findByName("Смекта").get(0)));

        //арбидол
        medicamentRepository.save(new Medicament("Арбидол", "Умифеновир",
                categoryRepository.findByName("Профилактика ОРВИ и гриппа"),
                manufacturerRepository.findByName("Фармстандарт-Лексредства ОАО"),
                releaseFormRepository.findByName("Порошок")
        ));
        medicamentCountRepository.save(new MedicamentCount("37 гр", medicamentRepository.findByName("Арбидол").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("25 мг", medicamentRepository.findByName("Арбидол").get(0)));

        //Гиотриф
        medicamentRepository.save(new Medicament("Гиотриф", "Афатиниб",
                categoryRepository.findByName("Противоопухолевые препараты и иммуномодуляторы"),
                manufacturerRepository.findByName("Берингер Ингельхайм Фарма ГмбХ и Ко.КГ"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("30 шт", medicamentRepository.findByName("Гиотриф").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("20 мг", medicamentRepository.findByName("Гиотриф").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("30 мг", medicamentRepository.findByName("Гиотриф").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("40 мг", medicamentRepository.findByName("Гиотриф").get(0)));

        //Чемеричная вода
        medicamentRepository.save(new Medicament("Черемичная вода", "Чемерицы настойка",
                categoryRepository.findByName("Противопаразитарные препараты"),
                manufacturerRepository.findByName("ТВЕРСКАЯ ФАРМАЦЕВТИЧЕСКАЯ ФАБРИКА ОАО"),
                releaseFormRepository.findByName("Раствор для нар. применения")
        ));
        medicamentCountRepository.save(new MedicamentCount("100 мл", medicamentRepository.findByName("Черемичная вода").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("50%", medicamentRepository.findByName("Черемичная вода").get(0)));

        //Нош-па
        medicamentRepository.save(new Medicament("Нош-па", "Дротаверин",
                categoryRepository.findByName("Препараты для пищеварительного тракта и обмена веществ"),
                manufacturerRepository.findByName("Опелла Хелскеа Венгрия Лтд."),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("24 шт", medicamentRepository.findByName("Нош-па").get(0)));
        medicamentCountRepository.save(new MedicamentCount("64 шт", medicamentRepository.findByName("Нош-па").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("40 мг", medicamentRepository.findByName("Нош-па").get(0)));

        //Витамин С
        medicamentRepository.save(new Medicament("Витамин C", "Витамин C",
                categoryRepository.findByName("Витамины"),
                manufacturerRepository.findByName("Эвалар"),
                releaseFormRepository.findByName("Таблетки шипучие")
        ));
        medicamentCountRepository.save(new MedicamentCount("10 шт", medicamentRepository.findByName("Витамин C").get(0)));
        medicamentCountRepository.save(new MedicamentCount("20 шт", medicamentRepository.findByName("Витамин C").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("250 мг", medicamentRepository.findByName("Витамин C").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("900 мг", medicamentRepository.findByName("Витамин C").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("1200 мг", medicamentRepository.findByName("Витамин C").get(0)));

        //Ринсулин НПХ
        medicamentRepository.save(new Medicament("Ринсулин НПХ", "Инсулин-изофан человеческий генно-инженерный",
                categoryRepository.findByName("Диабет"),
                manufacturerRepository.findByName("ГЕРОФАРМ"),
                releaseFormRepository.findByName("Суспензия для п/к введения")
        ));
        medicamentCountRepository.save(new MedicamentCount("5 шт", medicamentRepository.findByName("Ринсулин НПХ").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("100МЕ/мл", medicamentRepository.findByName("Ринсулин НПХ").get(0)));

        //Буденофальк
        medicamentRepository.save(new Medicament("Буденофальк", "Будесонид",
                categoryRepository.findByName("Гормоны для системного применения"),
                manufacturerRepository.findByName("Лозан Фарма"),
                releaseFormRepository.findByName("Капсулы")
        ));
        medicamentCountRepository.save(new MedicamentCount("20 шт", medicamentRepository.findByName("Буденофальк").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("3 мг", medicamentRepository.findByName("Буденофальк").get(0)));

        //Надропарин кальция
        medicamentRepository.save(new Medicament("Надропарин кальция", "Надропарин кальция",
                categoryRepository.findByName("Препараты для кроветворения и крови"),
                manufacturerRepository.findByName("Московский эндокринный завод"),
                releaseFormRepository.findByName("Раствор для в/м введения")
        ));
        medicamentCountRepository.save(new MedicamentCount("5 шт", medicamentRepository.findByName("Надропарин кальция").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("0,3 мл", medicamentRepository.findByName("Надропарин кальция").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("0,4 мл", medicamentRepository.findByName("Надропарин кальция").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("0,6 мл", medicamentRepository.findByName("Надропарин кальция").get(0)));

        //Лозартан
        medicamentRepository.save(new Medicament("Лозартан", "Лозартан",
                categoryRepository.findByName("Препараты для сердечно-сосудистой системы"),
                manufacturerRepository.findByName("Пранафарм ООО"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("30 шт", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentCountRepository.save(new MedicamentCount("60 шт", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentCountRepository.save(new MedicamentCount("90 шт", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("12,5 мг", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("25 мг", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("50 мг", medicamentRepository.findByName("Лозартан").get(0)));

        //Дюфастон
        medicamentRepository.save(new Medicament("Дюфастон", "Дидрогестерон",
                categoryRepository.findByName("Препараты для мочеполовой системы и половые гормоны"),
                manufacturerRepository.findByName("Пранафарм ООО"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("20 шт", medicamentRepository.findByName("Дюфастон").get(0)));
        medicamentCountRepository.save(new MedicamentCount("28 шт", medicamentRepository.findByName("Дюфастон").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("10 мг", medicamentRepository.findByName("Дюфастон").get(0)));

        //Итулси
        medicamentRepository.save(new Medicament("Итулси", "Палбоциклиб",
                categoryRepository.findByName("Противоопухолевые препараты и иммуномодуляторы"),
                manufacturerRepository.findByName("Пфайзер Фармасьютикалз ЭлЭлСи"),
                releaseFormRepository.findByName("Капсулы")
        ));
        medicamentCountRepository.save(new MedicamentCount("21 шт", medicamentRepository.findByName("Итулси").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("75 мг", medicamentRepository.findByName("Итулси").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("100 мг", medicamentRepository.findByName("Итулси").get(0)));

        //Немозол
        medicamentRepository.save(new Medicament("Немозол", "Албендазол",
                categoryRepository.findByName("Противопаразитарные препараты"),
                manufacturerRepository.findByName("Ипка Лабораториз Лимитед"),
                releaseFormRepository.findByName("Суспензия для приема внутрь")
        ));
        medicamentCountRepository.save(new MedicamentCount("25 мл", medicamentRepository.findByName("Немозол").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("0,1/5 мл", medicamentRepository.findByName("Немозол").get(0)));

        return new ResponseEntity<>(medicamentRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/autocompletePharmacies")
    public ResponseEntity<List<PharmacyAddress>> autocompletePharmacies()
    {
        pharmacyRepository.save(new Pharmacy("Аптека Вита", "https://vitaexpress.ru/"));
        pharmacyRepository.save(new Pharmacy("Аптека Алия", "http://alia.003.ms/"));
        pharmacyRepository.save(new Pharmacy("Аптека 63", "https://aptekiplus.ru/"));
        pharmacyRepository.save(new Pharmacy("Аптека на Гагарина", "https://aptekanagagarina.ru/"));
        pharmacyRepository.save(new Pharmacy("Фармленд", "https://farmlend.ru/"));

        pharmacyAddressRepository.save(new PharmacyAddress("ул. Куйбышева, 110", pharmacyRepository.findByName("Аптека Вита").get(0)));
        pharmacyAddressRepository.save(new PharmacyAddress("ул. Стара Загора, 85", pharmacyRepository.findByName("Аптека Вита").get(0)));

        pharmacyAddressRepository.save(new PharmacyAddress("ул. Полевая, 86А", pharmacyRepository.findByName("Аптека Алия").get(0)));
        pharmacyAddressRepository.save(new PharmacyAddress("ул. Гагарина, 59", pharmacyRepository.findByName("Аптека Алия").get(0)));

        pharmacyAddressRepository.save(new PharmacyAddress("просп. Кирова, 425", pharmacyRepository.findByName("Аптека 63").get(0)));
        pharmacyAddressRepository.save(new PharmacyAddress("ул. Ново-Вокзальная, 146А", pharmacyRepository.findByName("Аптека 63").get(0)));

        pharmacyAddressRepository.save(new PharmacyAddress("ул. Гагарина, 49", pharmacyRepository.findByName("Аптека на Гагарина").get(0)));

        pharmacyAddressRepository.save(new PharmacyAddress("ул. Революционная, 125", pharmacyRepository.findByName("Фармленд").get(0)));
        pharmacyAddressRepository.save(new PharmacyAddress("ул. Аэродромная, 47Б", pharmacyRepository.findByName("Фармленд").get(0)));

        return new ResponseEntity<>(pharmacyAddressRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/autocompleteAvailabilities")
    public ResponseEntity<List<MedicamentAvailability>> autocompleteMedicamentAvailabilities()
    {
        //катионорм
       medicamentAvailabilityRepository.save(new MedicamentAvailability(750.0,
                medicamentCountRepository.findByMedicament(medicamentRepository.findByName("Катионорм").get(0)).get(0),
                null,
                pharmacyAddressRepository.findByAddress("ул. Куйбышева, 110").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(760.0,
                medicamentCountRepository.findByMedicament(medicamentRepository.findByName("Катионорм").get(0)).get(0),
                null,
                pharmacyAddressRepository.findByAddress("просп. Кирова, 425").get(0)));

        //Имудон
        medicamentAvailabilityRepository.save(new MedicamentAvailability(409.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Имудон").get(0), "24 шт").get(0),
                medicamentDosageRepository.findByMedicament(medicamentRepository.findByName("Имудон").get(0)).get(0),
                pharmacyAddressRepository.findByAddress("ул. Революционная, 125").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(510.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Имудон").get(0), "24 шт").get(0),
                medicamentDosageRepository.findByMedicament(medicamentRepository.findByName("Имудон").get(0)).get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(574.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Имудон").get(0), "40 шт").get(0),
                medicamentDosageRepository.findByMedicament(medicamentRepository.findByName("Имудон").get(0)).get(0),
                pharmacyAddressRepository.findByAddress("ул. Аэродромная, 47Б").get(0)));

        //Акридерм ГК
        medicamentAvailabilityRepository.save(new MedicamentAvailability(562.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Акридерм ГК").get(0), "15 гр").get(0),
                null,
                pharmacyAddressRepository.findByAddress("ул. Полевая, 86А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(550.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Акридерм ГК").get(0), "15 гр").get(0),
                null,
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(803.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Акридерм ГК").get(0), "30 гр").get(0),
                null,
                pharmacyAddressRepository.findByAddress("просп. Кирова, 425").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(801.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Акридерм ГК").get(0), "30 гр").get(0),
                null,
                pharmacyAddressRepository.findByAddress("ул. Куйбышева, 110").get(0)));

        //Комбилипен
        medicamentAvailabilityRepository.save(new MedicamentAvailability(251.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Комбилипен").get(0), "5 шт").get(0),
                medicamentDosageRepository.findByMedicament(medicamentRepository.findByName("Комбилипен").get(0)).get(0),
                pharmacyAddressRepository.findByAddress("ул. Ново-Вокзальная, 146А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(263.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Комбилипен").get(0), "5 шт").get(0),
                medicamentDosageRepository.findByMedicament(medicamentRepository.findByName("Комбилипен").get(0)).get(0),
                pharmacyAddressRepository.findByAddress("ул. Революционная, 125").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(375.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Комбилипен").get(0), "10 шт").get(0),
                medicamentDosageRepository.findByMedicament(medicamentRepository.findByName("Комбилипен").get(0)).get(0),
                pharmacyAddressRepository.findByAddress("ул. Революционная, 125").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(345.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Комбилипен").get(0), "10 шт").get(0),
                medicamentDosageRepository.findByMedicament(medicamentRepository.findByName("Комбилипен").get(0)).get(0),
                pharmacyAddressRepository.findByAddress("ул. Ново-Вокзальная, 146А").get(0)));

        //костарокс
        medicamentAvailabilityRepository.save(new MedicamentAvailability(328.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Костарокс").get(0), "7 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Костарокс").get(0), "90 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(446.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Костарокс").get(0), "7 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Костарокс").get(0), "120 мл").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(327.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Костарокс").get(0), "14 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Костарокс").get(0), "60 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Революционная, 125").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(337.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Костарокс").get(0), "14 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Костарокс").get(0), "60 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(705.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Костарокс").get(0), "28 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Костарокс").get(0), "60 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        //Глюкофаж
        medicamentAvailabilityRepository.save(new MedicamentAvailability(265.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Глюкофаж").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Глюкофаж").get(0), "750 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Куйбышева, 110").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(267.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Глюкофаж").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Глюкофаж").get(0), "750 мг").get(0),
                pharmacyAddressRepository.findByAddress("просп. Кирова, 425").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(237.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Глюкофаж").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Глюкофаж").get(0), "750 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Революционная, 125").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(495.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Глюкофаж").get(0), "60 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Глюкофаж").get(0), "750 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Революционная, 125").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(510.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Глюкофаж").get(0), "60 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Глюкофаж").get(0), "750 мг").get(0),
                pharmacyAddressRepository.findByAddress("просп. Кирова, 425").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(362.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Глюкофаж").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Глюкофаж").get(0), "1000 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

        //Дексаметазон
        medicamentAvailabilityRepository.save(new MedicamentAvailability(287.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Дексаметазон").get(0), "25 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Дексаметазон").get(0), "1 мл").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(294.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Дексаметазон").get(0), "25 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Дексаметазон").get(0), "1 мл").get(0),
                pharmacyAddressRepository.findByAddress("ул. Революционная, 125").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(300.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Дексаметазон").get(0), "25 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Дексаметазон").get(0), "1 мл").get(0),
                pharmacyAddressRepository.findByAddress("ул. Ново-Вокзальная, 146А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(123.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Дексаметазон").get(0), "10 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Дексаметазон").get(0), "1 мл").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(133.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Дексаметазон").get(0), "10 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Дексаметазон").get(0), "1 мл").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 49").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(145.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Дексаметазон").get(0), "10 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Дексаметазон").get(0), "2 мл").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 49").get(0)));

        //Генеролон
        medicamentAvailabilityRepository.save(new MedicamentAvailability(931.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Генеролон").get(0), "1 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Генеролон").get(0), "2%").get(0),
                pharmacyAddressRepository.findByAddress("ул. Куйбышева, 110").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(1004.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Генеролон").get(0), "1 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Генеролон").get(0), "2%").get(0),
                pharmacyAddressRepository.findByAddress("просп. Кирова, 425").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(1150.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Генеролон").get(0), "1 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Генеролон").get(0), "5%").get(0),
                pharmacyAddressRepository.findByAddress("просп. Кирова, 425").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(2403.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Генеролон").get(0), "3 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Генеролон").get(0), "5%").get(0),
                pharmacyAddressRepository.findByAddress("ул. Полевая, 86А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(2403.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Генеролон").get(0), "3 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Генеролон").get(0), "5%").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        //Прадакса
        medicamentAvailabilityRepository.save(new MedicamentAvailability(2041.83,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Прадакса").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Прадакса").get(0), "75 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(2031.82,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Прадакса").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Прадакса").get(0), "75 мг").get(0),
                pharmacyAddressRepository.findByAddress("просп. Кирова, 425").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(1982.89,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Прадакса").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Прадакса").get(0), "110 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(3661.94,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Прадакса").get(0), "60 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Прадакса").get(0), "110 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(3761.94,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Прадакса").get(0), "60 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Прадакса").get(0), "110 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Полевая, 86А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(10993.94,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Прадакса").get(0), "180 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Прадакса").get(0), "110 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Полевая, 86А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(2040.64,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Прадакса").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Прадакса").get(0), "150 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 49").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(2040.64,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Прадакса").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Прадакса").get(0), "150 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Куйбышева, 110").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(2040.64,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Прадакса").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Прадакса").get(0), "150 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Ново-Вокзальная, 146А").get(0)));


        //Левомицетин Реневал
        medicamentAvailabilityRepository.save(new MedicamentAvailability(117.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Левомицетин Реневал").get(0), "25 мл").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Левомицетин Реневал").get(0), "1%").get(0),
                pharmacyAddressRepository.findByAddress("ул. Ново-Вокзальная, 146А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(113.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Левомицетин Реневал").get(0), "25 мл").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Левомицетин Реневал").get(0), "1%").get(0),
                pharmacyAddressRepository.findByAddress("ул. Полевая, 86А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(123.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Левомицетин Реневал").get(0), "25 мл").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Левомицетин Реневал").get(0), "1%").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(112.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Левомицетин Реневал").get(0), "25 мл").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Левомицетин Реневал").get(0), "1%").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(112.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Левомицетин Реневал").get(0), "25 мл").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Левомицетин Реневал").get(0), "3%").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(114.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Левомицетин Реневал").get(0), "25 мл").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Левомицетин Реневал").get(0), "3%").get(0),
                pharmacyAddressRepository.findByAddress("ул. Полевая, 86А").get(0)));


        //аспаркам
        medicamentAvailabilityRepository.save(new MedicamentAvailability(67.99,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "24 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Полевая, 86А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(63.45,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "24 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(69.65,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "24 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(71.13,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "24 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Ново-Вокзальная, 146А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(67.45,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "24 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Куйбышева, 110").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(67.99,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "24 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Полевая, 86А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(149.99,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "56 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Гагарина, 59").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(152.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "56 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(147.67,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "56 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Ново-Вокзальная, 146А").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(139.09,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Аспаркам").get(0), "56 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Аспаркам").get(0), "500 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Куйбышева, 110").get(0)));


        //тамсулозин реневал
        medicamentAvailabilityRepository.save(new MedicamentAvailability(401.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Тамсулозин Реневал").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Тамсулозин Реневал").get(0), "400 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Куйбышева, 110").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(421.0,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Тамсулозин Реневал").get(0), "30 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Тамсулозин Реневал").get(0), "400 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

        medicamentAvailabilityRepository.save(new MedicamentAvailability(975.96,
                medicamentCountRepository.findByMedicamentAndCount(medicamentRepository.findByName("Тамсулозин Реневал").get(0), "90 шт").get(0),
                medicamentDosageRepository.findByMedicamentAndDosage(medicamentRepository.findByName("Тамсулозин Реневал").get(0), "400 мг").get(0),
                pharmacyAddressRepository.findByAddress("ул. Стара Загора, 85").get(0)));

/*      //долгит
        medicamentRepository.save(new Medicament("Долгит", "Ибупрофен",
                categoryRepository.findByName("Препараты для лечения костно-мыщечной системы"),
                manufacturerRepository.findByName("Пфайзер Фармасьютикалз ЭлЭлСи"),
                releaseFormRepository.findByName("Крем")
        ));
        medicamentCountRepository.save(new MedicamentCount("50 гр", medicamentRepository.findByName("Долгит").get(0)));
        medicamentCountRepository.save(new MedicamentCount("100 гр", medicamentRepository.findByName("Долгит").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("5%", medicamentRepository.findByName("Долгит").get(0)));

        //Нейронтин
        medicamentRepository.save(new Medicament("Нейронтин", "Габапентин",
                categoryRepository.findByName("Препараты для нервной системы"),
                manufacturerRepository.findByName("Пфайзер Фармасьютикалз ЭлЭлСи"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("50 шт", medicamentRepository.findByName("Тамсулозин Реневал").get(0)));
        medicamentCountRepository.save(new MedicamentCount("100 шт", medicamentRepository.findByName("Тамсулозин Реневал").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("600 мг", medicamentRepository.findByName("Тамсулозин Реневал").get(0)));

        //смекта
        medicamentRepository.save(new Medicament("Смекта", "Смектит диоктаэдрический",
                categoryRepository.findByName("Препараты для пищеварительного тракта и обмена веществ"),
                manufacturerRepository.findByName("Бофур Ипсен Индастри"),
                releaseFormRepository.findByName("Порошок")
        ));
        medicamentCountRepository.save(new MedicamentCount("20 шт", medicamentRepository.findByName("Смекта").get(0)));
        medicamentCountRepository.save(new MedicamentCount("30 шт", medicamentRepository.findByName("Смекта").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("3 г", medicamentRepository.findByName("Смекта").get(0)));

        //арбидол
        medicamentRepository.save(new Medicament("Арбидол", "Умифеновир",
                categoryRepository.findByName("Профилактика ОРВИ и гриппа"),
                manufacturerRepository.findByName("Фармстандарт-Лексредства ОАО"),
                releaseFormRepository.findByName("Порошок")
        ));
        medicamentCountRepository.save(new MedicamentCount("37 гр", medicamentRepository.findByName("Арбидол").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("25 мг", medicamentRepository.findByName("Арбидол").get(0)));

        //Гиотриф
        medicamentRepository.save(new Medicament("Гиотриф", "Афатиниб",
                categoryRepository.findByName("Противоопухолевые препараты и иммуномодуляторы"),
                manufacturerRepository.findByName("Берингер Ингельхайм Фарма ГмбХ и Ко.КГ"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("30 шт", medicamentRepository.findByName("Гиотриф").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("20 мг", medicamentRepository.findByName("Гиотриф").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("30 мг", medicamentRepository.findByName("Гиотриф").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("40 мг", medicamentRepository.findByName("Гиотриф").get(0)));

        //Чемеричная вода
        medicamentRepository.save(new Medicament("Черемичная вода", "Чемерицы настойка",
                categoryRepository.findByName("Противопаразитарные препараты"),
                manufacturerRepository.findByName("ТВЕРСКАЯ ФАРМАЦЕВТИЧЕСКАЯ ФАБРИКА ОАО"),
                releaseFormRepository.findByName("Раствор для наружного применения")
        ));
        medicamentCountRepository.save(new MedicamentCount("100 мл", medicamentRepository.findByName("Черемичная вода").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("50%", medicamentRepository.findByName("Черемичная вода").get(0)));

        //Нош-па
        medicamentRepository.save(new Medicament("Нош-па", "Дротаверин",
                categoryRepository.findByName("Препараты для пищеварительного тракта и обмена веществ"),
                manufacturerRepository.findByName("Опелла Хелскеа Венгрия Лтд."),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("24 шт", medicamentRepository.findByName("Нош-па").get(0)));
        medicamentCountRepository.save(new MedicamentCount("64 шт", medicamentRepository.findByName("Нош-па").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("40 мг", medicamentRepository.findByName("Нош-па").get(0)));

        //Витамин С
        medicamentRepository.save(new Medicament("Витамин C", "Витамин C",
                categoryRepository.findByName("Витамины"),
                manufacturerRepository.findByName("Эвалар"),
                releaseFormRepository.findByName("Таблетки шипучие")
        ));
        medicamentCountRepository.save(new MedicamentCount("10 шт", medicamentRepository.findByName("Витамин C").get(0)));
        medicamentCountRepository.save(new MedicamentCount("20 шт", medicamentRepository.findByName("Витамин C").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("250 мг", medicamentRepository.findByName("Витамин C").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("900 мг", medicamentRepository.findByName("Витамин C").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("1200 мг", medicamentRepository.findByName("Витамин C").get(0)));

        //Ринсулин НПХ
        medicamentRepository.save(new Medicament("Ринсулин НПХ", "Инсулин-изофан человеческий генно-инженерный",
                categoryRepository.findByName("Диабет"),
                manufacturerRepository.findByName("ГЕРОФАРМ"),
                releaseFormRepository.findByName("Суспензия для подкожного введения")
        ));
        medicamentCountRepository.save(new MedicamentCount("5 шт", medicamentRepository.findByName("Ринсулин НПХ").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("100МЕ/мл", medicamentRepository.findByName("Ринсулин НПХ").get(0)));

        //Буденофальк
        medicamentRepository.save(new Medicament("Буденофальк", "Будесонид",
                categoryRepository.findByName("Гормоны для системного применения"),
                manufacturerRepository.findByName("Лозан Фарма"),
                releaseFormRepository.findByName("Капсулы")
        ));
        medicamentCountRepository.save(new MedicamentCount("20 шт", medicamentRepository.findByName("Буденофальк").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("3 мг", medicamentRepository.findByName("Буденофальк").get(0)));

        //Надропарин кальция
        medicamentRepository.save(new Medicament("Надропарин кальция", "Надропарин кальция",
                categoryRepository.findByName("Препараты для кроветворения и крови"),
                manufacturerRepository.findByName("Московский эндокринный завод"),
                releaseFormRepository.findByName("Раствор для внутримыщечного введения")
        ));
        medicamentCountRepository.save(new MedicamentCount("5 шт", medicamentRepository.findByName("Надропарин кальция").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("0,3 мл", medicamentRepository.findByName("Надропарин кальция").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("0,4 мл", medicamentRepository.findByName("Надропарин кальция").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("0,6 мл", medicamentRepository.findByName("Надропарин кальция").get(0)));

        //Лозартан
        medicamentRepository.save(new Medicament("Лозартан", "Лозартан",
                categoryRepository.findByName("Препараты для сердечно-сосудистой системы"),
                manufacturerRepository.findByName("Пранафарм ООО"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("30 шт", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentCountRepository.save(new MedicamentCount("60 шт", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentCountRepository.save(new MedicamentCount("90 шт", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("12,5 мг", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("25 мг", medicamentRepository.findByName("Лозартан").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("50 мг", medicamentRepository.findByName("Лозартан").get(0)));

        //Дюфастон
        medicamentRepository.save(new Medicament("Дюфастон", "Дидрогестерон",
                categoryRepository.findByName("Препараты для мочеполовой системы и половые гормоны"),
                manufacturerRepository.findByName("Пранафарм ООО"),
                releaseFormRepository.findByName("Таблетки")
        ));
        medicamentCountRepository.save(new MedicamentCount("20 шт", medicamentRepository.findByName("Дюфастон").get(0)));
        medicamentCountRepository.save(new MedicamentCount("28 шт", medicamentRepository.findByName("Дюфастон").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("10 мг", medicamentRepository.findByName("Дюфастон").get(0)));

        //Итулси
        medicamentRepository.save(new Medicament("Итулси", "Палбоциклиб",
                categoryRepository.findByName("Противоопухолевые препараты и иммуномодуляторы"),
                manufacturerRepository.findByName("Пфайзер Фармасьютикалз ЭлЭлСи"),
                releaseFormRepository.findByName("Капсулы")
        ));
        medicamentCountRepository.save(new MedicamentCount("21 шт", medicamentRepository.findByName("Итулси").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("75 мг", medicamentRepository.findByName("Итулси").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("100 мг", medicamentRepository.findByName("Итулси").get(0)));

        //Немозол
        medicamentRepository.save(new Medicament("Немозол", "Албендазол",
                categoryRepository.findByName("Противопаразитарные препараты"),
                manufacturerRepository.findByName("Ипка Лабораториз Лимитед"),
                releaseFormRepository.findByName("Суспензия для приема внутрь")
        ));
        medicamentCountRepository.save(new MedicamentCount("25 мл", medicamentRepository.findByName("Немозол").get(0)));
        medicamentDosageRepository.save(new MedicamentDosage("0,1/5 мл", medicamentRepository.findByName("Немозол").get(0)));

         */

        return new ResponseEntity<>(medicamentAvailabilityRepository.findAll(), HttpStatus.OK);
    }

    private SearchResponse convertMedicamentToSearchResponse(Medicament medicament)
    {
        List<MedicamentCount> medicamentCounts = medicamentCountRepository.findByMedicament(medicament);
        List<MedicamentDosage> medicamentDosages = medicamentDosageRepository.findByMedicament(medicament);
        Set<MedicamentAvailability> availabilities = new HashSet<>();
        for (MedicamentCount medicamentCount : medicamentCounts)
        {
            availabilities.addAll(medicamentAvailabilityRepository.findByMedicamentCount(medicamentCount));
        }

        for (MedicamentDosage medicamentDosage : medicamentDosages)
        {
            availabilities.addAll(medicamentAvailabilityRepository.findByMedicamentDosage(medicamentDosage));
        }


        Double minPrice = 0.0;
        if (availabilities.size() > 0)
            minPrice = Collections.min(availabilities.stream()
                .map(MedicamentAvailability::getPrice)
                .collect(Collectors.toList()));

        int pharmacyCount = availabilities.size();

        return new SearchResponse(medicament, minPrice, pharmacyCount);
    }

    /*
    На всякий случай здесь можно посмотреть исходную информацию о лекарствах
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
     */

    /*
    print("Введите время в часах (H) и минутах (М)")
    h = int(input("h = "))
    m = int(input("m = "))
     */
}
