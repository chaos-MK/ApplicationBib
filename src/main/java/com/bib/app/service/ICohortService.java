package com.bib.app.service;

import com.bib.app.entities.Cohort;
import com.bib.app.entities.User;


import java.util.List;

public interface ICohortService {

	Cohort add(Cohort cohort);
	Cohort Deleteone(Long cohortId);
	List<User> getUsersByCohortId(Long cohortId);
	 List<Cohort> searchByProject(Long projectId);
	 void deleteAllCohorts();
	 Cohort getOneCohort(Long id) ;
	 List<Cohort> getAllCohort() ;
}