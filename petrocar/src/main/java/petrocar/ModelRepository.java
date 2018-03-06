package petrocar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class ModelRepository {
	public List<Model> models = new ArrayList<Model>();
	public Set<String> marks = new HashSet<String>();
	public Map<String, List<Model>> modelsMap = new HashMap<String, List<Model>>();
	public Logger logger = Logger.getLogger("modelRepository");
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}
	public Set<String> getMarks() {
		return marks;
	}
	public void setMarks(Set<String> marks) {
		this.marks = marks;
	}
	
	
	public Map<String, List<Model>> getModelsMap() {
		return modelsMap;
	}
	public void setModelsMap(Map<String, List<Model>> modelsMap) {
		this.modelsMap = modelsMap;
	}
	/**
	 * konstruktor wczytuj¹cy dane z pliku z podanymi modelami
	 */
	public ModelRepository(String filePath, String separator) {
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			// celowe pominiecie pierwszego rekordu bedacego naglowkiem!
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				List<String> list = new ArrayList();
				String[] fields = line.split(separator);
				//model bez lat produkcji
				if(fields.length==7) {
					this.getMarks().add(fields[0].replaceAll("\"", ""));
					this.getModels().add(new Model(fields[0].replaceAll("\"", ""), fields[1].replaceAll("\"", ""), fields[2].replaceAll("\"", ""), fields[3].replaceAll("\"", ""), fields[4].replaceAll("\"", ""), fields[5].replaceAll("\"", ""), fields[6].replaceAll("\"", "")));					
				}
				else if(fields.length==6) {
					this.getMarks().add(fields[0]);
					this.getModels().add(new Model(fields[0].replaceAll("\"", ""), fields[1].replaceAll("\"", ""), fields[2].replaceAll("\"", ""), fields[3].replaceAll("\"", ""), fields[4].replaceAll("\"", ""), fields[5].replaceAll("\"", "")));
				}
				else if(fields.length==3) {
					this.getMarks().add(fields[0].replaceAll("\"", ""));
					Model model = new Model(fields[0].replaceAll("\"", ""), fields[1].replaceAll("\"", ""), fields[2].replaceAll("\"", ""));
					System.out.println("dodano model  : "+model.toString());
					this.getModels().add(model);
					// model z latami produkcji
				} else if(fields.length==4) {
					this.getMarks().add(fields[0]);
					Model model = new Model(fields[0].replaceAll("\"", ""), fields[1].replaceAll("\"", ""), fields[2].replaceAll("\"", ""), fields[3].replaceAll("\"", ""));
					this.getModels().add(model);
				} else {
					logger.info("b³¹d mapowania modelu : "+ line);
				}
			}

		} catch (FileNotFoundException e) {
			logger.error("Nieudana proba wczytania wartosci z pliku " + filePath, e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Nieudana proba wczytania wartosci z pliku " + filePath, e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.modelsMap = fillModelMap(this.getMarks(), this.getModels());
	
	}
	/**
	 * metoda zwraca listê modeli wybranej marki - uzupelnia modelsMap
	 * @param model
	 * @return
	 */
	public List<Model> getSubList(String model){
		List<Model> subList = new ArrayList<Model>();
		for(Model m:this.models) {
			if(m.getMark().contains(model)) subList.add(m);		
		}
		return subList;
	}
	/**
	 * metoda zwracajaca wszystkie znane typy silnikow dla danej marki oraz modelu
	 * @param possibleModels
	 * @param mark
	 * @param model
	 * @return
	 */
	public List<Model> getPossibleEngines(List<Model> possibleModels, String mark, String model){
		List<Model> subList = new ArrayList<Model>();
		for(Model m:possibleModels) {
			if(m.getMark().contains(mark) && m.getModel().contains(model)) subList.add(m);
		}
		return subList;
	}
	
	public Map<String, List<Model>> fillModelMap(Set<String> marks, List<Model> models){
		Map<String, List<Model>> map = new HashMap<String, List<Model>>();
		for(String s: marks) {
			map.put(s, getSubList(s));
		}
		return map;
	}
	
	

}
