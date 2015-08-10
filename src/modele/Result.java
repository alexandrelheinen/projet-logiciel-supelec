package modele;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Result implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private double time;
	private String date;
	
	public Result (String name, double time) {
		this.name = name;
		this.time = time;
		DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date date = new Date();  
        this.date = format.format(date);
	}
	
	public String getName() {
		return name; }
	
	public double getTime() {
		return time; }
	
	public String getDate() {
		return date; }
}
