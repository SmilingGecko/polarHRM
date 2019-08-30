package org.uosSport.polarheartmonitor;

import java.util.Observable;


import com.androidplot.xy.SimpleXYSeries;

public class DataHandler extends Observable{
	private static DataHandler dd = new DataHandler();

	//DATA FOR SAVING
	boolean newValue = true;
	SimpleXYSeries series1;
	SimpleXYSeries series2;

	ConnectThread reader;
	H7ConnectThread H7;

	//int pos=0;
	float pos=0;

	//int val=0;
	float val=0;

	//int min=0;
	float min =0;

	//int max=0;
	float max=0;
	//for the average maths
	//int data=0;
	float data=0;

	//int total=0;
	float total=0;

	int id;

	private DataHandler(){

	}

	public static DataHandler getInstance(){
		return dd;
	}


	public void acqui(int i){
		if (i==254){
			pos=0;
		}
		else if (pos==5){
			cleanInput(i);
		}
		pos++;
	}



	public void cleanInput(int i){
		val=i;
		if(val!=0){
			data+=val;//Average maths
			total++;//Average maths
		}
		if(val<min||min==0) // max value
			min=val;
		else if(val>max)
			max=val;
		setChanged();
		notifyObservers();
	}

	public String getLastValue(){


		return 60000/val + " HRV(ms)";
	}

	public String getBPMval(){
		return val + " BPM";
	}
	public float getLastIntValue(){

		return 60000/val;
	}

	public String getMin(){
		return "Min " + min + " BPM";
	}

	public String getMax(){

		return "Max " + max + " BPM";
	}

	public String getAvg(){
		if(total==0)
			return "Avg " + 0 + " HRV";
		return "Avg " + data/total + " HRV";
	}

	public void setNewValue(boolean newValue) {
		this.newValue = newValue;
	}

	public SimpleXYSeries getSeries1() {
		return series1;
	}

	public void setSeries1(SimpleXYSeries series1) {
		this.series1 = series1;
	}

	public void setSeries2(SimpleXYSeries series2) {
		this.series2 = series1;
	}


	public ConnectThread getReader() {
		return reader;
	}

	public void setReader(ConnectThread reader) {
		this.reader = reader;
	}
	// getID was int, now float.

	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id=id;
	}

	public void setH7(H7ConnectThread H7){
		this.H7=H7;
	}
	public H7ConnectThread getH7(){
		return H7;
	}


}
