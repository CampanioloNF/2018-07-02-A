package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private Graph <Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idAirportMap;
    private List<Rotta> rotte;
	
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
	
	/*
	 * Da rifare .. non è vero che passa il maggior numero di citta
	 *   
	 *   Credo sia oportuno un algoritmo ricorsivo ad HOC oppure uno degli algoritmi 'speciali'
	 * 
	 */
	
	public Map<List<Airport>, Double> cercaCammino(Airport partenza, int max){
		
		DijkstraShortestPath<Airport, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(grafo);
		
		Map<List<Airport>, Double> result = new HashMap<>();
		int best = 0;
		
		for(Airport a : grafo.vertexSet()) {
			
			//per ogni aereoporto 
			
			if(!a.equals(partenza)) {
				
				//diverso da quello di partenza
				
				//cerco un cammino minimo
			  GraphPath<Airport, DefaultWeightedEdge> cammino = dijkstra.getPath(partenza, a);
			    // e ne salvo il peso
			  double pesoCammino = dijkstra.getPathWeight(partenza, a);
			  List<Airport> aCamm = cammino.getVertexList();
			  
			  //se il peso è minore della condizione iniziale
			  if(pesoCammino<max) {
				  
				  //se il cammino è più lungo dei precedenti
				  if(aCamm.size()>best) {
					  //lo salvo
					 if(!result.isEmpty())
					     result.clear();
				      result.put(aCamm, pesoCammino);
				      best = aCamm.size();
				  }
			  }
			  
			}
		}
		   
		
		
		return result;
		
	}
	
}
