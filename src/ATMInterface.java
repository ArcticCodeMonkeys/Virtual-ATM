import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ATMInterface {
	// Declare variables and objects
	static Atm cur = new Atm();
	static Currencies conv = new Currencies();
	static JFrame f = new JFrame("ATM Central");
	static JButton insertCard = new JButton("Insert Card to Begin");
	static JButton login = new JButton("Login");
	static JButton create = new JButton("Create Account");
	static JButton interest = new JButton("Interest Calculator");
	static JButton deposit = new JButton("Deposit");
	static JButton withdraw = new JButton("Withdraw");
	static JButton foreignCur = new JButton("Foreign Currency");
	static JButton logout = new JButton("Log out");
	static JLabel balance = new JLabel("Current Balance: ");
	static JLabel change = new JLabel("");
	static JLabel thanks = new JLabel("Thank you for banking with ATM Central");
	static String currencyType;
	static NumberFormat CAD = NumberFormat.getCurrencyInstance(Locale.CANADA);
	static String[] userInfo = new String[200];

	public static void insertCard() {
		// remove the button and wait
		Container parent0 = insertCard.getParent();
		parent0.remove(insertCard);
		parent0.revalidate();
		parent0.repaint();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// add the login and create buttons
		f.add(login);
		f.add(create);
	}

	public static void login() {
		// asks for name and pass
		String userCheck = JOptionPane.showInputDialog("Please Enter Your First and Last Name (Ex. John Doe)");
		String passCheck = JOptionPane.showInputDialog("Please Enter a Password");
		// boolean allRight = false;
		// boolean userRight = false;

		int allRight = 0;
		int infoCount = 0;
		// reads all user info from a file
		String line = "";
		try (FileReader reader = new FileReader("info.txt"); BufferedReader br = new BufferedReader(reader)) {
			// read line by line

			/*
			 * for(int i = 0; i < line.length()+1; i++ ) { char currentChar =
			 * line.charAt(i); if(currentChar != '[') { first = first + line.charAt(i); }
			 * else {
			 * 
			 * } }
			 */
			while ((line = br.readLine()) != null) {
				infoCount++;
				// store into array
				String decodedLine = decode(line, 12);
				// System.out.println("decoded: " + decodedLine);
				userInfo[infoCount] = decodedLine;

				// if the line currently being read is the password, and the one before is the
				// user, then a correct login has been inputted
				if (decodedLine.equals(passCheck) && userInfo[infoCount - 1].equals(userCheck)) {

					allRight = 1;

					// setBalance to the one in the file and store which line it was on
					cur.setCurBalance(Double.parseDouble(userInfo[infoCount - 2]));
					// System.out.println("Balance: " +userInfo[infoCount-2]);
					cur.setBalLine(infoCount - 2);
				}

			}
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}

		if (allRight == 1) {
			// sets info
			cur.setUser(userCheck);
			cur.setPass(passCheck);
			// removes buttons
			Container parent1 = login.getParent();
			parent1.remove(login);
			parent1.revalidate();
			parent1.repaint();
			Container parent2 = create.getParent();
			parent2.remove(create);
			parent2.revalidate();
			parent2.repaint();
			// adds main interface
			f.add(deposit);
			f.add(withdraw);
			f.add(interest);
			f.add(logout);
			f.add(foreignCur);
			f.add(change);
			// sets balance text
			balance.setText("Current Balance: " + CAD.format(cur.getCurBalance()));
			f.add(balance);

		} else {
			// if wrong, error message
			JOptionPane.showMessageDialog(f, "Your username or password was incorrect. Try again or create an account",
					"Error", JOptionPane.ERROR_MESSAGE);

			// Delete button
			/*
			 * Container parent = login.getParent(); parent.remove(login);
			 * parent.revalidate(); parent.repaint();
			 */
		}
	}

	public static void logout() {

		// confirm they want to log out
		int n = JOptionPane.showOptionDialog(new JFrame(), "Are you sure you want to log out?", "Logout Confirm",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Yes", "No" },
				JOptionPane.YES_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			// if yes, remove everything from screen
			Container parent3 = interest.getParent();
			parent3.remove(interest);
			parent3.revalidate();
			parent3.repaint();
			Container parent4 = deposit.getParent();
			parent4.remove(deposit);
			parent4.revalidate();
			parent4.repaint();
			Container parent5 = withdraw.getParent();
			parent5.remove(withdraw);
			parent5.revalidate();
			parent5.repaint();
			Container parent6 = logout.getParent();
			parent6.remove(logout);
			parent6.revalidate();
			parent6.repaint();
			Container parent7 = balance.getParent();
			parent7.remove(balance);
			parent7.revalidate();
			parent7.repaint();
			Container parent8 = foreignCur.getParent();
			parent8.remove(foreignCur);
			parent8.revalidate();
			parent8.repaint();
			// add a thank you message, play a little jingle
			f.add(thanks);

			// write the new changed info to a file
			try {
				// Creates a FileWriter
				FileWriter file = new FileWriter("info.txt");

				// Creates a BufferedWriter
				BufferedWriter output = new BufferedWriter(file);

				// Writes the string to the file

				for (int i = 1; i < 200; i++) {
					if (i != cur.getBalLine()) {
						// uses stored info in the array from login
						if (userInfo[i] != null) {
							output.write(encode(userInfo[i], 12) + "\n");
						}
					} else {
						// if the line is the line of the balance, rewrite as the new balance, this
						// allows the program to remeber changes from previous uses
						output.write(encode("" + cur.getCurBalance(), 12) + "\n");
					}

				}

				// Closes the writer
				output.close();
			}

			catch (Exception e) {
				// error print
				e.getStackTrace();
			}

			// add line as a constructor of the atm object, use it store the line of the
			// current user balance, then whenever you file write, print all info in the
			// array, but if i (from the for loop) is = to line of cur user balance then
			// print new cur balance.then continue
			// wait so that the jingle has time to play and the user can read the thank you
			// message
			t1.start();
			// if they cancel or say no just close the option pane
		} else if (n == JOptionPane.NO_OPTION) {

		} else if (n == JOptionPane.CLOSED_OPTION) {

		}
	}

	public static void withdraw() {
		// follows the same as deposit, with obvious changes such as subtraction and
		// asking for withdrawl amount
		double withdrawValue = 0;
		String withdrawString = JOptionPane.showInputDialog("Please Enter the Amount You Would Like To Withdraw.");
		try {
			withdrawString.replaceAll("$", "");
			withdrawString.replaceAll(",", "");
			withdrawValue = Double.parseDouble(withdrawString);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(f, "That is an invalid input", "Error", JOptionPane.ERROR_MESSAGE);
		}

		if (withdrawValue <= 0) {
			JOptionPane.showMessageDialog(f, "You cannot withdraw an amount less than or equal to $0.00", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else if (withdrawValue > cur.getCurBalance()) {
			// disallow withdrawl of an amount greater that the current balance
			JOptionPane.showMessageDialog(f, "You cannot withdraw an amount greater than your current balance", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {

			cur.setCurBalance(cur.getCurBalance() - withdrawValue);
			f.add(change);
			change.setText("- " + CAD.format(withdrawValue));
			balance.setText("Current Balance: " + CAD.format(cur.getCurBalance()));
			Thread t2 = new Thread(new Runnable() {
				@Override
				// terminate
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Container parent10 = change.getParent();
					parent10.remove(change);
					parent10.revalidate();
					parent10.repaint();

				}
			});
			t2.start();

		}
	}

	public static void deposit() {
		// var declaration
		double depositValue = 0;
		String balanceFormatter;
		// get deposit amount
		String depositString = JOptionPane.showInputDialog("Please Enter the Amount You Would Like To Deposit.");
		try {
			// format
			depositString.replaceAll("[$]", "");
			depositString.replaceAll(",", "");
			depositValue = Double.parseDouble(depositString);
		} catch (Exception e) {
			// errors
			JOptionPane.showMessageDialog(f, "That is an invalid input", "Error", JOptionPane.ERROR_MESSAGE);

		}

		if (depositValue <= 0) {
			// disallow negative deposits
			JOptionPane.showMessageDialog(f, "You cannot deposit an amount less than or equal to $0.00", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			// formzt
			DecimalFormat df = new DecimalFormat("#.##");

			balanceFormatter = "" + df.format(depositValue);
			// set balance
			cur.setCurBalance(cur.getCurBalance() + Double.parseDouble(balanceFormatter));
			f.add(change);
			// change popup
			change.setText("+ " + CAD.format(depositValue));
			// set balance text
			balance.setText("Current Balance: " + CAD.format(cur.getCurBalance()));
			Thread t2 = new Thread(new Runnable() {
				@Override
				// terminate
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Container parent10 = change.getParent();
					parent10.remove(change);
					parent10.revalidate();
					parent10.repaint();

				}
			});
			t2.start();

		}
	}

	public static void interest() {
		// annual interest rate
		double interest = 0;
		// compound periods per year
		double cppy = 0;
		// number of years left to accumulate
		int years = 0;
		// interest per compounding period
		double ipc = 0;
		// completed periods
		double cp = 0;
		// total
		double depositAmount = 0;
		String balanceFormatter;
		// asks for rate, # of periods and time
		String interestString = JOptionPane.showInputDialog("Please Enter The Annual Interest Rate");
		String cppyString = JOptionPane.showInputDialog("Please Enter The Number of Compounding Periods Per Year");
		String yearsString = JOptionPane.showInputDialog(
				"Please Enter Number of Years You Would Like To Leave Your Balance Accumulating Interest");
		try {
			// formatting
			interestString.replaceAll("%", "");
			interest = Double.parseDouble(interestString);
			cppy = Integer.parseInt(cppyString);
			years = Integer.parseInt(yearsString);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(f,
					"There was an invalid input. Please only input numbers, do not include symbols", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		// interest per compounding period is the annual interest rate divided by the
		// number of compounding periods per year ex. 1 year / 12 compunding periods is
		// 1/12% interest rate per compunding period.
		ipc = interest / cppy;
		// divide by 100 to get percentage then add 1 ex. 12/100 = 0.12 0.12+1=1.12 =
		// 112%
		ipc = ipc / 100;
		ipc = ipc + 1;
		// the number of completed compounding periods is equal to the number of
		// compounding periods per year times the number of years but rounded down
		// incase a compounding period is not completely finished
		cp = Math.floor(cppy * years);

		// do this a number of times equal to the cp
		depositAmount = cur.getCurBalance() * Math.pow(ipc, cp);
		// formatting
		DecimalFormat df = new DecimalFormat("#.##");

		balanceFormatter = "" + df.format(depositAmount);

		if (cp != 0) {
			// set balance
			cur.setCurBalance(cur.getCurBalance() + Double.parseDouble(balanceFormatter));
			f.add(change);
			// change is a little text that pops up, showing how much your balance changed
			// by.
			change.setText("+ $" + balanceFormatter);
			balance.setText("Current Balance: " + CAD.format(cur.getCurBalance()));
			// this thread just makes the change pop up go away after 1 second
			Thread t2 = new Thread(new Runnable() {
				@Override
				// terminate
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Container parent10 = change.getParent();
					parent10.remove(change);
					parent10.revalidate();
					parent10.repaint();

				}
			});
			t2.start();
		}
	}

	public static void create() {
		char[] alphabet = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
				'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		boolean userError = true;
		boolean passError = true;
		// takes user and pass input
		String user = JOptionPane.showInputDialog("Please Enter Your First and Last Name (Ex. John Doe)");
		String pass = JOptionPane.showInputDialog("Please Enter a Password");
		// writes them to the file
		for (int p = 0; p < 26; p++) {
			if ((user.toUpperCase()).contains("" + alphabet[p]) == true) {
				userError = false;
			}
			if ((pass.toUpperCase()).contains("" + alphabet[p]) == true) {
				passError = false;
			}
		}
		try (FileWriter writer = new FileWriter("info.txt", true);

				BufferedWriter bw = new BufferedWriter(writer)) {
			// make it so that the user cant input symbols or numbers in their name
			if (user == null || pass == null || user == "" || pass == "" || user == "\n" || pass == "\n" || user == " "
					|| pass == " ") {
				JOptionPane.showMessageDialog(f, "Your username or password cannot be empty or null", "Error",
						JOptionPane.ERROR_MESSAGE);

			} else if (userError == true || passError == true) {
				JOptionPane.showMessageDialog(f, "Your username or password must contain a character", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {

				// writes the balance, user and pass to file
				bw.write(encode("0.00", 12) + "\n");
				bw.write(encode(user, 12) + "\n");
				bw.write(encode(pass, 12) + "\n");
				cur.setUser(user);
				cur.setPass(pass);

			}
			bw.close();

		} catch (Exception e) {
			System.err.format("IOException: %s%n", e);
		}
	}

	public static void foreignCurrency() {
		// var declaration + currency storing
		int custom = 0;
		String[] currencies = { "USD", "EUR", "JPY", "GBP", "AUD", "Custom" };
		// ask for which currency is being used
		currencyType = (String) JOptionPane.showInputDialog(null, "Which Foreign Currency Would You Like To Work With?",
				"Choose Foreign Currency", JOptionPane.QUESTION_MESSAGE, null, currencies, currencies[0]);
		// if it wasn't custom, set its name to what ever its supposed to be
		if (currencyType != "Custom") {
			conv.setName(currencyType);
		} else {
			// else get its name
			String customType = JOptionPane.showInputDialog("What is the name of your custom currency");
			conv.setName(customType);
			// delcare it as a custom currency
			custom = 1;
		}
		// store movement options
		String[] movements = { "Deposit", "Withdraw", "Cancel" };
//ask wether to deposit or withdraw
		String movementType = (String) JOptionPane.showInputDialog(null, "What Would You Like To Do?", "Confirm Option",
				JOptionPane.QUESTION_MESSAGE, null, movements, movements[0]);
		// if not custom, it will ask to either use a set current value or the user can
		// input a custom value
		if (custom != 1) {
			String[] defaults = { "Current Value", "Custom Value", "Cancel" };

			String defaultCheck = (String) JOptionPane.showInputDialog(null,
					"Would you like to use the current stock values of " + conv.getName() + " , or input your own?",
					"Confirm Option", JOptionPane.QUESTION_MESSAGE, null, defaults, defaults[0]);
			// if they want custom, ask them for the rate
			if (defaultCheck == "Custom Value") {
				try {
					String newValue = JOptionPane
							.showInputDialog("Please Enter Conversion Rate From $1.00 CAD to " + conv.getName() + ".");

					conv.setCADConversion(Double.parseDouble(newValue));
				} catch (Exception e) {
					JOptionPane.showMessageDialog(f, "Please Only Enter a Number", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				// otherwise get the current rates
				getMarketRates();
			}
		} else {
			// if it is custom, just ask for the rate
			try {
				String newValue = JOptionPane
						.showInputDialog("Please Enter Conversion Rate From $1.00 CAD to " + conv.getName() + ".");

				conv.setCADConversion(Double.parseDouble(newValue));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(f, "Please Only Enter a Number", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

//if(movementType == "Cancel" || defaultCheck == "Cancel" ) {

//} else {
		switch (movementType) {
		case "Deposit":
			// depositing
			try {
				// ask how much of the currency they are depositing
				String foreignDeposit = JOptionPane
						.showInputDialog("Please Enter How Many " + conv.getName() + " You Would Like To Deposit.");
				// stop negatives
				if (Double.parseDouble(foreignDeposit) < 0) {
					JOptionPane.showMessageDialog(f, "You cannot deposit a negative value", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					// multiply by 1/conv rate and change balnce
					cur.setCurBalance(
							cur.getCurBalance() + (Double.parseDouble(foreignDeposit) * (1 / conv.getCADConversion())));
					f.add(change);
					change.setText(
							"+ " + CAD.format(Double.parseDouble(foreignDeposit) * (1 / conv.getCADConversion())));
					balance.setText("Current Balance: " + CAD.format(cur.getCurBalance()));
					// popup waiter
					Thread t2 = new Thread(new Runnable() {
						@Override
						// terminate
						public void run() {
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Container parent10 = change.getParent();
							parent10.remove(change);
							parent10.revalidate();
							parent10.repaint();

						}
					});
					t2.start();
				}
			} catch (Exception e) {
				// error catch
				JOptionPane.showMessageDialog(f, "Please Only Enter a Number", "Error", JOptionPane.ERROR_MESSAGE);
			}

			break;
		case "Withdraw":
			// withdrawing
			try {
				// ask for amount of foreign currency
				String foreignWithdraw = JOptionPane
						.showInputDialog("Please Enter How Many " + conv.getName() + " You Would Like To Withdraw.");
				// stop negative and values more than current balance after conversion
				if (Double.parseDouble(foreignWithdraw) < 0) {
					JOptionPane.showMessageDialog(f, "You cannot withdraw a negative value", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else if ((Double.parseDouble(foreignWithdraw) * (conv.getCADConversion())) > cur.getCurBalance()) {
					JOptionPane.showMessageDialog(f, "You cannot withdraw an amount greater than your current balance",
							"Error", JOptionPane.ERROR_MESSAGE);
				} else {
					// subtract amount multiplied by 1/rate from account
					cur.setCurBalance(
							cur.getCurBalance() - (Double.parseDouble(foreignWithdraw) * (conv.getCADConversion())));
					f.add(change);
					change.setText(
							"- " + CAD.format(Double.parseDouble(foreignWithdraw) * (1 / conv.getCADConversion())));
					balance.setText("Current Balance: " + CAD.format(cur.getCurBalance()));
					Thread t2 = new Thread(new Runnable() {
						@Override
						// terminate
						public void run() {
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Container parent10 = change.getParent();
							parent10.remove(change);
							parent10.revalidate();
							parent10.repaint();

						}
					});
					t2.start();
				}
			} catch (Exception e) {

			}
			break;
		}
	}

	// 5 second wait for logout
	static Thread t1 = new Thread(new Runnable() {
		@Override
		// terminate
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// then terminate
			System.exit(0);
		}
	});

	// stores market rates
	public static void getMarketRates() {
		// depending on currency type, sets conv rate
		switch (currencyType) {
		case "USD":
			// conv.setCADConversion(Double.parseDouble(excerptUSD));
			conv.setCADConversion(0.78);
			break;
		case "EUR":
			// conv.setCADConversion(Double.parseDouble(excerptEUR));
			conv.setCADConversion(0.74);
			break;
		case "JPY":
			conv.setCADConversion(101.21);
			// conv.setCADConversion(Double.parseDouble(excerptJPY));
			break;
		case "GBP":
			// conv.setCADConversion(Double.parseDouble(excerptGBP));
			conv.setCADConversion(0.62);
			break;
		case "AUD":
			conv.setCADConversion(1.09);
			// conv.setCADConversion(Double.parseDouble(excerptAUD));
			break;

		}
	}

	public static String decode(String in, int offset) {
		return encode(in, 26 - offset);
	}

	public static String encode(String in, int offset) {
		// System.out.println(offset%26);
		offset = offset % 26 + 26;
		String encoded = "";
		for (char i : in.toCharArray()) {
			if (Character.isLetter(i)) {
				if (Character.isUpperCase(i)) {
					encoded = encoded + ((char) ('A' + (i - 'A' + offset) % 26));
				} else {
					encoded = encoded + ((char) ('a' + (i - 'a' + offset) % 26));
				}
			} else {
				encoded = encoded + (i);
			}
		}
		return encoded.toString();
	}

	public static void GUI() {
		// Make Frame
		// Jlabel and JButton setup + Currency formatter
		insertCard.setBounds(550, 300, 300, 100);
		login.setBounds(550, 100, 300, 100);
		create.setBounds(550, 500, 300, 100);
		interest.setBounds(900, 0, 300, 100);
		deposit.setBounds(900, 150, 300, 100);
		withdraw.setBounds(900, 300, 300, 100);
		foreignCur.setBounds(900, 450, 300, 100);
		logout.setBounds(900, 600, 300, 100);
		balance.setBounds(100, 100, 700, 100);
		balance.setFont(new Font("Serif", Font.PLAIN, 25));
		change.setBounds(100, 200, 700, 100);
		change.setFont(new Font("Serif", Font.PLAIN, 25));
		thanks.setBounds(475, 300, 700, 100);
		thanks.setFont(new Font("Serif", Font.PLAIN, 25));

		f.add(insertCard);
		f.setSize(1920, 1080);
		f.setLayout(null);
		f.setResizable(true);
		f.setVisible(true);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// set up userInfo

		int balancePos = 0;

		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				logout();
			}
		});
		// when user presses insert card
		insertCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				insertCard();
			}
		});
		// when user presses login
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				login();
			}
		});
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				create();
			}
		});
		// interest calculator
		interest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				interest();
			}
		});
		// deposit button
		deposit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deposit();
			}
		});
		withdraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				withdraw();
			}
		});

		foreignCur.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				foreignCurrency();
			}
		});
		// logout button
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				logout();
			}
		});
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// call GUI, which contains the entire program
		GUI();
	}

}
