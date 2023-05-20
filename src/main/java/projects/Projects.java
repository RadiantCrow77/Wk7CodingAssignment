package projects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.dao.DbConnection;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class Projects {

	private ProjectService projectService = new ProjectService();
	private Scanner scanner = new Scanner(System.in);

	// @ formatter:off
	private List<String> operations = List.of("1) Add a Project");
	// @formatter:on

	private void processUserSelections() {
		boolean done = false;

		while (!done) {// while not done
			try {
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Please try again.");
			}
		}
	}



	private boolean exitMenu() {
		System.out.println("\nExiting the menu.");
		return true;
	}

	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Please enter a menu selection.");

		return Objects.isNull(input) ? -1 : input; // check if input is null, -1 will exit menu app
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		// 7. b. Test if input is null, if so, return null
		if (Objects.isNull(input)) {
			return null;
		}

		// 7. c. Try-catch block to test that getStringInput() returned val can be
		// converted to Integer
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) { // c. iii. catch: throw with invalid msg
			throw new DbException(input + " is not a valid number. Please choose a valid #.");
		}
	}

	// 8. getStringInput()
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");

		String input = scanner.nextLine();
		return input.isBlank() ? null : input.trim();
	}

	private void printOperations() {
		System.out.println("\nThese are the available selections. Press Enter to quit: ");
		// print all available menu options:
		operations.forEach(line -> System.out.println(" " + line));
	}
	
	private void createProject() {
		String projectName = getStringInput("Enter the Project name.");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours.");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours.");
		Integer difficulty = getIntInput("Enter the project difficulty, scale from 1-5.");
		String notes = getStringInput("Enter any notes for the project.");
		
		Project project = new Project(); // new project object :)
		
		// setters for each
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		// call the addProject() method on projectService obj, pass it Project obj
		Project dbProject = projectService.addProject(project);
		
		System.out.println("You have successfully created project: "+dbProject);
	}
	

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);


		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) { // c. iii. catch: throw with invalid msg
			throw new DbException(input + " is not a valid number. Please choose a valid #.");
		}
	}



	public static void main(String[] args) {
		// 3. in the main method, create a new ProjectsApp obj and call the method
		// processUserSelections,
		// the method takes zero parameters, returns nothing
		new Projects().processUserSelections();

	} // end main

} // end this file
