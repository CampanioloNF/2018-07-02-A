package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;
import it.polito.tdp.extflightdelays.model.Rotta;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void loadGraph(Map<Integer, Airport> idAirportMap, List<Rotta> rotte, Graph<Airport, DefaultWeightedEdge> grafo, int distanza) {
		
		String sql = "SELECT f.ORIGIN_AIRPORT_ID, a1.IATA_CODE, a1.AIRPORT, a1.CITY, a1.STATE, a1.COUNTRY, a1.LATITUDE, a1.LONGITUDE, a1.TIMEZONE_OFFSET, f.DESTINATION_AIRPORT_ID, a2.IATA_CODE, a2.AIRPORT, a2.CITY, a2.STATE, a2.COUNTRY, a2.LATITUDE, a2.LONGITUDE, a2.TIMEZONE_OFFSET, AVG(DISTANCE) as distanzaMedia " + 
				"FROM flights f, airports a1, airports a2 " + 
				"WHERE f.ORIGIN_AIRPORT_ID = a1.ID AND f.DESTINATION_AIRPORT_ID = a2.ID " + 
				"GROUP BY f.ORIGIN_AIRPORT_ID, f.DESTINATION_AIRPORT_ID " + 
				"HAVING distanzaMedia >= ? ";
		

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, distanza);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				
				//creo il riferimento ai due aereoporti
				
				Airport origine = new Airport(rs.getInt("f.ORIGIN_AIRPORT_ID"), rs.getString("a1.IATA_CODE"), rs.getString("a1.AIRPORT"),
						rs.getString("a1.CITY"), rs.getString("a1.STATE"), rs.getString("a1.COUNTRY"), rs.getDouble("a1.LATITUDE"),
						rs.getDouble("a1.LONGITUDE"), rs.getDouble("a1.TIMEZONE_OFFSET"));

				Airport destinazione = new Airport(rs.getInt("f.DESTINATION_AIRPORT_ID"), rs.getString("a2.IATA_CODE"), rs.getString("a2.AIRPORT"),
						rs.getString("a2.CITY"), rs.getString("a2.STATE"), rs.getString("a2.COUNTRY"), rs.getDouble("a2.LATITUDE"),
						rs.getDouble("a2.LONGITUDE"), rs.getDouble("a2.TIMEZONE_OFFSET"));
				
				//se non sono presenti li aggiungo alla mappa 
				
				if(!idAirportMap.containsKey(origine.getId())) {
					
					idAirportMap.put(origine.getId(), origine);
					grafo.addVertex(origine);
					
				}
				if(!idAirportMap.containsKey(destinazione.getId())) {
					
					idAirportMap.put(destinazione.getId(), destinazione);
					grafo.addVertex(destinazione);
				}
				
				
				
				
				//ora devo aggiungere i vertici e gli archi considerando B-> A e A->B
				DefaultWeightedEdge edge = grafo.getEdge(destinazione, origine);
				
				if(edge!=null) {
					
					//devo modificarne il peso
					double peso = grafo.getEdgeWeight(edge);
					double newPeso = (peso + rs.getDouble("distanzaMedia"))/2;
					grafo.setEdgeWeight(edge, newPeso);
					
					for(Rotta r : rotte) {
						if(r.getDestinazione().equals(origine) && r.getOrigine().equals(destinazione))
							r.setAvg(newPeso);
					}
				
					
					
				}
				
				else {
					
					//altrimenti lo aggiungo e basta 
					
					Graphs.addEdge(grafo, origine, destinazione,rs.getDouble("distanzaMedia") );
					rotte.add(new Rotta(origine, destinazione, rs.getDouble("distanzaMedia")));
					
				}
					
				 
			}

			conn.close();
			

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			
		}
		
	}
}
