package com.bib.app.service;

import com.bib.app.controller.CohortCreateDTO;
import com.bib.app.dto.CohortDTO;
import com.bib.app.dto.UserDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface ICohortService {
    /**
     * Adds a new cohort based on the provided CohortCreateDTO.
     *
     * @param createDTO The DTO containing cohort creation data.
     * @return The created CohortDTO.
     * @throws RuntimeException if the associated project is not found.
     */
    CohortDTO add(@Valid CohortCreateDTO createDTO);

    /**
     * Deletes a cohort by its ID and returns the deleted cohort.
     *
     * @param cohortId The ID of the cohort to delete.
     * @return The deleted CohortDTO.
     * @throws RuntimeException if the cohort is not found.
     */
    CohortDTO deleteOne(Long cohortId);

    /**
     * Deletes all cohorts.
     */
    void deleteAllCohorts();

    /**
     * Retrieves a single cohort by its ID.
     *
     * @param id The ID of the cohort to retrieve.
     * @return The CohortDTO.
     * @throws RuntimeException if the cohort is not found.
     */
    CohortDTO getOneCohort(Long id);

    /**
     * Retrieves all cohorts.
     *
     * @return A list of CohortDTOs.
     */
    List<CohortDTO> getAllCohorts();

    /**
     * Retrieves cohorts associated with a specific project.
     *
     * @param projectId The ID of the project.
     * @return A list of CohortDTOs.
     * @throws RuntimeException if the project is not found.
     */
    List<CohortDTO> searchByProject(Long projectId);

    /**
     * Retrieves users associated with a specific cohort.
     *
     * @param cohortId The ID of the cohort.
     * @return A list of UserDTOs.
     * @throws RuntimeException if the cohort is not found.
     */
    List<UserDTO> getUsersByCohortId(Long cohortId);
}