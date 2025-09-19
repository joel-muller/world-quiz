package reader

import (
	"encoding/csv"
	"strings"
	"testing"
	"world-quiz/internal/entities"
)

const main = `England,"<img src=""ug-flag-england.svg"" />","<img src=""ug-map-england.png"" />",,UG::Europe
Scotland,"<img src=""ug-flag-scotland.svg"" />","<img src=""ug-map-scotland.png"" />",,UG::Europe
United Kingdom,"<img src=""ug-flag-united_kingdom.svg"" />","<img src=""ug-map-united_kingdom.png"" />",GB,"UG::Europe, UG::Sovereign_State"
Northern Ireland,,"<img src=""ug-map-northern_ireland.png"" />",,UG::Europe
France,"<img src=""ug-flag-france.svg"" />","<img src=""ug-map-france.png"" />",FR,"UG::Europe, UG::European_Union, UG::Mediterranean, UG::Sovereign_State"
Wales,"<img src=""ug-flag-wales.svg"" />","<img src=""ug-map-wales.png"" />",,UG::Europe
Georgia,"<img src=""ug-flag-georgia.svg"" />","<img src=""ug-map-georgia.png"" />",GE,"UG::Europe, UG::Sovereign_State"
Germany,"<img src=""ug-flag-germany.svg"" />","<img src=""ug-map-germany.png"" />",DE,"UG::Europe, UG::European_Union, UG::Sovereign_State"`

const capital = `England,London,London,Londres,Londres,London,Londýn,Лондон,Londen,London,Londres,伦敦(London),倫敦 (London),Londyn,Londra,London
Scotland,Edinburgh,Edinburgh,Edimburgo,Édimbourg,Edinburgh,Edinburgh,Эдинбург,Edinburgh,Edinburgh,Edimburgo,爱丁堡(Edinburgh),愛丁堡 (Edinburgh),Edynburg,Edimburgo,Edinburgh
United Kingdom,London,London,Londres,Londres,London,Londýn,Лондон,Londen,London,Londres,伦敦(London),倫敦 (London),Londyn,Londra,London
Northern Ireland,Belfast,Belfast,Belfast,Belfast,Belfast,Belfast,Белфаст,Belfast,Belfast,Belfaste,贝尔法斯特(Belfast),貝爾法斯特 (Belfast),Belfast,Belfast,Belfast
France,Paris,Paris,París,Paris,Paris,Paříž,Париж,Parijs,Paris,Paris,巴黎(Paris),巴黎 (Paris),Paryż,Parigi,Paris
Wales,Cardiff,Cardiff,Cardiff,Cardiff,Cardiff,Cardiff,Кардифф,Cardiff,Cardiff,Cardife,卡迪夫(Cardiff),卡地夫 (Cardiff),Cardiff,Cardiff,Cardiff
Georgia,Tbilisi,Tiflis,Tiflis,Tbilissi,Tbilisi,Tbilisi,Тбилиси,Tbilisi,Tbilisi,Tiblissi,第比利斯(Tbilisi),第比利斯 (Tbilisi),Tbilisi,Tbilisi,Tbilisi
Germany,Berlin,Berlin,Berlín,Berlin,Berlin,Berlín,Берлин,Berlijn,Berlin,Berlim,柏林(Berlin),柏林 (Berlin),Berlin,Berlino,Berlin`

const capital_info = `Greenland,,,,,På dansk Godthåb.,,Также известен как Готхоб,,,,,,,,
Finland,,,,,"Det finske navnet er Helsinki, men Utenriksdepartementet i Norge anbefaler å bruke byens svenske navn.",,,,,,,,,,
Luxembourg,Officially Luxembourg.,,,,,,Официально Люксембург.,,,,官方称为卢森堡(Luxembourg)。,,,,
Montenegro,Cetinje is an honorary capital.,Cetinje ist eine Ehrenhauptstadt.,Cetiña es una capital honoraria.,Cetinje est une capitale honoraire.,Administrativ hovedstad. Cetinje er historisk hovedstad og presidentens tilholdssted.,Cetinje je historicky hlavní město.,Цетине - почетная столица.,Voormalige hoofdstad is Cetinje.,Cetinje är hedershuvudstad.,Cetinje é uma capital honorária.,采蒂涅(Cetinje)，黑山古都和宗教、历史中心。,策提涅是歷史首都。,Honorową stolicą jest Cetynia.,Cettigne è una capitale onoraria.,Cetinje har status som Montenegros gamle royale hovedstad.
Netherlands,"While Amsterdam is the official capital, The Hague is the seat of the executive and legislative branches.","Amsterdam ist die offizielle Hauptstadt, während Den Haag Parlaments- und Regierungssitz ist.","Aunque Ámsterdam es la capital oficial, La Haya es la sede del gobierno y del poder legislativo.","Bien qu'Amsterdam soit la capitale officielle, La Haye est le siège du gouvernement et du parlement.",Nasjonalforsamling og regjering i Haag.,"Zatímco Amsterdam je oficiální hlavní město, Haag je sídlo zákonodárné a výkonné moci.","В то время как Амстердам является официальной столицей, Гаага является резиденцией исполнительной и законодательной ветвей власти.","Amsterdam is de officiële hoofdstad, hoewel het parlement en de regering in Den Haag zetelen.",Haag är administrativ huvudstad.,"Embora Amsterdã seja a capital oficial, Haia é a sede dos ramos executivo e legislativo.",虽然阿姆斯特丹(Amsterdam)是官方首都，但海牙(Hague)是行政和立法部门的所在地。,阿姆斯特丹是正式首都，行政立法首都為海牙。,"Oficjalną stolicą jest Amsterdam, natomiast siedzibą władz jest Haga.","Nonostante Amsterdam sia la capitale ufficiale, L'Aia è la sede del governo e del parlamento.","Selvom Amsterdam er den forfatningsmæssige hovedstad, ligger regeringen i Haag."`

const country_info = `England,Constituent country of the United Kingdom.,Landesteil des Vereinigten Königreichs.,Nación constitutiva del Reino Unido.,Nation constitutive du Royaume-Uni.,Land som utgjør en del av Storbritannia.,Konstituční země Velké Británie.,"Страна, входящая в состав Великобритании.",Autonoom gebied binnen het Verenigd Koninkrijk.,Riksdel av Storbritannien.,País constituinte do Reino Unido.,英国属地之一。,英國的構成國。,Kraj stanowiący część Wielkiej Brytanii.,Nazione costitutiva del Regno Unito.,Delvis selvstyrende nation i Storbritannien.
Scotland,Constituent country of the United Kingdom.,Landesteil des Vereinigten Königreichs.,Nación constitutiva del Reino Unido.,Nation constitutive du Royaume-Uni.,Land som utgjør en del av Storbritannia.,Konstituční země Velké Británie.,"Страна, входящая в состав Великобритании.",Autonoom gebied binnen het Verenigd Koninkrijk.,Riksdel av Storbritannien.,País constituinte do Reino Unido.,英国属地之一。,英國的構成國。,Kraj stanowiący część Wielkiej Brytanii.,Nazione costitutiva del Regno Unito.,Delvis selvstyrende nation i Storbritannien.
United Kingdom,,,,,Også kalt Det forente kongeriket Storbritannia og Nord-Irland.,Známé též jako Velká Británie.,,,Formellt Förenade kungariket.,,,,Znana także jako Zjednoczone Królestwo.,,Officielt benævnt Det Forenede Kongerige Storbritannien og Nordirland.
Northern Ireland,Constituent country of the United Kingdom.,Landesteil des Vereinigten Königreichs.,Nación constitutiva del Reino Unido.,Nation constitutive du Royaume-Uni.,Land som utgjør en del av Storbritannia.,Konstituční země Velké Británie.,"Страна, входящая в состав Великобритании.",Autonoom gebied binnen het Verenigd Koninkrijk.,Riksdel av Storbritannien.,País constituinte do Reino Unido.,英国属地之一。,英國的構成國。,Kraj stanowiący część Wielkiej Brytanii.,Nazione costitutiva del Regno Unito.,Delvis selvstyrende nation i Storbritannien.`

func TestApplyLineMain_WithManualEntities(t *testing.T) {
	r := csv.NewReader(strings.NewReader(main))

	expectedPlaces := []entities.Place{
		{Id: 1, Name: "England", Flag: "ug-flag-england.svg", Maps: "ug-map-england.png", RegionCode: "", Tags: []entities.Tag{entities.Europe}},
		{Id: 2, Name: "Scotland", Flag: "ug-flag-scotland.svg", Maps: "ug-map-scotland.png", RegionCode: "", Tags: []entities.Tag{entities.Europe}},
		{Id: 3, Name: "United Kingdom", Flag: "ug-flag-united_kingdom.svg", Maps: "ug-map-united_kingdom.png", RegionCode: "GB", Tags: []entities.Tag{entities.Europe, entities.Sovereign_States}},
		{Id: 4, Name: "Northern Ireland", Flag: "", Maps: "ug-map-northern_ireland.png", RegionCode: "", Tags: []entities.Tag{entities.Europe}},
		{Id: 5, Name: "France", Flag: "ug-flag-france.svg", Maps: "ug-map-france.png", RegionCode: "FR", Tags: []entities.Tag{entities.Europe, entities.European_Union, entities.Mediterranean, entities.Sovereign_States}},
		{Id: 6, Name: "Wales", Flag: "ug-flag-wales.svg", Maps: "ug-map-wales.png", RegionCode: "", Tags: []entities.Tag{entities.Europe}},
		{Id: 7, Name: "Georgia", Flag: "ug-flag-georgia.svg", Maps: "ug-map-georgia.png", RegionCode: "GE", Tags: []entities.Tag{entities.Europe, entities.Sovereign_States}},
		{Id: 8, Name: "Germany", Flag: "ug-flag-germany.svg", Maps: "ug-map-germany.png", RegionCode: "DE", Tags: []entities.Tag{entities.Europe, entities.European_Union, entities.Sovereign_States}},
	}

	var places []entities.Place
	count := 0
	for {
		record, err := r.Read()
		if err != nil {
			break
		}
		applyLineMain(count, record, &places)
		count++
	}

	if len(places) != len(expectedPlaces) {
		t.Fatalf("places length = %d; want %d", len(places), len(expectedPlaces))
	}

	for i, place := range places {
		exp := expectedPlaces[i]
		if place.Name != exp.Name {
			t.Errorf("Place[%d] Name = %q; want %q", i, place.Name, exp.Name)
		}
		if place.Flag != exp.Flag {
			t.Errorf("Place[%d] Flag = %q; want %q", i, place.Flag, exp.Flag)
		}
		if place.Maps != exp.Maps {
			t.Errorf("Place[%d] Maps = %q; want %q", i, place.Maps, exp.Maps)
		}
		if place.RegionCode != exp.RegionCode {
			t.Errorf("Place[%d] RegionCode = %q; want %q", i, place.RegionCode, exp.RegionCode)
		}
		if len(place.Tags) != len(exp.Tags) {
			t.Errorf("Place[%d] Tags length = %d; want %d", i, len(place.Tags), len(exp.Tags))
		}
	}
}

func TestApplyLineCapital(t *testing.T) {
	r := csv.NewReader(strings.NewReader(capital))

	// initial places to apply capital updates
	places := []entities.Place{
		{Name: "England"},
		{Name: "Scotland"},
		{Name: "United Kingdom"},
		{Name: "Northern Ireland"},
		{Name: "France"},
		{Name: "Wales"},
		{Name: "Georgia"},
		{Name: "Germany"},
	}

	count := 0
	for {
		record, err := r.Read()
		if err != nil {
			break
		}
		applyLineCapital(count, record, &places)
		count++
	}

	expectedCapitals := []string{
		"London", "Edinburgh", "London", "Belfast", "Paris", "Cardiff", "Tbilisi", "Berlin",
	}

	for i, place := range places {
		if place.Capital != expectedCapitals[i] {
			t.Errorf("Place[%d] Capital = %q; want %q", i, place.Capital, expectedCapitals[i])
		}
	}
}

func TestApplyLineCapitalInfo(t *testing.T) {
	r := csv.NewReader(strings.NewReader(capital_info))

	places := []entities.Place{
		{Name: "Greenland"},
		{Name: "Finland"},
		{Name: "Luxembourg"},
		{Name: "Montenegro"},
		{Name: "Netherlands"},
	}

	count := 0
	for {
		record, err := r.Read()
		if err != nil {
			break
		}
		applyLineCapitalInfo(count, record, &places)
		count++
	}

	expectedInfos := []string{
		"",                                // Greenland
		"",                                // Finland
		"Officially Luxembourg.",          // Luxembourg
		"Cetinje is an honorary capital.", // Montenegro
		"While Amsterdam is the official capital, The Hague is the seat of the executive and legislative branches.", // Netherlands
	}

	for i, place := range places {
		if place.CapitalInfo != expectedInfos[i] {
			t.Errorf("Place[%d] CapitalInfo = %q; want %q", i, place.CapitalInfo, expectedInfos[i])
		}
	}
}

func TestApplyLineCountryInfo(t *testing.T) {
	r := csv.NewReader(strings.NewReader(country_info))

	places := []entities.Place{
		{Name: "England"},
		{Name: "Scotland"},
		{Name: "United Kingdom"},
		{Name: "Northern Ireland"},
	}

	count := 0
	for {
		record, err := r.Read()
		if err != nil {
			break
		}
		applyLineCountryInfo(count, record, &places)
		count++
	}

	expectedInfos := []string{
		"Constituent country of the United Kingdom.",
		"Constituent country of the United Kingdom.",
		"",
		"Constituent country of the United Kingdom.",
	}

	for i, place := range places {
		if place.PlaceInfo != expectedInfos[i] {
			t.Errorf("Place[%d] PlaceInfo = %q; want %q", i, place.PlaceInfo, expectedInfos[i])
		}
	}
}
