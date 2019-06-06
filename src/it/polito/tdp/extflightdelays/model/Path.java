package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.List;

public class Path {
	
	
	//Un cammino è caratterizzato da una lista di Airport e da un peso
	private List<Airport> cammino;
	private double peso;
	//vi è inoltre un riferimento ai metodi del modello
	private Model model;
	
	public Path( Path other) {
		
		this.peso = other.peso;
		this.model = other.model;
		this.cammino = new ArrayList<>(other.cammino);
		
	}
	
	public Path(Model model, Airport partenza) {
		
		this.model = model;
		this.peso = 0;
		this.cammino = new ArrayList<>();
		this.cammino.add(partenza);
	}

	public List<Airport> getCammino() {
		return cammino;
	}

	public double getPeso() {
		
	      this.peso = model.calcolaPeso(cammino);
		
		return peso;
	}

	//vi sarà un metodo per riconoscere il peso del Path
	
	//metodi per aggiungere Airport al cammino
	
	public void add(Airport a ) {
		this.cammino.add(a);
	}
	
	public void remove(Airport a) {
		this.cammino.remove(a);
	}
	
	public void removeLast() {
		this.cammino.remove(cammino.size()-1);
		
	}

	public boolean contains(Airport altro) {
		
		for(Airport a : cammino ) {
			
			if(a.equals(altro))
				return true;
			
		}
			
		return false;
		
	}
	
	public Airport getLast() {
		return cammino.get(cammino.size()-1);
	}

	public int getSize() {
		
		return cammino.size();
	}

	
}
