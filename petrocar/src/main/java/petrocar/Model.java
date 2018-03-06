package petrocar;

import org.joda.time.*;
import org.joda.time.format.*;

public class Model {
	public String mark;
	public String findModel;
	public String replaceModel;
	public String years;
	public String minDate;
	public String maxDate;
	public LocalDate date;
	
	
	public String model;
	public String description;
	public String capacity;
	public String fuel;
	
	
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getFindModel() {
		return findModel;
	}
	public void setFindModel(String findModel) {
		this.findModel = findModel;
	}
	public String getReplaceModel() {
		return replaceModel;
	}
	public void setReplaceModel(String replaceModel) {
		this.replaceModel = replaceModel;
	}
	public String getYears() {
		return years;
	}
	public void setYears(String years) {
		this.years = years;
	}
		
	public String getMinDate() {
		return minDate;
	}
	public void setMinDate(String minDate) {
		this.minDate = minDate;
	}
	public String getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}
	
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCapacity() {
		return capacity;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	public String getFuel() {
		return fuel;
	}
	public void setFuel(String fuel) {
		this.fuel = fuel;
	}
	public Model() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Model(String mark, String findModel, String replaceModel, String years) {
		super();
		this.mark = mark;
		this.findModel = findModel;
		this.replaceModel = replaceModel;
		//podzia³ dat oraz wyciagniecie dwóch cyfr
		String[] dates=years.split(" - ");
		if(dates.length==2) {
			this.setMinDate(getOnlyYear(dates[0],"yyyy-MM-dd"));
			this.setMaxDate(getOnlyYear(dates[1],"yyyy-MM-dd"));
		}else {
			this.setMinDate(getOnlyYear(years.replace(" - ", "").trim(),"yyyy-MM-dd"));
		}
		this.years = years;
	}
	
	public Model(String mark, String model, String description, String minDate, String maxDate, String capacity,
			String fuel) {
		super();
		this.mark = mark;
		this.minDate = minDate.substring(Math.max(minDate.length() - 2, 0));
		this.maxDate = maxDate.substring(Math.max(maxDate.length() - 2, 0));
		this.model = model;
		this.description = description;
		this.capacity = capacity.replace("l", "");
		this.fuel = fuel;
	}
	public Model(String mark, String model, String description, String minDate, String maxDate, String capacity) {
		super();
		this.mark = mark;
		this.minDate = minDate.substring(Math.max(minDate.length() - 2, 0));
		this.maxDate = maxDate.substring(Math.max(maxDate.length() - 2, 0));
		this.model = model;
		this.description = description;
		this.capacity = capacity.replace("l", "");
	}
	/*
	 * metoda wybiera tylko rok
	 */
	public String getOnlyYear(String date, String pattern) {
		String year;
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
		LocalDate localDate = formatter.parseLocalDate(date);
		year = localDate.yearOfCentury().getAsText();
		if(year.length()==1) year = "0"+year;
		return year;
	}
	
	
	public Model(String mark, String model, String description) {
		super();
		this.mark = mark;
		this.model = model;
		this.description = description;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((findModel == null) ? 0 : findModel.hashCode());
		result = prime * result + ((mark == null) ? 0 : mark.hashCode());
		result = prime * result + ((replaceModel == null) ? 0 : replaceModel.hashCode());
		result = prime * result + ((years == null) ? 0 : years.hashCode());
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
		Model other = (Model) obj;
		if (findModel == null) {
			if (other.findModel != null)
				return false;
		} else if (!findModel.equals(other.findModel))
			return false;
		if (mark == null) {
			if (other.mark != null)
				return false;
		} else if (!mark.equals(other.mark))
			return false;
		if (replaceModel == null) {
			if (other.replaceModel != null)
				return false;
		} else if (!replaceModel.equals(other.replaceModel))
			return false;
		if (years == null) {
			if (other.years != null)
				return false;
		} else if (!years.equals(other.years))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Model [mark=" + mark + ", minDate=" + minDate + ", maxDate=" + maxDate + ", date=" + date + ", model="
				+ model + ", description=" + description + ", capacity=" + capacity + ", fuel=" + fuel + "]";
	}
	
	
	

}
