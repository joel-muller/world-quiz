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

const flag_similarity = `country,flag similarity,flag similarity:de,flag similarity:es,flag similarity:fr,flag similarity:nb,flag similarity:cs,flag similarity:ru,flag similarity:nl,flag similarity:sv,flag similarity:pt,flag similarity:zh,flag similarity:zh-tw,flag similarity:pl,flag similarity:it,flag similarity:da
Andorra,"Moldova (wider, coat of arms with eagle)","Republik Moldau/Moldawien (breiter, Wappen mit Adler)","Moldavia (más ancha, escudo con águila)","Moldavie (plus large, blason avec un aigle)","Moldova (bredere, våpenskjold med ørn)","Moldavsko (širší, erb s orlem)","Молдова (шире, герб с орлом)","Moldavië (breder, wapen met adelaar)","Moldavien (bredare, vapensköld med örn)","Moldávia (mais larga, brasão com águia)",摩尔多瓦(更宽，鹰纹),摩爾多瓦(更寬、老鷹),"Mołdawia (szersza, godło z orłem)","Moldavia (più larga, stemma con un'aquila)","Moldova (bredere, våbenskjold med ørn)"
Austria,"Latvia (darker red, narrower white band)","Lettland (dunkleres rot, dünneres weißes Band)","Letonia (rojo más oscuro, banda blanca más estrecha)","Lettonie (rouge plus foncé, bande blanche plus étroite)","Latvia (mørkere rød, smalere hvitt bånd)","Lotyšsko (tmavší červená, užší bílý pruh)","Латвия (более темная красная, более узкая белая полоса)","Letland (donkerder rood, smallere witte band)","Lettland (mörkare röd, smalare vitt band)","Letônia (vermelho mais escuro, faixa branca mais estreita)",拉脱维亚(较深的红色，较窄的白色带),拉脫維亞(更深紅、窄白線),"Łotwa (ciemniejsza czerwień, węższy biały pas)","Lettonia (rosso più scuro, banda bianca più stretta)","Letland (mørkere rød, smallere hvid stribe)"
Faroe Islands,"Iceland (blue background, red and white cross), Norway (red background, blue and white cross)","Island (blauer Hintergrund,rotes und weißes Kreuz), Norwegen (roter Hintergrund, blaues und weißes Kreuz)","Islandia (fondo azul, cruz roja y blanca), Noruega (fondo rojo, cruz azul y blanca)","Islande (fond bleu, croix rouge et blanche), Norvège (fond rouge, croix bleue et blanche)","Island (blå bakgrunn, rødt og hvitt kors), Norge (rød bakgrunn, blått og hvitt kors)","Island (modré pozadí, červený a bílý kříž), Norsko (červené pozadí, modrý a bílý kříž)","Исландия (синий фон, красный и белый крест), Норвегия (красный фон, синий и белый крест)","IJsland (blauwe achtergrond, rood met wit kruis), Noorwegen (rode achtergrond, blauw met wit kruis)","Island (blå bakgrund, rött och vitt kors), Norge (röd bakgrund, blått och vitt kors)","Islândia (fundo azul, cruz vermelha e branca) e Noruega (fundo vermelho, cruz azul e branca)",冰岛(蓝底，红白交叉)，挪威(红底，蓝白交叉),冰島(藍底、紅白十字)、挪威(紅底、藍白十字),"Islandia (niebieskie tło, czerwono-biały krzyż), Norwegia (czerwone tło, niebiesko-biały krzyż)","Islanda (sfondo blu, croce rossa e bianca), Norvegia (sfondo rosso, croce blu e bianca)","Island (blå baggrund, rødt og hvidt kors), Norge (rød baggrund, blåt og hvidt kors)"
Iceland,"Norway (red background, blue cross), Faroe Islands (white background, red and blue cross)","Norwegen (roter Hintergrund, blaues Kreuz), Färöer (weißer Hintergrund, rotes und blaues Kreuz)","Noruega (fondo rojo, cruz azul), Islas Feroe (fondo blanco, cruz roja y azul)","Norvège (fond rouge, croix bleue), Îles Féroé (fond blanc, croix rouge et bleue)","Norge (rød bakgrunn, blått kors), Færøyene (hvit bakgrunn, rødt og blått kors)","Norsko (červené pozadí, modrý a bílý kříž), Faerské ostrovy (bílé pozadí, červený a modrý kříž)","Норвегия (красный фон, синий крест), Фарерские острова (Белый фон, красный и синий крест)","Noorwegen (rode achtergrond, blauw kruis), Faeröer (witte achtergrond, rood/blauw kruis)","Norge (röd bakgrund, blått kors), Färöarna (vit bakgrund, rött och blått kors)","Noruega (fundo vermelho, cruz azul) e Ilhas Faroé (fundo branco, cruz vermelha e azul)",挪威(红底蓝十字)、法罗群岛(白底红蓝十字),挪威(紅底、藍十字),"Norwegia (czerwone tło, niebieski krzyż), Wyspy Owcze (białe tło, czerwono-niebieski krzyż)","Norvegia (sfondo rosso, croce blu), Fær Øer (sfondo bianco, croce rossa e blu)","Norge (rød baggrund, blåt og hvidt kors), Færøerne (hvid baggrund, rødt og blåt kors)"`

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

func TestApplyLineFlagSimilarity(t *testing.T) {
	r := csv.NewReader(strings.NewReader(flag_similarity))

	places := []entities.Place{
		{Name: "Andorra"},
		{Name: "Austria"},
		{Name: "Faroe Islands"},
		{Name: "Iceland"},
	}

	count := 0
	for {
		record, err := r.Read()
		if err != nil {
			break
		}
		applyFlagSimilarity(count, record, &places)
		count++
	}

	expectedInfos := []string{
		"Moldova (wider, coat of arms with eagle)",
		"Latvia (darker red, narrower white band)",
		"Iceland (blue background, red and white cross), Norway (red background, blue and white cross)",
		"Norway (red background, blue cross), Faroe Islands (white background, red and blue cross)",
	}

	for i, place := range places {
		if place.FlagInfo != expectedInfos[i] {
			t.Errorf("Place[%d] FlagSimilarity = %q; want %q", i, place.FlagInfo, expectedInfos[i])
		}
	}
}
