import java.io.IOException;
import java.util.Scanner;

import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;


public class RunStatechart {
	
	public static void main(String[] args) throws IOException {
		Scanner scanner;
		String input = "";
		
		try{
			scanner = new Scanner(System.in);
		} catch (Exception e){
			System.out.println("Exception thrown: " + e);
			return;
		}
		
		ExampleStatemachine s = new ExampleStatemachine();
		s.setTimer(new TimerService());
		RuntimeService.getInstance().registerStatemachine(s, 200);
		s.init();
		s.enter();
		s.runCycle();
		print(s);		

		while(scanner.hasNext()) {
			try {
				input = scanner.nextLine();
			} 
			catch (Exception e) {
				System.out.println("Exception thrown: " + e);
			}
			
			input = input.toLowerCase();
			switch(input) {			case "start":
				s.raiseStart();
				break;
			case "white":
				s.raiseStart();
				break;
			case "black":
				s.raiseStart();
				break;
			case "exit":
				scanner.close();
				System.exit(0);
			}
		
		s.runCycle();
		print(s);
		
		}	
	}

public static void print(IExampleStatemachine s){
	System.out.println("W = " + s.getSCInterface().getWhiteTime());
	System.out.println("B = " + s.getSCInterface().getBlackTime());
}