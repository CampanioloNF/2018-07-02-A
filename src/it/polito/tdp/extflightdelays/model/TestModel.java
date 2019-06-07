package it.polito.tdp.extflightdelays.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();

		model.creaGrafo(500);
		Airport a = model.getListAirport().get(1);
		
//		for(Rotta rotta : model.getRotte())
//	     	System.out.println(rotta.getOrigine()+"            "+rotta.getDestinazione()+"             "+rotta.getAvg()+"\n");
//	     	
		    Path cammino = model.cercaCammino(a, 100000);
		  
		    System.out.println(""+cammino.getSize()+"\n");
		    
		    for(Airport a1 : cammino.getCammino()) {
		    	System.out.println(a1);
		    }
	}

}
