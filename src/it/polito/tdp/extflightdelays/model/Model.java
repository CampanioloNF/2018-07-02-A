package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

/*
 * Versione piu snella
 */

public class Model {

	private Graph <Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idAirportMap;
    private List<Rotta> rotte;
    private Map<Airport, List<Airport>> vicini;
    
    //parametri ricorsione  
    private Set<Airport> visitati ;
    /*
     * In questo caso è meglio utilizzare i nodi piuttosto che gli archi dal momento che il grafo
     * è aciclico e non orientato
     */
   
    private int max;
    private  List<Airport> result; 
   
    
	
	public Model() {
		
		this.dao = new ExtFlightDelaysDAO();
		
	}
	
	public void creaGrafo(int distanza) {
		
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.idAirportMap = new HashMap<Integer, Airport>();
		this.vicini = new HashMap<>();
		this.rotte = new ArrayList<>();
	
		//aggiungo simultaneamente archi e vertici con un metodo dao 
		//carico la mappa
		
		dao.loadGraph(idAirportMap,rotte,grafo,distanza);	
		
		//mappo i vicini
		for(Airport a : grafo.vertexSet()) {
			vicini.put(a, Graphs.neighborListOf(grafo, a));
		}
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
		
		//forse tale metodo può essere sostituito
		
		List<Rotta> result = new LinkedList<>();
		for(Rotta rotta: rotte) {
			if(rotta.getOrigine().equals(cerca) || rotta.getDestinazione().equals(cerca))
				 result.add(rotta);
		}
		
		Collections.sort(result);
		
		  return result;
	}
	
	
	public List<Airport> cercaCammino(Airport partenza, int max){
		
	/*
	 * Tale metodo ricorsivo deve andare a creare una lista di Airport.
	 * Possiamo creare un oggetto che chiamiamo Path.
	 * 
	 * Tale oggetto sarà la soluzione parziale dove si andrà riempiendo con la lista di Airport 
	 * Possiamo inoltre dotarlo di un metodo che ci dica quanto è pesante tale cammino in termini di miglia
	 * Tale metodo potrebbe interagire con le rotte, presenti nel model e ricavare dunque il punteggio
	 * 
	 */	
	
	 this.max = max;
	 
	 
	 //Creiamo il parziale
	  
	    
	    List<Airport> parziale = new ArrayList<>();
	    parziale.add(partenza);
	    
	    result = new ArrayList<>(parziale);
	    
	    //all'inizio non ho visitato nessun aereoporto
	    visitati = new HashSet<>();
	    
	    cerca(parziale);
		
		return result;
	}

	
	
	private void cerca(List<Airport> parziale) {

		//il concetto di base è che quando si toglie la partenza ho concluso perchè ho provato tutto
		while(!parziale.isEmpty()) {
		
		//Arrivo in un nuovo aereoporto (mai visitato prima (?) ) e mi chiedo dove possa andare
		
	
		while(visitaInProfondita(visitati,parziale));
		
		//ritorna false se l'aereoporta sopra considerato è stato completamente esplorato!
		//in tal caso è necessario toglierlo dalla dal parziale e re-iterare
		
		if(parziale.size()>result.size()) {
			result = new ArrayList<>(parziale);
		}
		
	    Airport visitato = parziale.get(parziale.size()-1);
		
		parziale.remove(visitato);
		visitati.add(visitato);    
		//siamo tornati indietro di un aereoporto
		
		//aggiungo ai visitati solo quelli che rimuovo e da cui dunque non devo più passare
		// dal momento che esploro in profondità
		
		}
		
	} 
      			
	// metodo a cui passo il parziale e una lista di visitati e mi dice dove posso ancora visitare
	
	private boolean visitaInProfondita(Set<Airport> visitati, List<Airport> parziale ) {
		
		
		
		//chiamo questo metodo per vedere tra tutti i vicini quali sia quello dove posso andare 
		Airport air = parziale.get(parziale.size()-1);
		
		//Questo è il metod di visita in profondita
		
		for(Airport vicino : vicini.get(air)) {
			
			//non posso tornare indietro e se ho già visitato un vicino non torno a visitarlo
	           
			      if(!parziale.contains(vicino) && !visitati.contains(vicino)) {
	        	   
	        	  
	        	   // valuto se posso realmente andare a visitare il vicino senza sforare con le miglia
	        	   if(calcolaPesoCon(parziale,vicino)<=max) {
	        	  
	        		//se posso visitare il vicino lo aggiungo   
	                parziale.add(vicino);
	        	   
	        	   // vado in profondità
	                visitaInProfondita(visitati, parziale);
	        	   
	        	 }
	        	   
	        	   //se non posso andare avanti in profondità cerco un altro vicino attraverso il quale possa andare in profondità
	        	   
	           }
	         }
		
		//se non ho più vicini disponibili devo tornare indietro..
		
		return false;
		
	}
	

	private double calcolaPesoCon(List<Airport> parziale, Airport vicino) {
		
		
		if(parziale.size()==1)
			return 0.0;
		
		List<Airport> cammino = new ArrayList<>(parziale);
		cammino.add(vicino);
		
		
		return this.calcolaPeso(cammino);
	}

	public List<Rotta> getRotte() {
		return rotte;
	}
 
	public double calcolaPeso(List<Airport> cammino) {
		
		double peso = 0.0;
		
		for(int i= 0; i<cammino.size()-1; i++) {
			
			Rotta r = null;
		    for(Rotta rot : rotte) {
		    	if(rot.getDestinazione().equals(cammino.get(i)) && rot.getOrigine().equals(cammino.get(i+1))) {
		    		peso+=rot.getAvg();
		    		break;
		    	}
		    	if(rot.getOrigine().equals(cammino.get(i)) && rot.getDestinazione().equals(cammino.get(i+1))) {
		    		peso+=rot.getAvg();
		    		break;	
		    	}
		    }
		}
		
		return peso;
	}

	
	
		
	
}
