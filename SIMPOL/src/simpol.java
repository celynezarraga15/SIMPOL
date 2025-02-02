import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.io.*;
import java.lang.Object.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class simpol extends JPanel{

	JFrame frame = new JFrame("SIMPOL: Simple Interpreter");
	
	Container overallFrame = new Container();
	Container overall1 = new Container();
	Container overall1a = new Container();
	Container overall1b = new Container();
	Container overall1c = new Container();
	Container overall2 = new Container();
	Container overall3a = new Container();
	Container overall3b = new Container();
	
	DefaultTableModel symbolTable;
	DefaultTableModel lexemeTable;
	JTextArea inputText;
	JTextArea consoleText;
	JTable table1;
	JTable table2;
	Boolean hasError = false;
	Boolean variableStart = false;
	Boolean codeStart = false;
	int codeIndex;

	HashMap<String,String> reservedWords = new HashMap<String,String>();
	HashMap<String,String> variablesTypes = new HashMap<String,String>();
	HashMap<String,String> variablesValues = new HashMap<String,String>();
	ArrayList<String> symbolsInTable = new ArrayList<String>();
	
	public simpol(){
		renderFrame();
		addLabelsAndContainers();
		packFrame();
	}

	public static void main(String[] args){
		simpol simleInterpreter = new simpol();
	}

	public void renderFrame(){
		frame.setSize(new Dimension (1500,700));						//frame
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		
		overallFrame.setPreferredSize(new Dimension(1500, 700));
		overallFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		overall1.setPreferredSize(new Dimension(1500,350));
		overall1.setLayout(new GridLayout(1,3,2,2));
		
		overall2.setPreferredSize(new Dimension(1500,50));
		overall2.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		overall3a.setPreferredSize(new Dimension(1500,25));
		overall3a.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		overall3b.setPreferredSize(new Dimension(1500,375));
		overall3b.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		overall1a.setPreferredSize(new Dimension(300,350));
		overall1a.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		overall1b.setPreferredSize(new Dimension(650,350));
		overall1b.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		overall1c.setPreferredSize(new Dimension(550,350));
		overall1c.setLayout(new FlowLayout(FlowLayout.CENTER));
	
	
	}

	public void readFile(File fileToRead){
		try (BufferedReader br = new BufferedReader(new FileReader(fileToRead))) {

			String line;
															
			while ((line = br.readLine()) != null) {		//read per line
				String current = inputText.getText();
				inputText.setText(current.concat(line + "\n"));
			}

		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	public void initializeReservedWords(){
		reservedWords.put("variable", "Start of Variable Declaration");
		reservedWords.put("INT", "Data Type");
		reservedWords.put("BLN", "Data Type");
		reservedWords.put("STG", "Data Type");
		reservedWords.put("code", "Start of Code Segment");
		reservedWords.put("PRT", "Print Operator");
		reservedWords.put("ASK", "Ask Operator");
		reservedWords.put("PUT", "Assignment Operator");
		reservedWords.put("IN", "Assignment Operator");
		reservedWords.put("ADD", "Arithmetic Operator");
		reservedWords.put("SUB", "Arithmetic Operator");
		reservedWords.put("MUL", "Arithmetic Operator");
		reservedWords.put("DIV", "Arithmetic Operator");
		reservedWords.put("MOD", "Arithmetic Operator");
		reservedWords.put("GRT", "Numeric Predicate");
		reservedWords.put("GRE", "Numeric Predicate");
		reservedWords.put("LET", "Numeric Predicate");
		reservedWords.put("LEE", "Numeric Predicate");
		reservedWords.put("EQL", "Numeric Predicate");
		reservedWords.put("AND", "Logical Operator");
		reservedWords.put("OHR", "Logical Operator");
		reservedWords.put("NON", "Logical Operator");
		reservedWords.put("{", "Left Curly Brace");
		reservedWords.put("}", "Right Curly Brace");
	}
	
	public void interpretInput(){
		hasError = false;
		initializeReservedWords();
		
		String input = inputText.getText();
		
		if(input.length() != 0 && !(hasError)){
			if(input.contains("}")){
				String[] segment = new String[2];
				if((input.indexOf("}")+1) < input.length()){
					segment[0] = input.substring(0, input.indexOf("}")+1);
					segment[1] = input.substring(input.indexOf("}")+1);
					
					processVariableSegment(segment[0]);
					if(!hasError){
						processCodeSegment(segment[1]);
					}
				}
				else{
					print("Error: Incomplete Segments!");
					hasError = true;
				}
				
			}
			else{
				print("Error: No complete segment in code. '}' missing.");
				hasError = true;
			}
		}
		else{
			print("Error: Empty Input. Choose a file, or enter your code.");
			hasError = true;
		}

	}
	
	public void print(String toPrint){
		consoleText.setText(consoleText.getText().concat("SYSTEM_I/O: " + toPrint + "\n"));
	}
	
	public void processVariableSegment(String varSegment){
		String[] lines = varSegment.split("\\s");
		
		if(lines[0].equals("variable") && variableStart==false){
			lexemeTable.addRow(new Object[]{lines[0], reservedWords.get(lines[0])});

			print("  == VARIABLE DECLARATION STARTED ==  ");
			
			if(lines[1].equals("{")){
				lexemeTable.addRow(new Object[]{lines[1], reservedWords.get(lines[1])});
				
				int index = 2;
				while((index<lines.length) && !(lines[index].equals("}"))){
					
					if(reservedWords.containsKey(lines[index])){
						switch(lines[index]){
							case "INT": lexemeTable.addRow(new Object[]{lines[index], reservedWords.get(lines[index])});
										if(!(reservedWords.containsKey(lines[index+1])) && !(variablesTypes.containsKey(lines[index+1]))){
											lexemeTable.addRow(new Object[]{lines[index+1], "Variable Name"});
											symbolTable.addRow(new Object[]{lines[index+1], lines[index], "null"});
											variablesTypes.put(lines[index+1], lines[index]);
											variablesValues.put(lines[index+1], "null");
											symbolsInTable.add(lines[index+1]);
										}
										else{
											if(variablesTypes.containsKey(lines[index+1])){
												print("Error: Variable name already exists ->  ".concat(lines[index+1]));
											}
											else{
												print("Error: Invalid variable name ->  ".concat(lines[index+1]));
											}
											hasError = true;
											return;
										}
										break;
							case "BLN": lexemeTable.addRow(new Object[]{lines[index], reservedWords.get(lines[index])});
										if(!(reservedWords.containsKey(lines[index+1])) && !(variablesTypes.containsKey(lines[index+1]))){
											lexemeTable.addRow(new Object[]{lines[index+1], "Variable Name"});
											symbolTable.addRow(new Object[]{lines[index+1], lines[index], "null"});
											variablesTypes.put(lines[index+1], lines[index]);
											variablesValues.put(lines[index+1], "null");
											symbolsInTable.add(lines[index+1]);
										}
										else{
											if(variablesTypes.containsKey(lines[index+1])){
												print("Error: Variable name already exists ->  ".concat(lines[index+1]));
											}
											else{
												print("Error: Invalid variable name ->  ".concat(lines[index+1]));
											}
											hasError = true;
											return;
										}
										break;
							case "STG":	lexemeTable.addRow(new Object[]{lines[index], reservedWords.get(lines[index])});
										if(!(reservedWords.containsKey(lines[index+1])) && !(variablesTypes.containsKey(lines[index+1]))){
											lexemeTable.addRow(new Object[]{lines[index+1], "Variable Name"});
											symbolTable.addRow(new Object[]{lines[index+1], lines[index], "null"});
											variablesTypes.put(lines[index+1], lines[index]);
											variablesValues.put(lines[index+1], "null");
											symbolsInTable.add(lines[index+1]);
										}
										else{
											if(variablesTypes.containsKey(lines[index+1])){
												print("Error: Variable name already exists ->  ".concat(lines[index+1]));
											}
											else{
												print("Error: Invalid variable name ->  ".concat(lines[index+1]));
											}
											hasError = true;
											return;
										}
										break;
							default:	print("Error: Invalid data type -> ".concat(lines[index]));
										hasError = true;
										return;
						}
					}
					else{
						print("Error: Invalid data type -> ".concat(lines[index]));
						hasError = true;
						return;
					}
					index+=2;
				}
				if(lines[index].equals("}")){
					lexemeTable.addRow(new Object[]{lines[index], reservedWords.get(lines[index])});
					print("  == VARIABLE DECLARATION ENDED ==  ");
				}
				else{
					print("Error: No right curly brace found to end variable declaration segment properly.");
					hasError = true;
					return;
				}
			}
			else{
				print("Error: No left curly brace found after start of variable declaration segment.");
				hasError = true;
				return;
			}
		}
		else{
			if(variableStart){
				print("Error: Multiple start of variable declaration segment found.");
			}
			else{
				print("Error: No start of variable declaration segment found.");
			}
			hasError = true;
			return;
		}
	}
	
	public void addLabelsAndContainers(){
		JButton browseFileButton = new JButton("Select file");
		
		ActionListener chooseFileButton = new ActionListener(){
			public void actionPerformed(ActionEvent e){
		       	JFileChooser chooser= new JFileChooser();
		       	FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "sim");
				chooser.setFileFilter(filter);

				int choice = chooser.showOpenDialog(simpol.this);
				if (choice != JFileChooser.APPROVE_OPTION){
					System.out.println("File reading cancelled.");
				}
				else{
					File chosenFile = chooser.getSelectedFile();
					readFile(chosenFile);							//read file	
		    	}
		    }
		};

		browseFileButton.addActionListener(chooseFileButton);
		overall1a.add(browseFileButton);
		
		inputText = new JTextArea(450,275);										//input field
		inputText.setLineWrap(true);
		inputText.setWrapStyleWord(true);
		JScrollPane areaScrollPane = new JScrollPane(inputText);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(450, 275));
		overall1a.add(areaScrollPane);
		
		JButton clearButton = new JButton("Clear");
		
		ActionListener clearText = new ActionListener(){
			public void actionPerformed(ActionEvent e){
		       	inputText.setText("");
		    }
		};
		
		clearButton.addActionListener(clearText);
		overall1a.add(clearButton);
		
		JLabel symTable_label = new JLabel("SYMBOL TABLE");
		overall1b.add(symTable_label);
		
		String[] colHeadings1 = {"VAR","TYPE","VALUE"};
		int numRows1 = 0 ;
		symbolTable = new DefaultTableModel(numRows1, colHeadings1.length);
		symbolTable.setColumnIdentifiers(colHeadings1);
		table1 = new JTable(symbolTable);

		table1.setPreferredScrollableViewportSize(new Dimension(450,300));
		table1.setEnabled(false);
		overall1b.add(table1);
		overall1b.add(new JScrollPane(table1));
		
		JLabel lexTable_label = new JLabel("TOKENS/LEXEMES");
		overall1c.add(lexTable_label);
		
		String[] colHeadings2 = {"TOKEN","LEXEME"};
		int numRows2 = 0 ;
		lexemeTable = new DefaultTableModel(numRows2, colHeadings2.length);
		lexemeTable.setColumnIdentifiers(colHeadings2);
		table2 = new JTable(lexemeTable);

		table2.setPreferredScrollableViewportSize(new Dimension(450,300));
		table2.setEnabled(false);
		overall1c.add(table2);
		overall1c.add(new JScrollPane(table2));
		
		JButton executeButton = new JButton("EXECUTE");
		executeButton.setPreferredSize(new Dimension(1450, 50));
		
		ActionListener exec = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				for (int i = symbolTable.getRowCount() - 1; i > -1; i--){
			        symbolTable.removeRow(i);
			    }
				for (int i = lexemeTable.getRowCount() - 1; i > -1; i--){
					lexemeTable.removeRow(i);
			    }
				
				reservedWords.clear();
				variablesTypes.clear();
				variablesValues.clear();
				symbolsInTable.clear();
				
				interpretInput();
		    }
		};
		
		executeButton.addActionListener(exec);
		overall2.add(executeButton);
		
		JLabel console_label = new JLabel("CONSOLE");
		overall3a.add(console_label);
		
		consoleText = new JTextArea(1450,225);
		consoleText.setEditable(false);
		consoleText.setLineWrap(true);
		consoleText.setWrapStyleWord(true);
		JScrollPane areaScrollPane1 = new JScrollPane(consoleText);
		areaScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane1.setPreferredSize(new Dimension(1450, 225));
		overall3b.add(areaScrollPane1);
	}

	public void packFrame(){
		overall1.add(overall1a);
		overall1.add(overall1b);
		overall1.add(overall1c);
		
		overallFrame.add(overall1);
		overallFrame.add(overall2);
		overallFrame.add(overall3a);
		overallFrame.add(overall3b);
		
		frame.getContentPane().setBackground(Color.gray);

		frame.add(overallFrame);
		frame.pack();
		frame.setVisible(true);
	}

	public void processCodeSegment(String codeSegment){
		String[] lines = codeSegment.trim().split("\\s+");
		int x;
		Boolean temp;
		
		if(lines[0].equals("code") && codeStart==false){
			lexemeTable.addRow(new Object[]{lines[0], reservedWords.get(lines[0])});

			print("  == PROGRAM STARTED ==  ");
			
			if(lines[1].equals("{") && !(hasError)){
				lexemeTable.addRow(new Object[]{lines[1], reservedWords.get(lines[1])});
				
				codeIndex = 2;
				while((codeIndex<lines.length) && !(lines[codeIndex].equals("}")) && !(hasError)){
					if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "PRT": printingOperation(lines);
										break;
							case "ASK": scanningOperation(lines);
										break;
							case "PUT": assignOperation(lines);
										break;
							case "ADD": x = add(lines);
										break;
							case "SUB": x = subtract(lines);
										break;
							case "MUL": x = multiply(lines);
										break;
							case "DIV": x = divide(lines);
										break;
							case "MOD": x = modulo(lines);
										break;
							 case "GRT": temp = greaterThan(lines);
							 			break;
							 case "GRE": temp = greaterThanEqual(lines);
							 			break;
							 case "LET": temp = lessThan(lines);
							 			break;
							 case "LEE": temp = lessThanEqual(lines);
							 			break;
							 case "EQL": temp = equalOperation(lines);
							 			break;
							 case "AND": temp = andOperation(lines);
							 			break;
							 case "OHR": temp = orOperation(lines);
							 			break; 
							 case "NON": temp = notOperation(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return;
						}
					}
					else{
						print("Error: Invalid Operator!");
						hasError = true;
						return;
					}
				}
				if(lines[codeIndex].equals("}") && !(hasError)){
					lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
					print("  == PROGRAM ENDED WITH NO ERRORS ==  ");
				}
				else{
					print("Error: No right curly brace found to end code segment properly.");
					hasError = true;
					return;
				}
			}
			else{
				print("Error: No left curly brace found after start of code segment.");
				hasError = true;
				return;
			}
		}
		else{
			if(codeStart){
				print("Error: Multiple start of code segment found.");
			}
			else{
				print("Error: No start of code segment found.");
			}
			hasError = true;
			return;
		}
	}

	public void assignOperation(String[] lines){
		if(!hasError){
			if(lines[codeIndex].equals("PUT")){
				int x = 0;
				Boolean temp = false;
				String str = "";
				int ch = 0;

				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				while(!(lines[codeIndex].equals("IN")) && (codeIndex<lines.length)){
					if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": x = add(lines);
										ch = 1;
										break;
							case "SUB": x = subtract(lines);
										ch = 1;
										break;
							case "MUL": x = multiply(lines);
										ch = 1;
										break;
							case "DIV": x = divide(lines);
										ch = 1;
										break;
							case "MOD": x = modulo(lines);
										ch = 1;
										break;
							 case "GRT": temp = greaterThan(lines);
							 			ch = 2;
							 			break;
							 case "GRE": temp = greaterThanEqual(lines);
							 			ch = 2;
							 			break;
							 case "LET": temp = lessThan(lines);
							 			ch = 2;
							 			break;
							 case "LEE": temp = lessThanEqual(lines);
							 			ch = 2;
							 			break;
							 case "EQL": temp = equalOperation(lines);
							 			ch = 2;
							 			break;
							 case "AND": temp = andOperation(lines);
							 			ch = 2;
							 			break;
							 case "OHR": temp = orOperation(lines);
							 			ch = 2;
							 			break; 
							 case "NON": temp = notOperation(lines);
							 			ch = 2;
										break;
							 default: break;
						}
					}
					else{
						if(variablesTypes.containsKey(lines[codeIndex])){
							switch(variablesTypes.get(lines[codeIndex])){
								case "INT": ch = 1;
											x = Integer.parseInt(variablesValues.get(lines[codeIndex]));
											codeIndex++;
											break;
								case "BLN": ch = 2;
											temp = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
											codeIndex++;
											break;
								case "STG": ch = 3;
											str = variablesValues.get(lines[codeIndex]);
											codeIndex++;
											break;
							}
						}
						else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
							ch = 3;
							str = lines[codeIndex];
							codeIndex++;
						}
						else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
							ch = 1;
							x = Integer.parseInt((lines[codeIndex]));
							codeIndex++;
						}
						else if(lines[codeIndex].matches("^\\$.*\\$$")){
							ch = 3;
							str = lines[codeIndex].substring(1, lines[codeIndex].length()-1);
							codeIndex++;
						}
						else if(lines[codeIndex].matches("^\\$.*")){
							String strFound = lines[codeIndex].substring(1);
							codeIndex++;
							
							while((codeIndex < lines.length) && (!(lines[codeIndex].matches("^.*\\$$")))){
								strFound = strFound.concat(" " + lines[codeIndex] + " ");
								codeIndex++;
							}

							if(lines[codeIndex].matches("^.*\\$$") && (codeIndex < lines.length)){
								strFound = strFound.concat(" " + lines[codeIndex].substring(0,lines[codeIndex].length()-1) + " ");
								str = strFound;
								ch = 3;
								codeIndex++;
							}
							else{
								print("Error: '$' as ending character of string not found.");
								hasError = true;
								return;
							}
						}
						else{
							print("Error: Invalid Operator!");
							hasError = true;
							return;
						}
						
					}
				}
				if(lines[codeIndex].equals("IN") && !(hasError)){
					lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
					codeIndex++;
					if((codeIndex<lines.length) && (variablesTypes.containsKey(lines[codeIndex]))){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						switch(ch){
							case 1: variablesValues.put(lines[codeIndex],String.valueOf(x));
									lexemeTable.addRow(new Object[]{String.valueOf(x), "Value"});
									symbolTable.setValueAt(String.valueOf(x), symbolsInTable.indexOf(lines[codeIndex]), 2);
									break;
							case 2: variablesValues.put(lines[codeIndex],String.valueOf(temp));
									lexemeTable.addRow(new Object[]{String.valueOf(temp), "Value"});
									symbolTable.setValueAt(String.valueOf(temp), symbolsInTable.indexOf(lines[codeIndex]), 2);
									break;
							case 3: variablesValues.put(lines[codeIndex],str);
									lexemeTable.addRow(new Object[]{String.valueOf(str), "Value"});
									symbolTable.setValueAt(str, symbolsInTable.indexOf(lines[codeIndex]), 2);
									break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 break;
						}

						codeIndex++;
					}
					else{
						if(!variablesTypes.containsKey(lines[codeIndex])){
							print("Error: Variable name '".concat(lines[codeIndex] + "' was not declared."));
						}
						else{
							print("Error: No variable found where value will be assigned.");
						}
						hasError = true;
						return;	
					}
				}
				else{
					print("Error: No 'IN' found after 'PUT' operator.");
					hasError = true;
				}
			}
		}
		return;
	}

	public void scanningOperation(String[] lines){
		String dialogMessage;
		String value = "";
		if(!hasError){
			if(lines[codeIndex].equals("ASK")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) && (variablesTypes.containsKey(lines[codeIndex]))){
					lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});

					switch(variablesTypes.get(lines[codeIndex])){
						case "INT": dialogMessage = "Enter value to be assigned to ".concat(lines[codeIndex] + " (Integer) :");
									while(value == null || value.isEmpty() || value.length()==0){
								        value= JOptionPane.showInputDialog(dialogMessage);
								    }
									if(value.matches("-?\\d+(\\.\\d+)?")){
										int inputValue = Integer.parseInt(value);
										variablesValues.put(lines[codeIndex],String.valueOf(inputValue));
										table1.getModel().setValueAt(String.valueOf(inputValue), symbolsInTable.indexOf(lines[codeIndex]), 2);
									}
									else{
										print("Error: Invalid input.");
										hasError = true;
										return;
									}
									codeIndex++;
									break;
						case "BLN": dialogMessage = "Enter value to be assigned to ".concat(lines[codeIndex] + " (Boolean) :");
									while(value == null || value.isEmpty() || value.length()==0){
								        value= JOptionPane.showInputDialog(dialogMessage);
								    }
									if((value.equals("true")) || (value.equals("false"))){
										variablesValues.put(lines[codeIndex],value);
										symbolTable.setValueAt(value, symbolsInTable.indexOf(lines[codeIndex]), 2);
									}
									else{
										print("Error: Invalid input.");
										hasError = true;
										return;
									}
									codeIndex++;
									break;
						case "STG": dialogMessage = "Enter value to be assigned to ".concat(lines[codeIndex] + " (String) :");
									while(value == null || value.isEmpty() || value.length()==0){
								        value= JOptionPane.showInputDialog(dialogMessage);
								    }
									if(value.matches("^\\$.*\\$$")){
										value = value.substring(1, value.length()-1);
										variablesValues.put(lines[codeIndex],value);
										symbolTable.setValueAt(value, symbolsInTable.indexOf(lines[codeIndex]), 2);
									}
									else{
										print("Error: Invalid input.");
										hasError = true;
										return;
									}
									codeIndex++;
									break;
						default:	break;
					}
				}
				else{
					if(!variablesTypes.containsKey(lines[codeIndex])){
						print("Error: Variable name '".concat(lines[codeIndex] + "' was not declared."));
					}
					else{
						print("Error: No variable found where value will be assigned.");
					}
					hasError = true;
					return;	
				}
			}
		}
		return;

	}

	public void printingOperation(String[] lines){
		int ch = 0;
		if(!hasError){	
			if(lines[codeIndex].equals("PRT")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) && (variablesTypes.containsKey(lines[codeIndex]))){	
					lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
					
					switch(variablesTypes.get(lines[codeIndex])){
						case "INT": print(variablesValues.get(lines[codeIndex]));
									codeIndex++;
									break;
						case "BLN": print(variablesValues.get(lines[codeIndex]));
									codeIndex++;
									break;
						case "STG": print(variablesValues.get(lines[codeIndex]));
									// String toPrint = variablesValues.get(lines[codeIndex]);
									// print(toPrint.substring(1, toPrint.length()-1));
									codeIndex++;
									break;
						default:	break;
					}
				}
				else{
					if(lines[codeIndex].matches("^\\$.*\\$$")){
						String toPrint = lines[codeIndex].substring(1, lines[codeIndex].length()-1);
						print(toPrint);
						lexemeTable.addRow(new Object[]{toPrint, "String"});
						codeIndex++;
						return;
					}
					else if(lines[codeIndex].matches("^\\$.*")){
						String toPrint = lines[codeIndex].substring(1);
						codeIndex++;
						
						while((codeIndex < lines.length) && (!(lines[codeIndex].matches("^.*\\$$")))){
							toPrint = toPrint.concat(" " + lines[codeIndex] + " ");
							codeIndex++;
						}

						if(lines[codeIndex].matches("^.*\\$$") && (codeIndex < lines.length)){
							toPrint = toPrint.concat(" " + lines[codeIndex].substring(0,lines[codeIndex].length()-1) + " ");
							print(toPrint);

							lexemeTable.addRow(new Object[]{toPrint, "String"});
							codeIndex++;
							return;
						}
						else{
							print("Error: '$' as ending character of string not found.");
							hasError = true;
							return;
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						int x = -99999;
						Boolean temp = false;
						
						switch(lines[codeIndex]){
							case "ADD": x = add(lines);
										ch = 1;
										break;
							case "SUB": x = subtract(lines);
										ch = 1;
										break;
							case "MUL": x = multiply(lines);
										ch = 1;
										break;
							case "DIV": x = divide(lines);
										ch = 1;
										break;
							case "MOD": x = modulo(lines);
										ch = 1;
										break;
							case "GRT": temp = greaterThan(lines);
										ch = 2;
										break;
							case "GRE": temp = greaterThanEqual(lines);
										ch = 2;
										break;
							case "LET": temp = lessThan(lines);
										ch = 2;
										break;
							case "LEE": temp = lessThanEqual(lines);
										ch = 2;
										break;
							case "EQL": temp = equalOperation(lines);
										ch = 2;
										break;
							case "AND": temp = andOperation(lines);
										ch = 2;
										break;
							case "OHR": temp = orOperation(lines);
										ch = 2;
										break; 
							case "NON": temp = notOperation(lines);
										ch = 2;
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return;
						}

						if(ch==2){
							print(String.valueOf(temp));
						}
						else if(ch==1){
							print(String.valueOf(x));
						}
						else{
							print("Error found in PRT Operation.");
						}
					}
					else{
						print("Error: Invalid argument in PRT operation.");
						hasError = true;
						return;	
					}
				}
			}
		}
		return;

	}

	public int add(String[] lines){
		int value = 0;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("ADD")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for ADD operation.");
								hasError = true;
							}	
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for ADD operation.");
								hasError = true;
							}	
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for ADD operation.");
								hasError = true;
							}	
						}
					}
					else{
						print("Error: Invalid arguments for ADD operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for ADD operation.");
					hasError = true;
				}
			}
		}

		value = operand1 + operand2;
		return value;
	}

	public int subtract(String[] lines){
		int value = 0;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("SUB")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for SUB operation.");
								hasError = true;
							}	
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for SUB operation.");
								hasError = true;
							}	
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for SUB operation.");
								hasError = true;
							}	
						}
					}
					else{
						print("Error: Invalid arguments for SUB operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for SUB operation.");
					hasError = true;
				}
			}
		}

		value = operand1 - operand2;
		return value;
	}

	public int multiply(String[] lines){
		int value = 0;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("MUL")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for MUL operation.");
								hasError = true;
							}	
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for MUL operation.");
								hasError = true;
							}	
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for MUL operation.");
								hasError = true;
							}	
						}
					}
					else{
						print("Error: Invalid arguments for MUL operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for MUL operation.");
					hasError = true;
				}
			}
		}

		value = operand1 * operand2;
		return value;
	}

	public int divide(String[] lines){
		int value = 0;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("DIV")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for DIV operation.");
								hasError = true;
							}	
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for DIV operation.");
								hasError = true;
							}	
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for DIV operation.");
								hasError = true;
							}	
						}
					}
					else{
						print("Error: Invalid arguments for DIV operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for DIV operation.");
					hasError = true;
				}
			}
		}

		value = operand1 / operand2;
		return value;
	}

	public int modulo(String[] lines){
		int value = 0;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("ADD")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for MOD operation.");
								hasError = true;
							}	
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for MOD operation.");
								hasError = true;
							}	
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for MOD operation.");
								hasError = true;
							}	
						}
					}
					else{
						print("Error: Invalid arguments for MOD operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for MOD operation.");
					hasError = true;
				}
			}
		}

		value = operand1 % operand2;
		return value;
	}
	
	public Boolean greaterThan(String[] lines){
		Boolean value = false;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("GRT")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for GRT operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for GRT operation.");
							hasError = true;
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for GRT operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for GRT operation.");
							hasError = true;
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for GRT operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for GRT operation.");
							hasError = true;
						}
					}
					else{
						print("Error: Invalid arguments for GRT operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for GRT operation.");
					hasError = true;
				}
			}
		}

		if(operand1 > operand2){
			value = true;
		}else{
			value = false;
		}
		return value;
	}

	public Boolean greaterThanEqual(String[] lines){
		Boolean value = false;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("GRE")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for GRE operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for GRE operation.");
							hasError = true;
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for GRE operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for GRE operation.");
							hasError = true;
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for GRE operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for GRE operation.");
							hasError = true;
						}
					}
					else{
						print("Error: Invalid arguments for GRE operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for GRE operation.");
					hasError = true;
				}
			}
		}

		if(operand1 >= operand2){
			value = true;
		}else{
			value = false;
		}
		return value;
	}

	public Boolean lessThan(String[] lines){
		Boolean value = false;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("LET")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for LET operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for LET operation.");
							hasError = true;
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for LET operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for LET operation.");
							hasError = true;
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for LET operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for LET operation.");
							hasError = true;
						}
					}
					else{
						print("Error: Invalid arguments for LET operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for LET operation.");
					hasError = true;
				}
			}
		}

		if(operand1 < operand2){
			value = true;
		}else{
			value = false;
		}
		return value;
	}

	public Boolean lessThanEqual(String[] lines){
		Boolean value = false;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("LEE")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for LEE operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for LEE operation.");
							hasError = true;
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for LEE operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for LEE operation.");
							hasError = true;
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for LEE operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for LEE operation.");
							hasError = true;
						}
					}
					else{
						print("Error: Invalid arguments for LEE operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for LEE operation.");
					hasError = true;
				}
			}
		}

		if(operand1 <= operand2){
			value = true;
		}else{
			value = false;
		}
		return value;
	}

	public Boolean equalOperation(String[] lines){
		Boolean value = false;
		int operand1 = 0;
		int operand2 = 0;

		if(!hasError){
			if(lines[codeIndex].equals("EQL")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for EQL operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for EQL operation.");
							hasError = true;
						}
					}
					else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Integer.parseInt(lines[codeIndex]);
						codeIndex++;
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for EQL operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for EQL operation.");
							hasError = true;
						}
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "ADD": operand1 = add(lines);
										break;
							case "SUB": operand1 = subtract(lines);
										break;
							case "MUL": operand1 = multiply(lines);
										break;
							case "DIV": operand1 = divide(lines);
										break;
							case "MOD": operand1 = modulo(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}
						
						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("INT")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Integer.parseInt(variablesValues.get(lines[codeIndex]));
								codeIndex++;
							}
							else if(lines[codeIndex].matches("-?\\d+(\\.\\d+)?")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Integer.parseInt(lines[codeIndex]);
								codeIndex++;
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "ADD": operand2 = add(lines);
												break;
									case "SUB": operand2 = subtract(lines);
												break;
									case "MUL": operand2 = multiply(lines);
												break;
									case "DIV": operand2 = divide(lines);
												break;
									case "MOD": operand2 = modulo(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for EQL operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for EQL operation.");
							hasError = true;
						}
					}
					else{
						print("Error: Invalid arguments for EQL operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for EQL operation.");
					hasError = true;
				}
			}
		}

		if(operand1 == operand2){
			value = true;
		}else{
			value = false;
		}
		return value;
	}

	public Boolean andOperation(String[] lines){
		Boolean value = false;
		Boolean operand1 = false;
		Boolean operand2 = false;

		if(!hasError){
			if(lines[codeIndex].equals("AND")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("BLN")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("BLN")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
								codeIndex++;
								
							}
							else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Boolean.parseBoolean(lines[codeIndex]);
								codeIndex++;
								
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "GRT": operand2 = greaterThan(lines);
												break;
									case "GRE": operand2 = greaterThanEqual(lines);
												break;
									case "LET": operand2 = lessThan(lines);
												break;
									case "LEE": operand2 = lessThanEqual(lines);
												break;
									case "EQL": operand2 = equalOperation(lines);
												break;
									case "AND": operand2 = andOperation(lines);
												break;
									case "OHR": operand2 = orOperation(lines);
												break;
									case "NON": operand2 = notOperation(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for AND operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for AND operation.");
							hasError = true;
						}
						
					}
					else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Boolean.parseBoolean(lines[codeIndex]);
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("BLN")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
								codeIndex++;
								
							}
							else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Boolean.parseBoolean(lines[codeIndex]);
								codeIndex++;
								
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "GRT": operand2 = greaterThan(lines);
												break;
									case "GRE": operand2 = greaterThanEqual(lines);
												break;
									case "LET": operand2 = lessThan(lines);
												break;
									case "LEE": operand2 = lessThanEqual(lines);
												break;
									case "EQL": operand2 = equalOperation(lines);
												break;
									case "AND": operand2 = andOperation(lines);
												break;
									case "OHR": operand2 = orOperation(lines);
												break;
									case "NON": operand2 = notOperation(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for AND operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for AND operation.");
							hasError = true;
						}
						
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "GRT": operand1 = greaterThan(lines);
										break;
							case "GRE": operand1 = greaterThanEqual(lines);
										break;
							case "LET": operand1 = lessThan(lines);
										break;
							case "LEE": operand1 = lessThanEqual(lines);
										break;
							case "EQL": operand1 = equalOperation(lines);
										break;
							case "AND": operand1 = andOperation(lines);
										break;
							case "OHR": operand1 = orOperation(lines);
										break;
							case "NON": operand1 = notOperation(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("BLN")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
								codeIndex++;
								
							}
							else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Boolean.parseBoolean(lines[codeIndex]);
								codeIndex++;
								
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "GRT": operand2 = greaterThan(lines);
												break;
									case "GRE": operand2 = greaterThanEqual(lines);
												break;
									case "LET": operand2 = lessThan(lines);
												break;
									case "LEE": operand2 = lessThanEqual(lines);
												break;
									case "EQL": operand2 = equalOperation(lines);
												break;
									case "AND": operand2 = andOperation(lines);
												break;
									case "OHR": operand2 = orOperation(lines);
												break;
									case "NON": operand2 = notOperation(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for AND operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for AND operation.");
							hasError = true;
						}
					}
					else{
						print("Error: Invalid arguments for AND operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for AND operation.");
					hasError = true;
				}
			}
		}

		if(operand1 && operand2){
			value = true;
		}else{
			value = false;
		}
		return value;
	}

	public Boolean orOperation(String[] lines){
		Boolean value = false;
		Boolean operand1 = false;
		Boolean operand2 = false;

		if(!hasError){
			if(lines[codeIndex].equals("OHR")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("BLN")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("BLN")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
								codeIndex++;
								
							}
							else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Boolean.parseBoolean(lines[codeIndex]);
								codeIndex++;
								
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "GRT": operand2 = greaterThan(lines);
												break;
									case "GRE": operand2 = greaterThanEqual(lines);
												break;
									case "LET": operand2 = lessThan(lines);
												break;
									case "LEE": operand2 = lessThanEqual(lines);
												break;
									case "EQL": operand2 = equalOperation(lines);
												break;
									case "AND": operand2 = andOperation(lines);
												break;
									case "OHR": operand2 = orOperation(lines);
												break;
									case "NON": operand2 = notOperation(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for OHR operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for OHR operation.");
							hasError = true;
						}
						
					}
					else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Boolean.parseBoolean(lines[codeIndex]);
						codeIndex++;

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("BLN")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
								codeIndex++;
								
							}
							else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Boolean.parseBoolean(lines[codeIndex]);
								codeIndex++;
								
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "GRT": operand2 = greaterThan(lines);
												break;
									case "GRE": operand2 = greaterThanEqual(lines);
												break;
									case "LET": operand2 = lessThan(lines);
												break;
									case "LEE": operand2 = lessThanEqual(lines);
												break;
									case "EQL": operand2 = equalOperation(lines);
												break;
									case "AND": operand2 = andOperation(lines);
												break;
									case "OHR": operand2 = orOperation(lines);
												break;
									case "NON": operand2 = notOperation(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for OHR operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for OHR operation.");
							hasError = true;
						}
						
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "GRT": operand1 = greaterThan(lines);
										break;
							case "GRE": operand1 = greaterThanEqual(lines);
										break;
							case "LET": operand1 = lessThan(lines);
										break;
							case "LEE": operand1 = lessThanEqual(lines);
										break;
							case "EQL": operand1 = equalOperation(lines);
										break;
							case "AND": operand1 = andOperation(lines);
										break;
							case "OHR": operand1 = orOperation(lines);
										break;
							case "NON": operand1 = notOperation(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}

						if((codeIndex<lines.length) ){
							if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("BLN")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
								operand2 = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
								codeIndex++;
								
							}
							else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
								lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
								operand2 = Boolean.parseBoolean(lines[codeIndex]);
								codeIndex++;
								
							}
							else if(reservedWords.containsKey(lines[codeIndex])){
								switch(lines[codeIndex]){
									case "GRT": operand2 = greaterThan(lines);
												break;
									case "GRE": operand2 = greaterThanEqual(lines);
												break;
									case "LET": operand2 = lessThan(lines);
												break;
									case "LEE": operand2 = lessThanEqual(lines);
												break;
									case "EQL": operand2 = equalOperation(lines);
												break;
									case "AND": operand2 = andOperation(lines);
												break;
									case "OHR": operand2 = orOperation(lines);
												break;
									case "NON": operand2 = notOperation(lines);
												break;
									default: print("Error: Invalid Operator!");
											 hasError = true;
											 return value;
								}
							}
							else{
								print("Error: Invalid arguments for OHR operation.");
								hasError = true;
							}	
						}
						else{
							print("Error: Invalid arguments for OHR operation.");
							hasError = true;
						}
					}
					else{
						print("Error: Invalid arguments for OHR operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for OHR operation.");
					hasError = true;
				}
			}
		}

		if(operand1 || operand2){
			value = true;
		}else{
			value = false;
		}
		return value;
	}

	public Boolean notOperation(String[] lines){
		Boolean value = false;
		Boolean operand1 = false;
		Boolean operand2 = false;

		if(!hasError){
			if(lines[codeIndex].equals("NON")){
				lexemeTable.addRow(new Object[]{lines[codeIndex], reservedWords.get(lines[codeIndex])});
				codeIndex++;

				if((codeIndex<lines.length) ){
					if(variablesTypes.containsKey(lines[codeIndex]) && variablesTypes.get(lines[codeIndex]).equals("BLN")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Variable"});
						operand1 = Boolean.parseBoolean(variablesValues.get(lines[codeIndex]));
						codeIndex++;
						
					}
					else if(lines[codeIndex].equals("true") || lines[codeIndex].equals("false")){
						lexemeTable.addRow(new Object[]{lines[codeIndex], "Expression/Operand"});
						operand1 = Boolean.parseBoolean(lines[codeIndex]);
						codeIndex++;
						
					}
					else if(reservedWords.containsKey(lines[codeIndex])){
						switch(lines[codeIndex]){
							case "GRT": operand1 = greaterThan(lines);
										break;
							case "GRE": operand1 = greaterThanEqual(lines);
										break;
							case "LET": operand1 = lessThan(lines);
										break;
							case "LEE": operand1 = lessThanEqual(lines);
										break;
							case "EQL": operand1 = equalOperation(lines);
										break;
							case "AND": operand1 = andOperation(lines);
										break;
							case "OHR": operand1 = orOperation(lines);
										break;
							case "NON": operand1 = notOperation(lines);
										break;
							default: print("Error: Invalid Operator!");
									 hasError = true;
									 return value;
						}
					}
					else{
						print("Error: Invalid arguments for NON operation.");
						hasError = true;
					}	
				}
				else{
					print("Error: Invalid arguments for NON operation.");
					hasError = true;
				}
			}
		}

		value = !operand1;
		return value;
	}
}
