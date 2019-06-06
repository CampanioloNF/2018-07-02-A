package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private Graph <Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idAirportMap;
    private List<Rotta> rotte;
    
    //parametri ricorsione
   
    private int max;
    private  Path result; 
    
	
	public Model() {
		
		this.dao = new ExtFlightDelaysDAO();
		
	}
	
	public void creaGrafo(int distanza) {
		
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.idAirportMap = new HashMap<Integer, Airport>();
		this.rotte = new ArrayList<>();
	
		//aggiungo simultaneamente archi e vertici con un metodo dao 
		//carico la mappa
		
		dao.loadGraph(idAirportMap,rotte,grafo,distanza);	
		
	}

	public List<Airport> getListAirport(){
	
	 List<Airport> aereoporti = new LinkedList<>();	
		
		if(grafo!=null) {
			
		  aereoporti.addAll(grafo.vertexSet());
		  Collections.sort(aereoporti);
			
		}
		
	  return aereoporti;
	}
	
	public List<Rotta> getViciniPerDistanza(Airport cerca){
		
		
		List<Rotta> result = new LinkedList<>();
		for(Rotta rotta: rotte) {
			if(rotta.getOrigine().equals(cerca) || rotta.getDestinazione().equals(cerca))
				 result.add(rotta);
		}
		
		Collections.sort(result);
		
		
		
		  return result;
	}
	
	
	public Path cercaCammino(Airport partenza, int max){
		
	/*
	 * Tale metodo ricorsivo deve andare a creare una lista di Airport.
	 * Possiamo creare un oggetto che chiamiamo Path.
	 * 
	 * Tale oggetto sarà la soluzione parziale che si andrà riempiendo con la lista di Airport 
	 * Possiamo inoltre dotarlo di un metodo che ci dica quanto è pesante tale cammino in termini di miglia
	 * Tale metodo potrebbe interagire con le rotte, presenti nel model e ricavare dunque il punteggio
	 * 
	 */

		
	
	 this.max = max;
	 
      
	;
	 
	 //Creiamo il parziale
	  
	    
	    Path parziale = new Path(this, partenza);
	    
	    result = parziale;
	    
	    cerca(parziale);
		
		
		
		return result;
	}

	
	private void cerca(Path parziale) {

		
		//condizione di terminazione
		if(parziale.getPeso()>max) {
			
			//il parziale che ci interessa
			parziale.removeLast();
			if(parziale.getSize()>result.getSize()) 
				result = parziale;
			
			return;
		}
		
		//Considero l'aereoporto dove sono
		Airport aereoporto = parziale.getLast();
		boolean over = true;
		
		//cerco tra tutti i vicini
		for(Airport vicino : cercaVicini(aereoporto)) {
			
	           if(!parziale.contains(vicino)) {
	        	   
	        	   over=false;
	        	   //se il parziale non contiene il vicino vado la 
	        	   
	        	   parziale.add(vicino);
	        	   
	        	   //se non ho superato il limite continuo
	        	   cerca(parziale);
	        	   
	        	   //torno indietro
	        	   parziale.removeLast();
	        	  
	           }
	         }
		
		if(over) {
			if(parziale.getSize()>result.getSize()) 
				result = parziale;			
			return;
		}
			
	}
		
	

	public double calcolaPeso(List<Airport> cammino) {
		
		double peso = 0.0;
		
		for(int i= 0; i<cammino.size()-1; i++) {
			
			peso+=pesoRotta(cammino.get(i), cammino.get(i+1));
		}
		
		return peso;
	}

	private double pesoRotta(Airport a1, Airport a2) {

		for(Rotta rotta : rotte) {
			if(rotta.getOrigine().equals(a1) && rotta.getDestinazione().equals(a2))
				return rotta.getAvg();
			else if(rotta.getOrigine().equals(a2) && rotta.getDestinazione().equals(a1))
				return rotta.getAvg();
		}

		return 0;
	}
	
	private List<Airport> cercaVicini(Airport a){
		
		return Graphs.neighborListOf(grafo, a);
	}
}
