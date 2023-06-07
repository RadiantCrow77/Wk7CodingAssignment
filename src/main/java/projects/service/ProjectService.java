package projects.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.NoSuchElementException;

import projects.Projects;
import projects.dao.DbConnection;
import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	private ProjectDao projectDao = new ProjectDao();
	
	public Project addProject(Project project) {
		// call insertProject() on projectDao obj, pass in Project param, return value from method
		return projectDao.insertProject(project);
	}

	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	
	}

	public Project fetchProjectById(Integer projectId) {
	    return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
	        "Project with project ID=" + projectId + " does not exist."));

	  }

	// week 11 method
	public void modifyProjectDetails(Project project) {
		// call projectDao.modifyProjectDetails()
		if(!projectDao.modifyProjectDetails(project)) { // this method gives boolean that indicates U was succesful, if false, DNE
			throw new DbException("Project with ID="+project.getProjectId()+"does not exist. ");
		}
	

	}

	public void deleteProject(Integer projectId) {
		// very similar to modifyProjectDetails
		// call deleteProject in DAO, pass ID as param
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID = "+ projectId + "does not exist. Try again.");
		}
		// return a bool
		
		
		// if return false, throw an exception
		
	}
	

}
