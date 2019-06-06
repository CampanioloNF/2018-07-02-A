

/**
 * Sample Skeleton for 'ExtFlightDelays.fxml' Controller Class
 */

package it.polito.tdp.extflightdelays;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Model;
import it.polito.tdp.extflightdelays.model.Rotta;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ExtFlightDelaysController {

	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML // fx:id="distanzaMinima"
    private TextField distanzaMinima; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalizza"
    private Button btnAnalizza; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBoxAeroportoPartenza"
    private ComboBox<Airport> cmbBoxAeroportoPartenza; // Value injected by FXMLLoader

    @FXML // fx:id="btnAeroportiConnessi"
    private Button btnAeroportiConnessi; // Value injected by FXMLLoader

    @FXML // fx:id="numeroVoliTxtInput"
    private TextField numeroVoliTxtInput; // Value injected by FXMLLoader

    @FXML // fx:id="btnCercaItinerario"
    private Button btnCercaItinerario; // Value injected by FXMLLoader

    @FXML
    void doAnalizzaAeroporti(ActionEvent event) {

    	  cmbBoxAeroportoPartenza.getItems().clear();
    	
          String input = distanzaMinima.getText();
          if(input!=null) {
        	  
        	  try {
        		  
        		  model.creaGrafo(Integer.parseInt(input));
        		  this.cmbBoxAeroportoPartenza.getItems().addAll(model.getListAirport());
        		  
        	  }catch(NumberFormatException nfe) {
        		  txtResult.appendText("Si prega di inserire una distanza minima (numero intero), grazie");
        		  return;
        	  }
        	  
        	  
          }else {
        	  txtResult.appendText("Si prega di inserire una distanza minima (numero intero), grazie");
          }
    }

    @FXML
    void doCalcolaAeroportiConnessi(ActionEvent event) {

    	txtResult.clear();
    	
    	Airport input = cmbBoxAeroportoPartenza.getValue();
    	
    	if(input!=null) {
    		
    		for(Rotta ap : model.getViciniPerDistanza(input)) {
    			if(ap.getDestinazione().equals(input))
    				txtResult.appendText(ap.getOrigine() + "  " + "( "+(double)(Math.round(ap.getAvg()*100))/100+" )\n" );
    			else
    				txtResult.appendText(ap.getDestinazione() + "  " + "( "+(double)(Math.round(ap.getAvg()*100))/100+" )\n" );
    		}
    			
    		
    	}else
    		  txtResult.appendText("Si prega di inserire un aereoporto, grazie");
    	
    }

    @FXML
    void doCercaItinerario(ActionEvent event) {
    	
    	txtResult.clear();
    	
    	String input = numeroVoliTxtInput.getText();
    	Airport airport = cmbBoxAeroportoPartenza.getValue();
    	
    	 if(input!=null && airport!=null ) {
    		 
       	  
       	  try {
       		  
       		Map<List<Airport>, Double> result = model.cercaCammino(airport, Integer.parseInt(input));
       		
       		for(Entry<List<Airport>, Double> entry : result.entrySet()) {
       			
       			    txtResult.appendText("Il cammino prevede i seguenti Airport ("+entry.getKey().size()+"): \n");
       			   
       			for(Airport a : entry.getKey()) {
       				txtResult.appendText(a+"\n");
       			}
       			
       			    txtResult.appendText("Il cammino totale è lungo: "+entry.getValue()+" miglia");
       			
       		}
       		
       		  
       	  }catch(NumberFormatException nfe) {
       		  txtResult.appendText("Si prega di inserire una distanza massima (numero intero) e selezionare un aereoporto, grazie");
       		  return;
       	  }
       	  
       	  
         }else {
       	  txtResult.appendText("Si prega di inserire una distanza massima (numero intero) e selezionare un aereoporto, grazie");
         }

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert distanzaMinima != null : "fx:id=\"distanzaMinima\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnAeroportiConnessi != null : "fx:id=\"btnAeroportiConnessi\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert numeroVoliTxtInput != null : "fx:id=\"numeroVoliTxtInput\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnCercaItinerario != null : "fx:id=\"btnCercaItinerario\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";

    }
    
    public void setModel(Model model) {
		this.model = model;
		
	}
}

