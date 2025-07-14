package com.bib.app.service;

import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Project;

import java.util.List;

public interface IProjectService {
    Project add(Project project);
    Project updateProject(Project project);
    Project deleteOne(Long id);
    void deleteAllProjects();
    ProjectDTO getOneProject(Long id);
    List<ProjectDTO> getAllProject();
    List<Project> getProjectsByCompanyId(Long companyId);
}