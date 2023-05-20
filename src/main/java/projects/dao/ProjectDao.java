package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
// Table Name Constraints
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	// Project Details
	public Project insertProject(Project project) {
// SQL STATEMENTS
		//@formatter:off
		String sql = ""
				+ "INSERT INTO "+ PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+"VALUES "
				+"(?, ?, ?, ?, ?)";
		//@formatter:on
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				// setting project details
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				// save details by calling executeUpdate on PreparedStatement obj
				stmt.executeUpdate();
				
				// Obtain PK by calling conveneience method
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				
				// Commit transaction
				commitTransaction(conn);
				
				// set projectId on Project obj
				project.setProjectId(projectId);
				
				// return the PK
				return project;
			}
				catch(Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
				}
			}
			catch(SQLException e) {
				throw new DbException(e);
			}
		}
	}


