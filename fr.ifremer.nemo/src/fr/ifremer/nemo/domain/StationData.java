package fr.ifremer.nemo.domain;

public class StationData implements Comparable {
	private Double _value;

	private int _index;

	public StationData() {
		_value = 0.0;
		_index = 0;
	}

	public StationData(Double value, int index, String stationRef) {
		_value = value;
		_index = index;
	}

	public int getIndex() {
		return _index;
	}

	public void setIndex(int index) {
		this._index = index;
	}

	public Double getValue() {
		return _value;
	}

	public void setValue(Double value) {
		this._value = value;
	}

	public int compareTo(Object other) {
		Double nombre1 = ((StationData) other).getValue();
		Double nombre2 = this.getValue();
		if (nombre1 > nombre2) {
			return -1;
		} else if (nombre1 == nombre2) {
			return 0;
		} else {
			return 1;
		}
	}
}
