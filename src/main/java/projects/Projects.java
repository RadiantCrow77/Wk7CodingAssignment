package projects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.dao.DbConnection;
import projects.exception.DbException;
import projects.service.ProjectService;

public class Projects {

	private ProjectService projectService = new ProjectService();
	private Scanner scanner = new Scanner(System.in);
	private Project curProject;

	// @ formatter:off
	private List<String> operations = List.of("1) Add a Project", 
			"2) List projects",
			"3) Select project by Project ID",
			"4) Update project details",
			"5) Delete a project");
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
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				case 4:
					updateProjectDetails();
					break;
				case 5:
					deleteProject();
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

	private void deleteProject() {
	listProjects();
	Integer projectId = getIntInput("Please enter Project ID to delete. ");
	projectService.deleteProject(projectId);
	
	System.out.println("Project w/ ID:  "+projectId + " was deleted succesfully.");
	
	if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
		curProject = null;
	}
	
	}

	private void updateProjectDetails() {
	if (Objects.isNull(curProject)){ // if current project is null, prompt user to select one
		System.out.println("\nPlease select a project.");
		return;
	}
	// WK 11 - start
	// WK 11: for each field in the Project obj, print msg along w/ current setting in curProject
			String projectName = getStringInput("Enter the Project name. ["+ curProject.getProjectName()+"]"); // added curProject getter wk 11
			BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours. ["+curProject.getEstimatedHours()+"]");
			BigDecimal actualHours = getDecimalInput("Enter the actual hours. ["+curProject.getActualHours()+"]");
			Integer difficulty = getIntInput("Enter the project difficulty, scale from 1-5. ["+curProject.getDifficulty()+"]");
			String notes = getStringInput("Enter any notes for the project. ["+curProject.getNotes()+"]");

			
			Project project = new Project(); // new project object :)
	
			
			
			// WK 11: steps c-g on pg 14
			project.setProjectId(curProject.getProjectId());
			
			// WK 11 other setters
			// WK 11: create new project obj, if user input != null, add value to project object, if null, add value from curProject, repeat for all proj variables
			project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
			project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
			project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
			project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
			project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
			
			
			projectService.modifyProjectDetails(project);
			
			curProject = projectService.fetchProjectById(curProject.getProjectId());
	}


	


	private void listProjects() { // option 2
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects:");

		// print each at a time
		projects.forEach(
				project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
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
		
		// print current project when available menu selections are displayed to user
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		}else {
			System.out.println("\nYou are working with project: " + curProject);
		}
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

		System.out.println("You have successfully created project: " + dbProject);
		curProject = projectService.fetchProjectById(dbProject.getProjectId()); // 
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

	// select project method
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Please enter a project ID to select a project.");

		// unselect current project by assigning null
		curProject = null;

		// throws an exception if invalid proj ID is entered
		curProject = projectService.fetchProjectById(projectId);
	}

	public static void main(String[] args) {
		// 3. in the main method, create a new ProjectsApp obj and call the method
		// processUserSelections,
		// the method takes zero parameters, returns nothing
		new Projects().processUserSelections();

	} // end main

} // end this file
