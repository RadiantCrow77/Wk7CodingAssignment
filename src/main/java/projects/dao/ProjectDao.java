package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
// Table Name Constraints
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "PROJECT";
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

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public List<Project> fetchAllProjects() {
		// 1.a SQL stmt to return all projects, !materials, steps, categories.. order by
		// project name
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

		// 1.b try-with-resource to obtain connection obj
		try (Connection conn = DbConnection.getConnection()) {
			// 1.c inside try block, start transaction
			startTransaction(conn);

			// try-w-resource
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();

					while (rs.next()) {
						// 1.g extract method assigns each result row to a new project obj, adds to the
						// list just created
						projects.add(extract(rs, Project.class));
					}
//					return list of Projects
					return projects;
				}

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		// stmt returns all cols from table in row that matches project_id
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

		// obtain connection object with try-w-resource statment
		try (Connection conn = DbConnection.getConnection()) {
			// 1.c inside try block, start transaction
			startTransaction(conn);

			// try-w-resource
			try {
				Project project = null; // no project pre-selected

				try (PreparedStatement stmt = conn.prepareStatement(sql)) { // obtain preparedstmt from conn object in a
																			// try-w-resource
					setParameter(stmt, 1, projectId, Integer.class);

					try (ResultSet rs = stmt.executeQuery()) { // a.g. Obtain ResultSet in try-w-resource

						if (rs.next()) { // if has a row in it...
							project = extract(rs, Project.class); // set Project to a new Project obj, set all fields
																	// from values in rs
						}
					}
				}
				if (Objects.nonNull(project)) { // return project object as Optional object
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId)); // obtain list of
																								// materials...
					project.getSteps().addAll(fetchStepsForProject(conn, projectId)); // steps, and...
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId)); // categories
				}

				commitTransaction(conn); // 1.i commit the transaction
				return Optional.ofNullable(project);
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	// Methods to Obtain materials, steps, and categories
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException { // note:
																												// for
																												// these
																												// 3
																												// methods,
																												// since
																												// conn
																												// is
																												// passed
																												// in,
																												// no
																												// need
																												// to
																												// obtain
																												// connection
																												// from
																												// dbconn
	// @formatter:off
		String sql = ""+
	" SELECT c.* FROM " + CATEGORY_TABLE + " c "
	+ " JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
	+ " WHERE project_id = ?"; // joins tbl so a little different
	//@formatter:on
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}
		}
	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = ""+
	"SELECT * FROM " + STEP_TABLE +  " WHERE project_id = ?";
	//@formatter:on
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = ""+
	"SELECT * FROM " + MATERIAL_TABLE +  " WHERE project_id = ?";
	//@formatter:on
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();

				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				return materials;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
		// @formatter:off
		String sql = ""
				+"UPDATE "+ PROJECT_TABLE + " SET "
				+"project_name = ?, "
				+"estimated_hours = ?, "
				+"actual_hours = ?, "
				+"difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?"; 
		// @formatter:on

		try (Connection conn = DbConnection.getConnection()) { // obtain conneciton
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				// Wk 11: 3: set all parameters on PeparedStatement...
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);

				boolean modified = stmt.executeUpdate() == 1; // ...call execute update, check if 1, save to variable
				commitTransaction(conn); // commit transaction

				return modified; // return result from executeUpdate() as a bool
			} 
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} 
		catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectId) {
		// SQL Delete stmt
		
	String sql = "DELETE FROM "+ PROJECT_TABLE +" WHERE project_id = ?";
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn); // start transaction
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				
				setParameter(stmt, 1, projectId, Integer.class); // set project ID in preparedstmt
				
				boolean deleted = stmt.executeUpdate() == 1; // return true if execute returns 1
				
				commitTransaction(conn); // commit
				
				return deleted;
			}
			catch(Exception e) {
				rollbackTransaction(conn); // rollback 
				throw new DbException(e);
			}
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
	}
}