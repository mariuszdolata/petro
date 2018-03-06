package petrocar;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa przechowujaca pojedyncza encje dla
 * -katalogu
 * -indeksu czesci zamiennych
 * -tytulow
 * @author mariusz
 *
 */
public class Entity {
	public String index;
	public List<String> values = new ArrayList<String>();
	//pole przechowujace przyporzadkowany model
	public Model model = new Model();
	
	
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Entity(String index, List<String> values) {
		super();
		this.index = index;
		this.values = values;
	}
	
	@Override
	public String toString() {
		String string ="";
		for(String s: this.values) {
			string+=s+";";
		}
		return string;
	}
	/**
	 * Porownanie tylko na podstawie indexu
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		return true;
	}
	
	public int compareTo(Entity e) {
		return(Integer.parseInt(this.getModel().getMark())-Integer.parseInt(e.getModel().getMark()));
	}
	
	
}
