package petrocar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class EntityRepository {

	public List<Entity> katalog = new ArrayList<Entity>();
	public List<Entity> zamienniki = new ArrayList<Entity>();
	public List<Entity> tytuly = new ArrayList<Entity>();
	public List<String> katalogNaglowki = new ArrayList<String>();
	public List<String> zamiennikiNaglowki = new ArrayList<String>();
	public List<String> tytulyNaglowki = new ArrayList<String>();
	public Set<String> kodTowaru = new HashSet<String>();
	public ModelRepository modelRepository;
	// mapa przechowujaca liczbê braków modeli w bazie
	public Map<String, Integer> missingModels = new HashMap<String, Integer>();
	public Logger mainLog = Logger.getLogger("mainLog");
	public Logger errLog = Logger.getLogger("errLog");
	public Logger altLog = Logger.getLogger("altLog");
	public Logger foundModelsLog = Logger.getLogger("foundModelsLog");
	public Logger missingModelsLog = Logger.getLogger("missingModelsLog");
	public Logger titleLog = Logger.getLogger("titleLog");
	public Logger readLog = Logger.getLogger("readLog");
	public Properties properties = new Properties();

	/**
	 * zmienne przechowuj¹ce fragmenty kodu HTML wczytywanego z pliku
	 */
	public String begin, end, prefixHeader, prefixValue, suffixHeader, suffixValue;

	public Set<String> getKodTowaru() {
		return kodTowaru;
	}

	public void setKodTowaru(Set<String> kodTowaru) {
		this.kodTowaru = kodTowaru;
	}

	public String getBegin() {
		return begin;
	}

	public void setBegin(String begin) {
		this.begin = begin;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getPrefixHeader() {
		return prefixHeader;
	}

	public void setPrefixHeader(String prefixHeader) {
		this.prefixHeader = prefixHeader;
	}

	public String getPrefixValue() {
		return prefixValue;
	}

	public void setPrefixValue(String prefixValue) {
		this.prefixValue = prefixValue;
	}

	public String getSuffixHeader() {
		return suffixHeader;
	}

	public void setSuffixHeader(String suffixHeader) {
		this.suffixHeader = suffixHeader;
	}

	public String getSuffixValue() {
		return suffixValue;
	}

	public void setSuffixValue(String suffixValue) {
		this.suffixValue = suffixValue;
	}

	public List<Entity> getKatalog() {
		return katalog;
	}

	public void setKatalog(List<Entity> katalog) {
		this.katalog = katalog;
	}

	public List<Entity> getZamienniki() {
		return zamienniki;
	}

	public void setZamienniki(List<Entity> zamienniki) {
		this.zamienniki = zamienniki;
	}

	public List<Entity> getTytuly() {
		return tytuly;
	}

	public void setTytuly(List<Entity> tytuly) {
		this.tytuly = tytuly;
	}

	public List<String> getKatalogNaglowki() {
		return katalogNaglowki;
	}

	public void setKatalogNaglowki(List<String> katalogNaglowki) {
		this.katalogNaglowki = katalogNaglowki;
	}

	public List<String> getZamiennikiNaglowki() {
		return zamiennikiNaglowki;
	}

	public void setZamiennikiNaglowki(List<String> zamiennikiNaglowki) {
		this.zamiennikiNaglowki = zamiennikiNaglowki;
	}

	public List<String> getTytulyNaglowki() {
		return tytulyNaglowki;
	}

	public void setTytulyNaglowki(List<String> tytulyNaglowki) {
		this.tytulyNaglowki = tytulyNaglowki;
	}

	public ModelRepository getModelRepository() {
		return modelRepository;
	}

	public void setModelRepository(ModelRepository modelRepository) {
		this.modelRepository = modelRepository;
	}

	public Map<String, Integer> getMissingModels() {
		return missingModels;
	}

	public void setMissingModels(Map<String, Integer> missingModels) {
		this.missingModels = missingModels;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String readHtml(String filePath) {
		String html = "";
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			while ((line = br.readLine()) != null) {
				html += line;
			}
		} catch (FileNotFoundException e) {
			errLog.error("Nieudana proba wczytania wartosci z pliku " + filePath, e);
			e.printStackTrace();
		} catch (IOException e) {
			errLog.error("Nieudana proba wczytania wartosci z pliku " + filePath, e);
			e.printStackTrace();
		}

		return html;
	}

	public List<String> readHeaders(String filePath, String separator) {
		mainLog.info("Wczytanie naglowka z pliku " + filePath + ", separator: " + separator);
		List<String> headers = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String firstLine = br.readLine();
			String[] headerFields = firstLine.split(separator);
			readLog.info("Znaleziono nag³ówki : " + headerFields.length);
			// dodanie naglowka do listy
			if (headerFields.length > 2) {
				for (String s : headerFields) {
					s = s.replace("\"", "");
					readLog.info("dodanie nag³ówka " + s);
					headers.add(s);
				}
			} else {
				errLog.error("zbyt ma³a liczba kolumn z pliku " + filePath + ", tylko " + headerFields.length);
			}

		} catch (FileNotFoundException e) {
			errLog.error("Nieudana proba wczytania naglowka z pliku " + filePath, e);
			e.printStackTrace();
		} catch (IOException e) {
			errLog.error("Nieudana proba wczytania naglowka z pliku " + filePath, e);
			e.printStackTrace();
		}
		return headers;
	}

	public List<Entity> readValues(String filePath, String separator, int index) {
		List<Entity> entities = new ArrayList<Entity>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			// celowe pominiecie pierwszego rekordu bedacego naglowkiem!
			String line = br.readLine();
			readLog.info("Odczyt wartoœci");
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				line = line.toUpperCase().replace("\"", "");

				List<String> list = new ArrayList();
				String[] fields = line.split(separator);
				if (fields.length >= 2) {
					for (String s : fields) {
						s = s.replace("\"", "");
						// System.out.println("s : "+s);
						list.add(s);
					}
				} else {
					// System.out.println("PROBLEM");
				}
				readLog.info("line = " + line + ", tablica =" + fields.length + ", lista=" + list.size());
				entities.add(new Entity(fields[index].replaceAll("\"", ""), list));
			}

		} catch (FileNotFoundException e) {
			errLog.error("Nieudana proba wczytania wartosci z pliku " + filePath, e);
			e.printStackTrace();
		} catch (IOException e) {
			errLog.error("Nieudana proba wczytania wartosci z pliku " + filePath, e);
			e.printStackTrace();
		}

		return entities;
	}

	/**
	 * Do zrealizowania w drugim etapie
	 * 
	 * @param katalogFilePath
	 * @param katalogSeparator
	 * @param zamiennikiFilePath
	 * @param zamiennikiSeparator
	 * @param tytulyFilePath
	 * @param tytulySeparator
	 */
	public EntityRepository(Properties properties) {
		this.properties = properties;

		String katalogFilePath = this.properties.getProperty("sciezkaKatalogu");
		String zamiennikiFilePath = this.properties.getProperty("sciezkaZamiennikow");
		String tytulyFilePath = this.properties.getProperty("sciezkaModele");
		String katalogSeparator = this.properties.getProperty("katalogSeparator");
		String zamiennikiSeparator = this.properties.getProperty("zamiennikiSeparator");
		String tytulySeparator = this.properties.getProperty("modeleSeparator");
		if (katalogFilePath == this.properties.getProperty("sciezkaKatalogu")) {
			System.out.println("sciezki takie same");
		} else {
			System.out.println("sciezki rozne - prop: i string");
			System.out.println(this.properties.getProperty("sciezkaKatalogu") + "<");
			System.out.println(katalogFilePath + "<");
		}
		this.katalogNaglowki = readHeaders(katalogFilePath, katalogSeparator);
		this.zamiennikiNaglowki = readHeaders(zamiennikiFilePath, zamiennikiSeparator);
		this.tytulyNaglowki = readHeaders(tytulyFilePath, tytulySeparator);
		this.katalog = readValues(katalogFilePath, katalogSeparator, 0);
		this.zamienniki = readValues(zamiennikiFilePath, zamiennikiSeparator, 3);
		// tytuly do usuniecia?
		this.tytuly = readValues(tytulyFilePath, tytulySeparator, 0);
		this.modelRepository = new ModelRepository(tytulyFilePath, tytulySeparator);
		// matchowanie
		matchModelFromBase(katalog, 1, modelRepository);

		// match model wykorzystywany w poprzednich wersjach - do usuniecia
		// matchModel(katalog, 1, modelRepository);
		readHtmlInput();
		splitList(this.katalog, kodTowaru, 0);
	}

	public EntityRepository(String katalogFilePath, String katalogSeparator, String zamiennikiFilePath,
			String zamiennikiSeparator) {

		this.katalogNaglowki = readHeaders(katalogFilePath, katalogSeparator);
		this.zamiennikiNaglowki = readHeaders(zamiennikiFilePath, zamiennikiSeparator);
		this.katalog = readValues(katalogFilePath, katalogSeparator, 0);
		this.zamienniki = readValues(zamiennikiFilePath, zamiennikiSeparator, 3);
		readHtmlInput();
		splitList(this.katalog, kodTowaru, 0);
	}

	/*
	 * Czesc wspolna dla obu konstruktorow
	 */
	public void readHtmlInput() {
		altLog.trace("Wczytano " + this.zamienniki.size() + " zamiennikow");
		// printResult(this.katalogNaglowki, "katalogNag³ówki");
		// printResult(this.zamiennikiNaglowki, "zamiennikiNaglowki");
		// printEntityResult(this.katalog, "katalog");
		// printEntityResult(this.zamienniki, "zamienniki");
		this.kodTowaru = findUniqeKodTowaru(katalog, 0);
		this.begin = readHtml("C:\\petrocar\\html\\begin.txt");
		this.end = readHtml("C:\\petrocar\\html\\end.txt");
		this.prefixHeader = readHtml("C:\\petrocar\\html\\prefixHeader.txt");
		this.prefixValue = readHtml("C:\\petrocar\\html\\prefixValue.txt");
		this.suffixHeader = readHtml("C:\\petrocar\\html\\suffixHeader.txt");
		this.suffixValue = readHtml("C:\\petrocar\\html\\suffixValue.txt");
	}

	public void printResult(List<String> list, String title) {
		System.out.println(title);
		for (String s : list) {
			System.out.println("nag³ówek : " + s.toString());
		}
	}

	public void printEntityResult(List<Entity> list, String title) {
		System.out.println(title);
		for (Entity e : list) {
			System.out.println(e.toString());
		}
	}

	/**
	 * metoda znajduje unikalne kody towaru wystepujace w liscie
	 * katalogowej/zamiennikow/tytulow
	 * 
	 * @param list
	 * @param index
	 * @return
	 */
	public Set<String> findUniqeKodTowaru(List<Entity> list, int index) {
		Set<String> kody = new HashSet<String>();
		for (Entity e : list) {
			kody.add(e.getValues().get(index));
			// System.out.println("get(0): " + e.getValues().get(index).toString());
		}
		return kody;
	}

	/**
	 * metoda zwraca liste odpowiadajaca kodzie produktu i usuwa z glownego zbioru
	 * 
	 * @param list
	 * @param pattern
	 * @param index
	 *            - okresla polozenie klucza
	 * @return
	 */
	public List<Entity> createAndRemoveSubList(List<Entity> list, String pattern, int index) {
		mainLog.info("wczytywana lista ma " + list.size() + " elementów");
		List<Entity> subList = new ArrayList<Entity>();
		mainLog.info("pattern=" + pattern + "<");
		for (Entity e : list) {
			// znaleziono pasujaca encje

			if (e.getValues().get(index).equals(pattern)) {
				mainLog.info("dodane index=" + e.getValues().get(index) + "<");
				subList.add(e);
			} else {
				// mainLog.info("odrzucone index="+e.getValues().get(index)+"<"+"wzorcowy kod
				// towaru="+pattern+"<");
				// mainLog.info("index dlugosc="+e.getValues().get(index).length()+", pattern
				// dlugosc="+pattern.length());
			}
			if (isTheSame(e.getValues().get(index), pattern)) {
				// mainLog.info("TEST zakonczony sukcesem");
			} else {
				// mainLog.info("TEST zakonczony pora¿k¹");
			}
		}
		mainLog.info("lista przed usunieciem ma " + list.size() + " elementow");
		list.removeAll(subList);
		mainLog.info("lista po usuniecu ma " + list.size() + " elementow");
		mainLog.info("subLista ma " + subList.size() + " elementów");
		return subList;
	}

	public boolean isTheSame(String s1, String s2) {
		boolean result = false;
		if (s1.length() == s2.length()) {
			char[] c1 = s1.toCharArray();
			char[] c2 = s2.toCharArray();
			if (c1.length == c2.length) {
				// mainLog.info("D³ugoœæ tablic ta sama - wszystko póki co OK");
				result = true;
				for (int i = 0; i < c1.length; i++) {
					/// System.out.println("c1="+c1[i]+", c2="+c2[i]);
					if (c1[i] != c2[i]) {
						result = false;
						break;
					}
				}
				return result;
			} else {
				mainLog.trace("D³ugoœæ tablic jest ró¿na - stringi s¹ ró¿ne c1=" + c1.length + ", c2=" + c2.length);
				return false;
			}

		} else {
			mainLog.trace("D³ugoœæ stringów jest ró¿na");
			return false;
		}

	}

	/**
	 * metoda odpowiedzialna za iteracje wszystkich kodow towarow
	 * 
	 * @param list
	 * @param patterns
	 * @param index
	 */
	public void splitList(List<Entity> list, Set<String> patterns, int index) {
		saveHeader("c:\\petrocar\\wynik.csv");
		for (Iterator it = patterns.iterator(); it.hasNext();) {
			String pattern = (String) it.next();
			mainLog.info("iteracja subKatalog dla wzoru : " + pattern);
			List<Entity> subKatalog = createAndRemoveSubList(this.getKatalog(), pattern, index);
			mainLog.info("dla kodu " + pattern + " znaleziono " + subKatalog.size() + " katalogów");
			// uwaga na index!
			mainLog.info("iteracja subZamienniki dla wzoru : " + pattern);
			List<Entity> subZamienniki = createAndRemoveSubList(this.getZamienniki(), pattern, 3);
			mainLog.info("dla kodu " + pattern + " znaleziono " + subZamienniki.size() + " zamienników");
			List<Entity> subTytuly = createAndRemoveSubList(this.getTytuly(), pattern, index);

			List<String> optimalizedTitles = null;
			if (this.getProperties().getProperty("algorytm").contains("1")) {
				titleLog.info("WYBRANO algorytm nr 1");
				optimalizedTitles = optimizeTitle(subKatalog,
						Integer.parseInt(this.getProperties().getProperty("maxDlugoscTytulu")));
			} else if (this.getProperties().getProperty("algorytm").contains("2")) {
				titleLog.info("WYBRANO algorytm nr 2");
				optimalizedTitles = optimizeTitleWithParameters(subKatalog,
						Integer.parseInt(this.getProperties().getProperty("maxDlugoscTytulu")));
			}
			String result = createResult(subKatalog, subZamienniki, subTytuly, 1, 2, 0);
			// saveResult("c:\\petrocar\\wynik.csv", pattern, result);
			saveResult("c:\\petrocar\\wynik.csv", pattern, result, optimalizedTitles);
			mainLog.info("RESULT : ");
			mainLog.info(result);
			mainLog.info("#################################################################");
		}
		mainLog.info("koniec iteracji kodów towaru");
	}

	/**
	 * metoda optymalizujaca tytul z parametrami - druga wersja algorytmu
	 * 
	 * @param subkatalog
	 * @param maxLength
	 * @return
	 */
	public List<String> optimizeTitleWithParameters(List<Entity> subkatalog, int maxLength) {
		List<String> titles = new ArrayList<String>(); // zbior wszystkich tytu³ów

		String title = ""; // aktualny tytul
		String prefix ="";
		try {
			int prefixNumber = Integer.parseInt(this.getProperties().getProperty("tytulPrefix"))-1;
			prefix = subkatalog.get(0).getValues().get(prefixNumber);
		}catch(Exception e) {
			System.err.println("Brak mapowania"+e);
//			System.err.println("Brak mapowania prefixu tytu³u, rozmiar values : "+subkatalog.get(1).getValues().size());
//			System.err.println("0 "+subkatalog.get(0).getValues().get(0));
//			System.err.println("1 "+subkatalog.get(0).getValues().get(1));
//			System.err.println("2 "+subkatalog.get(0).getValues().get(2));
		}
		title=prefix;
		String[] title_parts = new String[7]; // marka/model/pojemnosc/paliwo/opis/rok_min/rok_max
		for (int i = 0; i < title_parts.length; i++)
			title_parts[i] = "";
		Entity previousRecord = null;
		try {
			previousRecord = subkatalog.get(0);
		} catch (Exception e) {
			titleLog.error("pusty subKatalog");
		}
		titleLog.info("WEJSCIE optimizeTitleWithParameters()");
		for (int i = 0; i < subkatalog.size(); i++) {
			Entity currentRecord = subkatalog.get(i);
			titleLog.info("iteracja : " + i);

			titleLog.info("Poprzedni model : " + previousRecord.toString());
			titleLog.info("Obecny model    : " + currentRecord.toString());
			if (currentRecord.getModel().getModel() != null && currentRecord.getModel().getMark() != null) {
				titleLog.info("Wejscie w strefe continue");

				if (this.getProperties().getProperty("pojemnosc").contains("ON")
						&& currentRecord.getModel().getCapacity() == null) {
					currentRecord.getModel().setCapacity("");
					// previousRecord=currentRecord;
					// continue;
				}
				if (this.getProperties().getProperty("paliwo").contains("ON")
						&& currentRecord.getModel().getFuel() == null) {
					currentRecord.getModel().setFuel("");
					// previousRecord=currentRecord;
					// continue;
				}
				if (this.getProperties().getProperty("opis").contains("ON")
						&& currentRecord.getModel().getDescription() == null) {
					currentRecord.getModel().setDescription("");
					// previousRecord=currentRecord;
					// continue;
				}
			} else {
				previousRecord = currentRecord;
				continue;
			}

			////
			// koniecznie zrob logowanie nieparsowanych plików
			/////
			if (previousRecord != currentRecord) {
				titleLog.info("nastêpny rekord jest ró¿ny od poprzedniego");
				if (previousRecord.getModel().getMark() != currentRecord.getModel().getMark()) {
					titleLog.info("MARKI ró¿ne, zapisuje dotychczasowy tytul do listy");

					//////////////////////////////
					titleLog.info("Próba dodania parts do title -1");
					String returnedTitle = addTitle(title, title_parts, maxLength);
					if (returnedTitle != null) {
						title = returnedTitle;
						titleLog.info("dodano parts, znaki : " + title.length() + " aktualny tytu³ : " + title);
						// dodano parts do tytu³u
						title_parts[0] = currentRecord.getModel().getMark();
						title_parts[1] = " " + currentRecord.getModel().getModel();
					} else {
						titleLog.info("nie dodano parts do tytulu - zbyt du¿a liczba znaków by³aby po dodaniu");
						;
						titles.add(title);
						showCurrentTitleList(titles);
						title = prefix; // wyzerowanie bie¿¹cego tytu³u
						title_parts[0] = currentRecord.getModel().getMark();
						title_parts[1] = " " + currentRecord.getModel().getModel();
						title_parts[2] = "";
						title_parts[3] = "";
						title_parts[4] = "";
						title_parts[5] = "";
						title_parts[6] = "";
					}

					////////////////////////////
				} else {
					titleLog.info("MARKI te same");
				}
				// kontynuacja niezale¿nie od nowej marki
				if (i == 68) {
					titleLog.info("pause - check");
				}
				if (previousRecord.getModel().getModel() != currentRecord.getModel().getModel()) {
					titleLog.info("MODELE ró¿ne, zapisuje dotychczasowy tytul do listy");
					String returnedTitle;

					titleLog.info("Próba dodania parts do title -2");
					returnedTitle = addTitle(title, title_parts, maxLength);
					titleLog.info("returnedTitle : " + returnedTitle);
					if (returnedTitle != null) {
						title = returnedTitle;
						titleLog.info("dodano parts, znaki : " + title.length() + " aktualny tytu³ : " + title);
						title_parts[0] = currentRecord.getModel().getMark();
						title_parts[1] = " " + currentRecord.getModel().getModel();
						// dodano parts do tytu³u
					} else {
						titleLog.info("nie dodano parts do tytulu - zbyt du¿a liczba znaków by³aby po dodaniu");
						;
						titles.add(title);
						showCurrentTitleList(titles);
						title = prefix; // wyzerowanie bie¿¹cego tytu³u
						title_parts[0] = currentRecord.getModel().getMark();
						title_parts[1] = " " + currentRecord.getModel().getModel();
						title_parts[2] = "";
						title_parts[3] = "";
						title_parts[4] = "";
						title_parts[5] = "";
						title_parts[6] = "";
						titleLog.info("nowy model : " + title_parts[1]);
					}

				} else {
					titleLog.info("MODELE te same");
				}
				String tempPojemnosc = "";
				String tempPaliwo = "";
				String tempOpis = "";
				String tempRokMin = "";
				String tempRokMax = "";

				// dla pojemnosci, opisu, paliwa i rocznikow sprawdzana jest tablica String[]
				// title_parts
				if (this.getProperties().getProperty("pojemnosc").contains("ON")
						&& !title_parts[2].contains(currentRecord.getModel().getCapacity())) {
					titleLog.info("POJEMNOŒÆ ró¿ne");
					tempPojemnosc = currentRecord.getModel().getCapacity().replaceAll(" ", "");
				} else {
					titleLog.info("POJEMNOŒÆ ta sama lub ta opcja zosta³a pominiêta w ustawieniach");
				}

				if (!title_parts[3].contains(" " + currentRecord.getModel().getFuel())
						&& this.getProperties().getProperty("paliwo").contains("ON")) {
					titleLog.info("PALIWA ró¿ne");
					tempPaliwo = currentRecord.getModel().getFuel().replaceAll(" ", "");
				} else {
					titleLog.info("PALIWA te same lub ta opcja zosta³a pominiêta w ustawieniach");
					// titleLog.info("Paliwo : "+currentRecord.getModel().getFuel());
					// titleLog.info("obecny rekord : "+currentRecord.getModel().toString());
				}

				if (this.getProperties().getProperty("opis").contains("ON")
						&& !title_parts[4].contains(currentRecord.getModel().getDescription())) {
					titleLog.info("OPISY ró¿ne");
					tempOpis = currentRecord.getModel().getDescription().replaceAll(" ", "");
				} else {
					titleLog.info("OPISY te same lub ta opcja zosta³a pominiêta w ustawieniach");
				}
				// dodanie roczników
				if (this.getProperties().getProperty("rocznik").contains("ON")) {
					int newMinYear;
					try {
						newMinYear = Integer.parseInt(title_parts[5]);
					} catch (Exception e) {
						newMinYear = 99;
					}
					int newMaxYear;
					try {
						newMaxYear = Integer.parseInt(title_parts[6]);
					} catch (Exception e) {
						newMaxYear = 0;
					}
					if (Integer.parseInt(currentRecord.getModel().getMinDate()) < newMinYear)
						tempRokMin = String.valueOf(Integer.parseInt(currentRecord.getModel().getMinDate()));
					else
						tempRokMin = String.valueOf(newMinYear);
					int currentMaxYear;
					int titlePartsMaxYear;
					try {
						currentMaxYear = Integer.parseInt(currentRecord.getModel().getMaxDate());
					} catch (Exception e) {
						currentMaxYear = 99;
					}
					try {
						titlePartsMaxYear = Integer.parseInt(title_parts[6]);
					} catch (Exception e) {
						titlePartsMaxYear = 99;
					}
					if (currentMaxYear == 99)
						title_parts[6] = "max";
					if (currentMaxYear > titlePartsMaxYear)
						title_parts[6] = String.valueOf(currentMaxYear);

				}
				// próba dodania do tytu³u
				if (isOverLength(title_parts, tempPojemnosc, tempPaliwo, tempOpis, tempRokMin, tempRokMax, maxLength)) {
					titleLog.info("Dodanie rekordu do tytu³u");
					if (!title_parts[2].contains(tempPojemnosc))
						title_parts[2] += tempPojemnosc;
					else
						titleLog.info("POJEMNOŒÆ - pominiêcie w dodawaniu");
					if (!title_parts[3].contains(" " + tempPaliwo))
						title_parts[3] += tempPaliwo;
					else
						titleLog.info("PALIWO - pominiêcie w dodawaniu");
					if (!title_parts[4].contains(tempOpis)) {
						String pp = tempOpis.replaceAll(",", " ");
						String[] podzial = pp.split(" ");
						for (String s : podzial) {
							if (!title_parts[4].contains(s)) {
								title_parts[4] += s;
							}
						}

					} else
						titleLog.info("OPIS - pominiêcie w dodawaniu");
					if (title_parts[6] == "max" || tempRokMax == "max") {
						// model produkowany obecnie
						try {

							if (Integer.parseInt(tempRokMin) < Integer.parseInt(title_parts[5]))
								title_parts[5] = tempRokMin;
						} catch (Exception e) {
							titleLog.error("problem z dodaniem daty zakoñczenia produkcji " + "tempRokMin=" + tempRokMin
									+ ", minRok=" + title_parts[5]);
						}
						title_parts[6] = "max";
					} else {
						// model z zakonczon¹ produkcj¹
						title_parts[6] = "max";
					}

				} else {
					titleLog.info("dodanie tytu³u do Listy i stworzenie nowego tytu³u");
					titleLog.info("Próba dodania parts do title -3");

					/////////////////
					String returnedTitle = addTitle(title, title_parts, maxLength);
					if (returnedTitle != null) {
						title = returnedTitle;
						titleLog.info("dodano parts, znaki : " + title.length() + " aktualny tytu³ : " + title);
						title_parts[0] = currentRecord.getModel().getMark();
						title_parts[1] = " " + currentRecord.getModel().getModel();
						// dodano parts do tytu³u
					} else {
						titleLog.info("nie dodano parts do tytulu - zbyt du¿a liczba znaków by³aby po dodaniu");
						;
						titles.add(title);
						showCurrentTitleList(titles);
						title = prefix; // wyzerowanie bie¿¹cego tytu³u
						title_parts[0] = currentRecord.getModel().getMark();
						title_parts[1] = " " + currentRecord.getModel().getModel();
						title_parts[2] = "";
						title_parts[3] = "";
						title_parts[4] = "";
						title_parts[5] = "";
						title_parts[6] = "";
					}
				}
				previousRecord = currentRecord;

			} else {
				titleLog.info("nastêpny rekord jest taki sam jak poprzedni");
				previousRecord = currentRecord;
				// przypisanie pocz¹tkowych wartoœci
				try {
					title_parts[0] = currentRecord.getModel().getMark() + " ";
					title_parts[1] = currentRecord.getModel().getModel() + " ";
					if (this.getProperties().getProperty("pojemnosc").contains("ON"))
						title_parts[2] = currentRecord.getModel().getCapacity() + " ";
					if (this.getProperties().getProperty("paliwo").contains("ON"))
						title_parts[3] = currentRecord.getModel().getFuel() + " ";
					if (this.getProperties().getProperty("opis").contains("ON"))
						title_parts[4] = currentRecord.getModel().getDescription() + " ";
					if (this.getProperties().getProperty("rocznik").contains("ON")) {
						try {
							title_parts[5] = currentRecord.getModel().getMinDate();
						} catch (Exception e) {
							titleLog.error("nieudana próba przypisania roczników z pierwszego rekordu MINdate"
									+ e.getMessage());
						}
						try {
							title_parts[6] = currentRecord.getModel().getMaxDate();
						} catch (Exception e) {
							titleLog.error("nieudana próba przypisania roczników z pierwszego rekordu MAXdate"
									+ e.getMessage());
						}
					}

				} catch (Exception e) {
					titleLog.error("nieudana próba przypisania wartoœci z pierwszego rekordu" + e.getMessage());
				}
			}
		}
		// dodanie ostatniego urobku
		String lastTitle = addTitle(title, title_parts, maxLength);
		titleLog.info("ostatni tutu³ akcji : " + lastTitle);
		titles.add(lastTitle);

		return titles;
	}

	public boolean isOverLength(String[] parts, String tempPojemnosc, String tempPaliwo, String tempOpis,
			String tempMinRok, String tempMaxRok, int maxLength) {
		if (tempPojemnosc.length() > 0)
			parts[2] += " " + tempPojemnosc;
		if (tempPaliwo.length() > 0)
			parts[3] += " " + tempPaliwo;
		if (tempOpis.length() > 0)
			parts[4] += " " + tempOpis;
		if (parts[6] == "max" || tempMaxRok == "max") {
			// model produkowany obecnie
			try {
				if (Integer.parseInt(tempMinRok) < Integer.parseInt(parts[5]))
					parts[5] = tempMinRok;
			} catch (Exception e) {
				if (this.properties.getProperty("rocznik").contains("ON")) {
					titleLog.error("problem z dodaniem daty rozpoczecia produkcji " + "tempRokMin=" + tempMinRok
							+ ", minRok=" + parts[5]);
				}
			}

			parts[6] = "max";
		} else {
			// model z zakonczon¹ produkcj¹
			try {
				if (Integer.parseInt(tempMaxRok) > Integer.parseInt(parts[6]))
					parts[6] = tempMaxRok;
			} catch (Exception e) {
				if (this.properties.getProperty("rocznik").contains("ON")) {
					titleLog.error("problem z dodaniem daty zakoñczenia produkcji " + "tempRokMin=" + tempMaxRok
							+ ", minRok=" + parts[6]);
				}
			}

		}
		titleLog.info("ZLICZANIE : " + parts[0] + parts[1] + parts[2] + parts[3] + parts[4] + "[" + parts[5] + "-"
				+ parts[6] + "]");
		if (countCharacters(parts) <= maxLength - 8) {
			titleLog.info("mo¿na dodaæ do tytulu bie¿¹cy rekord - liczba znakow : " + countCharacters(parts));
			return true;
		} else {
			titleLog.info("nie mo¿na dodaæ do tytu³u - za du¿o znaków - liczba znakow : " + countCharacters(parts));
			return false;
		}
	}

	public int countCharacters(String[] parts) {
		int result = 0;
		for (String s : parts) {
			result += s.length();
		}
		return result;
	}

	/**
	 * metoda odpowiadaj¹ca tylko za skladanie tytulow
	 * 
	 * @param title
	 * @param titles_parts
	 * @param maxLength
	 * @return
	 */
	public String addTitle(String title, String[] titles_parts, int maxLength) {

		int currentLength;
		try {
			currentLength = title.length();
		} catch (Exception e) {
			currentLength = 0;
		}

		// zliczanie znaków
		if (!title.contains(titles_parts[0])) {
			// dodanie nowej marki
			currentLength += titles_parts[0].length();
		}
		if (!title.contains(titles_parts[1])) {
			// dodanie nowego modelu
			currentLength += titles_parts[1].length();
			currentLength++;
		}
		/// reszta tytu³u wg wskazan w pliku config.propertsies
		if (this.getProperties().getProperty("pojemnosc").contains("ON")) {
			currentLength += titles_parts[2].length();
			currentLength++;
		}
		if (this.getProperties().getProperty("paliwo").contains("ON")) {
			currentLength += titles_parts[3].length();
			currentLength++;
		}
		if (this.getProperties().getProperty("rocznik").contains("ON")) {
			currentLength += 6;
			currentLength++;
		}
		if (this.getProperties().getProperty("opis").contains("ON")) {
			// currentLength+=titles_parts[4].length(); //poni¿ej poprawiona metoda
			currentLength++;

			String zmiana = titles_parts[4].replaceAll(",", " ");
			String[] podzial = zmiana.split(" ");
			for (String s : podzial) {
				if (!title.contains(s))
					currentLength += s.length();
			}

		}
		titleLog.info("przy probie dodania wysz³o znaków : " + currentLength);

		if (currentLength <= maxLength) {
			titleLog.info("dodanie czêœci sk³adowych do tytu³u");
			// mo¿na dodaæ
			if (!title.contains(titles_parts[0])) {
				// dodanie nowej marki
				title += " " + titles_parts[0];
			}
			if (!title.contains(titles_parts[1])) {
				// dodanie nowego modelu
				title += " " + titles_parts[1];
			}
			/// reszta tytu³u wg wskazan w pliku config.propertsies
			if (this.getProperties().getProperty("pojemnosc").contains("ON")) {
				title += " " + titles_parts[2];
			}
			if (this.getProperties().getProperty("paliwo").contains("ON")) {
				title += " " + titles_parts[3];
			}
			if (this.getProperties().getProperty("rocznik").contains("ON")) {
				title += " " + titles_parts[5];
				title += " " + titles_parts[6];
			}
			if (this.getProperties().getProperty("opis").contains("ON")) {
				String zmiana = titles_parts[4].replaceAll(",", " ");
				String[] podzial = zmiana.split(" ");
				for (String s : podzial) {
					if (!title.contains(s))
						title += " " + s;
				}
				// title += " " + titles_parts[4];
			}
		} else {
			titleLog.info("nie mo¿na dodaæ czêœci sk³¹dowych do tytu³u - zwrócenie null");
			// zbyt d³ugi ci¹g - nie mo¿na dodaæ
			return null;

		}

		return title.trim().replaceAll("  ", " ");

	}

	/**
	 * metoda grupujaca tytuly w celu optymalizacji d³ugoœci tytu³u - pierwsza
	 * wersja algorytmu
	 * 
	 * @param subKatalog
	 * @param maxLength
	 * @return
	 */
	public List<String> optimizeTitle(List<Entity> subKatalog, int maxLength) {
		// Pair<String, List<List<Entity>>> optimizedList = new Pair<String,
		// List<List<Entity>>>("",
		// new ArrayList<List<Entity>>());

		String pr = "";
		String title = "";
		List<String> titles = new ArrayList<String>();

		// sortowanie

		// Collections.sort(subKatalog, new Comparator<Entity>(){
		// public int compare(Entity o1, Entity o2){
		// if(o1.getModel().getMark() == o2.getModel().getMark())
		// return 0;
		// return o1.getModel().getMark() < o2.getModel().getMark() ? -1 : 1;
		// }
		// });

		for (int i = 0; i < subKatalog.size(); i++) {
			String temp = "";
			String cr = subKatalog.get(i).getModel().getMark();
			String ca = subKatalog.get(i).getModel().getReplaceModel();
			String cYear = subKatalog.get(i).getModel().getMinDate() + "-";
			// dodawaj tytu³y tylko dla znanych marek i modeli - reszte pomin
			if (cr != null && ca != null) {

				// dodaj date konca produkcji
				if (subKatalog.get(i).getModel().getMaxDate() != null)
					cYear += subKatalog.get(i).getModel().getMaxDate();
				else
					cYear += this.getProperties().getProperty("brakDatyKoncaProdukcji");

				if (pr == cr) {
					// ta sama marka
					temp = "; " + ca + " " + cYear;
				} else {
					// zmiana marki
					temp = cr + " " + ca + " " + cYear;
				}
				if (title.length() + temp.length() >= maxLength) {
					// za du¿o znakow
					if (!titles.contains(title))
						titles.add(title);
					title = cr + " " + ca + " " + cYear;
				} else {
					// mo¿na dodaæ kolejny model do tytulu
					if (!title.contains(temp.replace("; ", "")))
						title += " " + temp;
				}
				pr = cr;
			} else {
				// modul loguj¹cy pominiête marki oraz modele

			}
		}

		if (title.length() > 0 && !titles.contains(title))
			titles.add(title);

		return titles;
	}

	public String createResult(List<Entity> subKatalog, List<Entity> subZamienniki, List<Entity> subTytuly,
			int indexZastosowanie, int indexInfo, int indexKod) {
		String result = "";
		result += this.getBegin() + "\n";

		// zastosowanie
		result += this.getPrefixHeader();
		result += this.getKatalogNaglowki().get(indexZastosowanie) + ":";
		result += this.getSuffixHeader() + "\n";
		for (Entity e : subKatalog) {
			result += this.getPrefixValue();
			result += e.getValues().get(indexZastosowanie);
			result += this.getSuffixValue() + "\n";
		}

		String resultChecker = "";
		// wymiary tarcz - dopuszczony brak pola
		try {
			resultChecker += this.getPrefixHeader();
			resultChecker += this.getKatalogNaglowki().get(indexInfo);
			resultChecker += this.getSuffixHeader() + "\n";

			resultChecker += this.getPrefixValue();
			resultChecker += subKatalog.get(0).getValues().get(indexInfo);
			resultChecker += this.getSuffixValue() + "\n";
		} catch (Exception e) {
			errLog.info("pomiete wymiary tarcz dla " + subKatalog.get(0).getValues().get(indexKod));
			resultChecker = "";
		} finally {
			result += resultChecker;
		}

		// kod towaru
		result += this.getPrefixHeader();
		result += this.getKatalogNaglowki().get(indexKod);
		result += this.getSuffixHeader() + "\n";

		result += this.getPrefixValue();
		result += subKatalog.get(0).getValues().get(indexKod);
		result += this.getSuffixValue() + "\n";

		if (subZamienniki.size() > 0) {
			// zamienniki
			result += this.getPrefixHeader();
			result += "ZAMIENNIKI:";
			result += this.getSuffixHeader() + "\n";

			for (Entity e : subZamienniki) {
				result += this.getPrefixValue();
				result += e.getValues().get(1) + " - " + e.getValues().get(2);
				result += this.getSuffixValue() + "\n";
			}
		}

		result += this.getEnd();

		// miejsce na doda
		return result;
	}

	public void saveResult(String filePath, String kodTowaru, String result) {
		try (PrintStream out = new PrintStream(new FileOutputStream(filePath, true))) {
			out.println("\"" + kodTowaru + "\";\"" + result + "\"");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void saveResult(String filePath, String kodTowaru, String result, List<String> titles) {
		for (String title : titles) {
			if (title != null) {
				try (PrintStream out = new PrintStream(new FileOutputStream(filePath, true))) {
					out.println("\"" + kodTowaru + "\";\"" + title + "\";\"" + result + "\"");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void saveHeader(String filePath) {
		try (PrintStream out = new PrintStream(new FileOutputStream(filePath))) {
			out.println("\"KodTowaru\";\"TYTU£\";\"OPIS\"");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * metoda przyporzadkowujaca wybrane modele z zamknietej listy oraz logujaca
	 * braki
	 * 
	 * @param katalog
	 * @param modelRepository
	 */
	public void matchModelFromBase(List<Entity> katalog, int indexModels, ModelRepository modelRepository) {
		System.out.println("Wejscie w matchModel");
		System.out.println("wejœcie w metodê matchModel");
		System.out.println("W katalogu jest elementów : "+katalog.size());
		System.out.println("modeli mamy : "+modelRepository.getModels().size());
		System.out.println("MATCHOWANIE");
		for(String marka:modelRepository.getMarks())System.out.println("dostêpna marka : "+marka);
		for (Entity e : katalog) {
			String markPattern = "", modelPattern = "";
			//System.out.println("model : " + e.getValues().get(indexModels));
			List<Model> possibleModels = new ArrayList<Model>();
			for (String marka : modelRepository.getMarks()) {
				// System.out.println("Wyszykiwanie marki "+marka + "auto:" +
				// e.getValues().get(indexModels));
//				System.out.println("model : " + e.getValues().get(indexModels));
				if (e.getValues().get(indexModels).contains(marka)) {
					// znaleziono markê i przyporz¹dkowano
					e.getModel().setMark(marka);
					// System.out.println("Znaleziono markê "+marka+" dla
					// "+e.getValues().get(indexModels));
					// znaleziono mo¿liwe modele dla marki
					possibleModels = modelRepository.getModelsMap().get(marka);
					for (Model m : possibleModels) {
						// iteracja mozliwych modeli
						if (e.getValues().get(indexModels).contains(m.getModel())) {
							// znaleziono model i przyporzadkowano
							 System.out.println("Znaleziono model "+m.getModel()+" dla "+e.getValues().get(indexModels));
							e.getModel().setModel(m.getModel());
							markPattern = marka;
							modelPattern = m.getModel();
							break;
						}
					}
					break;
				}
			}
//			System.out.println("Marka : "+markPattern+", model : "+modelPattern);
			// wyodrebnienie z possibleModels modelu z mozliwymi pojemnosciami i silnikami
			boolean foundDescription = false;
			if (!possibleModels.isEmpty()) {
				List<Model> possibleEngines = modelRepository.getPossibleEngines(possibleModels, markPattern,
						modelPattern);
				if (!possibleEngines.isEmpty()) {
					// System.out.println("znaleziono "+possibleEngines.size()+" silnikow dla
					// "+markPattern +" " + modelPattern);
					for (Model m : possibleEngines) {
						if (e.getValues().get(indexModels).contains(m.getDescription())) {
							// znaleziono opis i ustawiono opis silnika
							e.getModel().setDescription(m.getDescription());
							// System.out.println("Znaleziono opis "+m.getDescription()+" dla
							// "+e.getValues().get(indexModels));
							foundDescription = true;
							break;
						}
					}
					// iteracja modeli tylko z opisami
					for (Model m : possibleEngines) {
						if (foundDescription) {
							// marka, model, opis silnika
							if (this.getProperties().getProperty("pojemnosc").contains("ON")) {

								if (e.getValues().get(indexModels).contains(m.getCapacity())) {
									// System.out.println("znaleziono pojemnoœæ");
									e.getModel().setCapacity(m.getCapacity());
									if (this.getProperties().getProperty("rocznik").contains("ON"))
										e.getModel().setMinDate(m.getMinDate());
									if (this.getProperties().getProperty("rocznik").contains("ON"))
										e.getModel().setMaxDate(m.getMaxDate());
									if (this.getProperties().getProperty("paliwo").contains("ON"))
										e.getModel().setFuel(m.getFuel());
									System.out.println(e.getModel().getMark() + " " + e.getModel().getModel() + " "
											+ e.getModel().getDescription() + " " + e.getModel().getCapacity() + " dla "
											+ e.getValues().get(indexModels));
								}
							}

						} else {
							// brak opisu silnika
							if (this.getProperties().getProperty("pojemnosc").contains("ON")) {

								if (e.getValues().get(indexModels).contains(m.getCapacity())) {
									// System.out.println("znaleziono pojemnoœæ");
									if (this.getProperties().getProperty("pojemnosc").contains("ON"))
										e.getModel().setCapacity(m.getCapacity());
									if (this.getProperties().getProperty("rocznik").contains("ON"))
										e.getModel().setMinDate(m.getMinDate());
									if (this.getProperties().getProperty("rocznik").contains("ON"))
										e.getModel().setMaxDate(m.getMaxDate());
									if (this.getProperties().getProperty("paliwo").contains("ON"))
										e.getModel().setFuel(m.getFuel());
//									System.out.println("bez DESC " + e.getModel().getMark() + " "
//											+ e.getModel().getModel() + " " + e.getModel().getDescription() + " "
//											+ e.getModel().getCapacity() + " dla " + e.getValues().get(indexModels));
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * nieu¿ywana metoda !!!!!!!!!!!!
	 * 
	 * @param katalog
	 * @param indexModels
	 * @param modelRepository
	 */
	public void matchModel(List<Entity> katalog, int indexModels, ModelRepository modelRepository) {

		System.out.println("wejœcie w metodê matchModel");
		System.out.println("W katalogu jest elementów : " + katalog.size());
		System.out.println("modeli mamy : " + modelRepository.getModels().size());
		System.out.println("MATCHOWANIE");
		for (Entity e : katalog) {
			// znalezienie marki samochodu
			for (String marka : modelRepository.getMarks()) {
				if (e.getValues().get(indexModels).contains(marka)) {
					// znaleziono dopasowanie marki
					e.getModel().setMark(marka);
					List<Model> possibleModels = modelRepository.getModelsMap().get(marka);
					for (Model m : possibleModels) {
						if (e.getValues().get(indexModels).contains(m.getFindModel())) {
							e.getModel().setFindModel(m.getFindModel());
							e.getModel().setReplaceModel(m.getReplaceModel());
							// zalozenie - model ma tylko jedna date produkcji
							e.getModel().setMinDate(m.getMinDate());
							e.getModel().setMaxDate(m.getMaxDate());
							foundModelsLog.info(foundLog(e, indexModels));
							break;
						}

					}
					if (e.getModel().getReplaceModel() == null) {
						// brak dopasowania modelu -> wstawienie elementu do missingModels
						putMissingModels(e, indexModels);
					}
					break;
				}
			}
			if (e.getModel().getMark() == null) {
				// brak dopasowania marki -> wstawienie elementu do missingModels
				putMissingModels(e, indexModels);
			}
		}

		// info do pliku o niedopasowanych modelach
		for (Map.Entry<String, Integer> entry : this.missingModels.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			missingModelsLog.info("model : " + key + ", liczba powtórzeñ w katalogu : " + value);
		}
	}

	public String foundLog(Entity e, int indexModels) {
		return "Marka=" + e.getModel().getMark() + ", model znaleziony=" + e.getModel().getFindModel()
				+ ", model zamieniony=" + e.getModel().getReplaceModel() + ",minRok=" + e.getModel().getMinDate()
				+ ", maxRok=" + e.getModel().getMaxDate() + ", model katalogowy=" + e.getValues().get(indexModels);
	}

	/**
	 * tworzy mapê brakujacych modeli (na potrzeby logów
	 * 
	 * @param e
	 * @param indexModels
	 */
	public void putMissingModels(Entity e, int indexModels) {
		// jesli wpis juz jest inkrementuje
		if (this.missingModels.containsKey(e.getValues().get(indexModels))) {
			this.missingModels.put(e.getValues().get(indexModels),
					this.missingModels.get(e.getValues().get(indexModels)) + 1);
		} else {
			// jesli nie ma wstawia z wartoscia 1
			this.missingModels.put(e.getValues().get(indexModels), 1);
		}
	}

	public void showCurrentTitleList(List<String> titles) {
		for (String s : titles) {
			titleLog.info("TYTU£ : " + s);
		}
	}

}