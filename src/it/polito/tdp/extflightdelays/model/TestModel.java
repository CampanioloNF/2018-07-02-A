package it.polito.tdp.extflightdelays.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();

		model.creaGrafo(900);
		Airport a = model.getListAirport().get(1);
		
		model.cercaCammino(a, 1000);
	}

}
