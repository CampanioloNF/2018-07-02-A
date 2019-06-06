package it.polito.tdp.extflightdelays.model;

public class Rotta implements Comparable<Rotta> {

	private Airport origine;
	private Airport destinazione;
	private double avg;
	
	
	
	public Rotta(Airport origine, Airport destinazione, double avg) {
	
		this.origine = origine;
		this.destinazione = destinazione;
		this.avg = avg;
	}



	public double getAvg() {
		return avg;
	}



	public void setAvg(double avg) {
		this.avg = avg;
	}



	public Airport getOrigine() {
		return origine;
	}



	public Airport getDestinazione() {
		return destinazione;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destinazione == null) ? 0 : destinazione.hashCode());
		result = prime * result + ((origine == null) ? 0 : origine.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rotta other = (Rotta) obj;
		if (destinazione == null) {
			if (other.destinazione != null)
				return false;
		} else if (!destinazione.equals(other.destinazione))
			return false;
		if (origine == null) {
			if (other.origine != null)
				return false;
		} else if (!origine.equals(other.origine))
			return false;
		return true;
	}



	@Override
	public int compareTo(Rotta arg0) {
		// TODO Auto-generated method stub
		return (int) (arg0.avg-this.avg);
	}
	
	
}
