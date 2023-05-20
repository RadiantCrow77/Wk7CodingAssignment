/*Delete tables if run, and recreate -- start from related tables, primary last*/
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project;

/*Create table statements -- start with primary, related last*/
CREATE TABLE project (
project_id INT AUTO_INCREMENT NOT NULL, 
project_name VARCHAR(128) NOT NULL, 
estimated_hours DECIMAL(7,2),
actual_hours DECIMAL(7,2),
difficulty INT,
notes TEXT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (project_id)
);

CREATE TABLE category(
category_id INT AUTO_INCREMENT NOT NULL,
category_name VARCHAR(128) NOT NULL,
PRIMARY KEY (category_id)
);

CREATE TABLE project_category(
project_id INT NOT NULL,
category_id INT NOT NULL,
FOREIGN KEY  (project_id) REFERENCES project (project_id) ON DELETE CASCADE,
FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
UNIQUE KEY (project_id, category_id)
);

CREATE TABLE step(
step_id INT AUTO_INCREMENT NOT NULL,
project_id INT NOT NULL,
step_text TEXT NOT NULL,
step_order INT NOT NULL,
PRIMARY KEY (step_id),
FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE material(
material_id INT AUTO_INCREMENT NOT NULL,
project_id INT NOT NULL,
material_name VARCHAR(128) NOT NULL,
num_required INT,
cost DECIMAL(7,2),
PRIMARY KEY(material_id),
FOREIGN key (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);