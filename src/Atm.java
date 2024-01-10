//Atm constructor class
public class Atm {
		//variables for characteristics
		private String bank, user, pass;
		private double curBalance;
		private int balLine;
		//constructors
		public Atm() {
			bank = "RBC";
			curBalance = 0;
			user = "John Doe";
			pass = "Ball1234";
			balLine = 0;
			}
		public Atm(String bnk, String usr, String pss, double cb, int blLn) {
			bnk= bank;
			cb = curBalance;
			usr = user;
			pss = pass;
			blLn = balLine;
			}
		
		public String getBank() {
			return bank;
		}
		public void setBank(String bnk){
			bank = bnk;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String usr){
			user = usr;
		}
		public String getPass() {
			return pass;
		}
		public void setPass(String pss){
			pass = pss;
		}
		public double getCurBalance() {
			return curBalance;
		}
		public void setCurBalance(double cb){
			curBalance = cb;
		}
		public int getBalLine() {
			return balLine;
		}
		public void setBalLine(int blLn) {
			balLine = blLn;
		}
		//add rest of info
		//method to display all info of the Dog
		public String toString() {
			//Change this to output bus info
			String output = "Bank: " + bank + "\n";
			output += "Starting Balance: " + curBalance + "\n";
			//output string is complete, return it
			return output;
		}
	}
