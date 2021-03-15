package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.Scanner;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.base.types.Direction;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		
		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				
				
				
				//Finding unnamed states
				if (state.getName() == null || state.getName() == "") {
					System.out.println("Unnamed state: " + state.getDocumentation());
					System.out.println("Name recommended: State" + state.hashCode());
				}
				
				//Finding trap states
				if (state.getOutgoingTransitions().size() == 0) {
					System.out.println("TRAP! " + state.getName());
				} else {
					System.out.println(state.getName());
				}
				//Reading and printing transitions
			} else if (content instanceof Transition) {
				Transition transition = (Transition) content;
				System.out.println(transition.getSource().getName() + " -> " + transition.getTarget().getName());
				
				//Reading and printing variable definitions
			} else if (content instanceof VariableDefinition) {
				VariableDefinition variableDefinition = (VariableDefinition) content;
				System.out.println(variableDefinition.getName());
				
				//Reading and printing incoming events
			} else if (content instanceof EventDefinition) {
				EventDefinition eventDefinition = (EventDefinition) content;
				if (eventDefinition.getDirection() == Direction.IN) {
					System.out.println(eventDefinition.getName());
				}
			}
		}
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	
		writeJavaFile();
	}
	
	public static void writeJavaFile() {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		String headerStr = new String(
				"import java.io.IOException;\r\n" + 
				"import java.util.Scanner;\r\n" + 
				"\r\n" + 
				"import hu.bme.mit.yakindu.analysis.RuntimeService;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.TimerService;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;\r\n\r\n\r\n");
		String mainFuncStr = new String(
				"public class RunStatechart {\r\n" + 
				"	\r\n" + 
				"	public static void main(String[] args) throws IOException {\r\n" + 
				"		Scanner scanner;\r\n" + 
				"		String input = \"\";\r\n" + 
				"		\r\n" + 
				"		try{\r\n" + 
				"			scanner = new Scanner(System.in);\r\n" + 
				"		} catch (Exception e){\r\n" + 
				"			System.out.println(\"Exception thrown: \" + e);\r\n" + 
				"			return;\r\n" + 
				"		}\r\n" + 
				"		\r\n" + 
				"		ExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"		s.setTimer(new TimerService());\r\n" + 
				"		RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" + 
				"		s.init();\r\n" + 
				"		s.enter();\r\n" + 
				"		s.runCycle();\r\n" + 
				"		print(s);		\r\n\r\n" + 
				"		while(scanner.hasNext()) {\r\n" + 
				"			try {\r\n" + 
				"				input = scanner.nextLine();\r\n" + 
				"			} \r\n" + 
				"			catch (Exception e) {\r\n" + 
				"				System.out.println(\"Exception thrown: \" + e);\r\n" + 
				"			}\r\n" + 
				"			\r\n" + 
				"			input = input.toLowerCase();\r\n" + 
				"			switch(input) {\r\nS");
		String switchStr = new String ("");
		String printFunctionStr = new String("public static void print(IExampleStatemachine s){\n");
		String temp;
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		
		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if (content instanceof VariableDefinition) {
				VariableDefinition variableDefinition = (VariableDefinition) content;
				temp = variableDefinition.getName();
				printFunctionStr = printFunctionStr.concat("\tSystem.out.println(\"" + temp.substring(0, 1).toUpperCase() + " = \" + s.getSCInterface().get" + temp.substring(0, 1).toUpperCase() + temp.substring(1) + "());\n");
			} else if (content instanceof EventDefinition) {
				EventDefinition eventDefinition = (EventDefinition) content;
				if (eventDefinition.getDirection() == Direction.IN) {
					switchStr = switchStr + "			case \"" + eventDefinition.getName() + "\":\r\n" + 
							"				s.raiseStart();\r\n" + 
							"				break;\r\n";
				}
			}
		}
		
		mainFuncStr = mainFuncStr.concat(switchStr.concat(
				"			case \"exit\":\r\n" + 
				"				scanner.close();\r\n" + 
				"				System.exit(0);\r\n" + 
				"			}\r\n" + 
				"		\r\n" + 
				"		s.runCycle();\r\n" + 
				"		print(s);\r\n" + 
				"		\r\n" + 
				"		}	\r\n" + 
				"	}\r\n\r\n"
				));
		
		printFunctionStr = printFunctionStr.concat("}");
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		String fileStr = headerStr.concat(mainFuncStr).concat(printFunctionStr);
		manager.saveFile("model_output/generatedClass.java", fileStr);
		System.out.print("Excercise 4.:\n" + fileStr);
	}
}
