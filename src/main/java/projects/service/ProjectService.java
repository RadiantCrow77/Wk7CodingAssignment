package projects.service;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	private ProjectDao projectDao = new ProjectDao();
	
	public Project addProject(Project project) {
		// call insertProject() on projectDao obj, pass in Project param, return value from method
		return projectDao.insertProject(project);
	}

}
