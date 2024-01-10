//Atm constructor class
public class Currencies {
		//variables for characteristics
		private String name;
		private double CADConversion;
		//constructors
		public Currencies() {
			name = "USD";
			CADConversion = 1.00;
			}
		public Currencies(String nme, double cdc) {
			nme = name;
			cdc = CADConversion;
			}
		
		public String getName() {
			return name;
		}
		public void setName(String nme){
			name = nme;
		}
		
		public double getCADConversion() {
			return CADConversion;
		}
		public void setCADConversion(double cdc){
			CADConversion = cdc;
		}
		
		//add rest of info
		//method to display all info of the Dog
		public String toString() {
			//Change this to output bus info
			String output = "Name: " + name + "\n";
			output += "CAD Conversion Rate: " + CADConversion + "\n";
			//output string is complete, return it
			return output;
		}
	}
