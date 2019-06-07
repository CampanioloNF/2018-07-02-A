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

public class Model {

	private Graph <Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idAirportMap;
    private List<Rotta> rotte;
    private Map<Airport, List<Airport>> vicini;
    
    //parametri ricorsione
   
    private int max;
    private  Path result; 
    private Set<Airport> visitati ; //serve per tenere traccia degli aereoporti che tolgo dal parziale (gia considerati)
    
	
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
	
	
	public Path cercaCammino(Airport partenza, int max){
		
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
	  
	    
	    Path parziale = new Path(this, partenza);
	    
	    result = new Path(parziale);
	    visitati = new HashSet<>();
	    
	    cerca(parziale);
		
		
		
		return result;
	}

	
	
	private void cerca(Path parziale) {

		//il concetto di base è che quando si toglie la partenza ho concluso perchè ho provato tutto
		while(!parziale.isEmpty()) {
		
		//Arrivo in un nuovo aereoporto (mai visitato prima (?) ) e mi chiedo dove possa andare
		Airport aereoporto = parziale.getLast();
	
		while(visitaInProfondita(visitati, parziale));
		
		//ritorna false se l'aereoporta sopra considerato è stato completamente esplorato!
		//in tal caso è necessario toglierlo dalla dal parziale e re-iterare
		
		if(parziale.getSize()>result.getSize()) {
			result = new Path(parziale);
			System.out.format("%.2f max : %d\n",  parziale.getPeso(), max);
		}
		parziale.remove(aereoporto);
		//siamo tornati indietro di un aereoporto
		
		//aggiungo ai visitati solo quelli che rimuovo e da cui dunque non devo più passare
		// dal momento che esploro in profondità
		
		visitati.add(aereoporto);
		
		}
		
	} 
      			
	// metodo a cui passo il parziale e una lista di visitati e mi dice dove posso ancora visitare
	
	private boolean visitaInProfondita(Set<Airport> visitati, Path parziale ) {
		
		
		
		//chiamo questo metodo per vedere tra tutti i vicini quali sia quello dove posso andare 
		
		//Questo è il metod di visita in profondita
		
		for(Airport vicino : vicini.get(parziale.getLast())) {
			
			
			//non posso tornare indietro e se ho già visitato un vicino non torno a visitarlo
	           
			      if(!parziale.contains(vicino) && !visitati.contains(vicino)) {
	        	   
	        	  
	        	   // valuto se posso realmente andare a visitare il vicino senza sforare con le miglia
	        	   if(calcolaPesoCon(parziale,vicino)<max) {
	        	  
	        		//se posso visitare il vicino lo aggiungo   
	                parziale.add(vicino);
	        	        
	        	   // vado in profondità
	        	   cerca(parziale);
	        	   
	        	 }
	        	   
	        	   //se non posso andare avanti in profondità cerco un altro vicino attraverso il quale possa andare in profondità
	        	   
	           }
	         }
		
		//se non ho più vicini disponibili devo tornare indietro..
		
		return false;
		
	}
	

	private double calcolaPesoCon(Path parziale, Airport vicino) {
		
		
		if(parziale.getSize()==1)
			return 0.0;
		
		List<Airport> cammino = new ArrayList<>(parziale.getCammino());
		cammino.add(vicino);
		
		
		return this.calcolaPeso(cammino);
	}

	public List<Rotta> getRotte() {
		return rotte;
	}

	public double calcolaPeso(List<Airport> cammino) {
		
		double peso = 0.0;
		
		for(int i= 0; i<cammino.size()-1; i++) {
			
			peso+=pesoRotta(cammino.get(i), cammino.get(i+1));
		}
		
		return peso;
	}

	//dati due aereporti mi ricavo il peso dell'arco fra essi
	private double pesoRotta(Airport a1, Airport a2) {

		DefaultWeightedEdge dwe1 = grafo.getEdge(a1, a2);
		DefaultWeightedEdge dwe2 = grafo.getEdge(a2, a1);
		
		if(dwe1==null)
		     return grafo.getEdgeWeight(dwe2);
		  return grafo.getEdgeWeight(dwe1);

	}
	
	
		
	
}
