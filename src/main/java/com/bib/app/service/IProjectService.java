package com.bib.app.service;


import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Cohort;
import com.bib.app.entities.Project;
import org.springframework.security.access.method.P;

import java.util.List;

public interface IProjectService {

    Project add(Project project);
    Project Deleteone(Long projectId);
    void deleteAllProjects();
    ProjectDTO getOneProject(Long id);
    List<ProjectDTO> getAllProject();
   /* Project getOneProject (Long id) ;
    List<Project> getAllProject() ; */

}
