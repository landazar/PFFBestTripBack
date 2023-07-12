package com.inti.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inti.model.Activite;
import com.inti.model.Experiences;
import com.inti.repository.ActiviteRepository;
import com.inti.repository.IExperiencesRepository;
import com.inti.repository.IUtilisateurRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
public class ExperiencesController {
	
	@Autowired
	IExperiencesRepository ier;
	@Autowired
	ActiviteRepository activiteRepository;
	@Autowired
	IUtilisateurRepository iur;
	
	
	
//	@GetMapping("listeExperiences")
//	public List<Experiences> listeExperiences() {
//		return ier.findAll();
//	}
	
	/////////////////////////////////////////////
	
	@GetMapping("listeExperiences/{type}")
	public List<Experiences> listeExperiences(@PathVariable("type") String type) {
		if(type.contentEquals("undefined")) {
			return ier.findAll();
		}
		else {
			return ier.getExperiencesByType(type);
		}
		
	}
	
	//////////////////////////////////////////////////
	
	@GetMapping("getExperiencesById/{idExperience}")
	public Experiences getExperiencesById(@PathVariable("idExperience") int idExperience) {
		System.out.println(ier.getReferenceById(idExperience));
		System.out.println(ier.getReferenceById(idExperience).getActivites());
		
		return ier.getReferenceById(idExperience);
	}
	
	
	@PostMapping("saveExperiences")
	public Experiences saveExperiences(@RequestBody Experiences experiences, @RequestParam("username") String username) {
		Experiences savedExperiences = ier.save(experiences);
		
		System.out.println("Username : " + username);
		
		// ier.insertIdUtilisateur(iur.getIdByUsername(username).get(0), savedExperiences.getIdExperience());

    List<Activite> activites = experiences.getActivites();
    for (Activite activite : activites) {
        activite.setExperience(savedExperiences);

        activiteRepository.save(activite);
    }

    return savedExperiences;
	}
	
	@PutMapping("updateExperiences")
	public boolean updateExperiences(@RequestBody Experiences experiences, @RequestParam("username") String username) {


		if(ier.getReferenceById(experiences.getIdExperience()) != null) {

			ier.save(experiences);
			
			ier.insertIdUtilisateur(iur.getIdByUsername(username).get(0), ier.save(experiences).getIdExperience());

			Experiences savedExperiences = ier.save(experiences);
			List<Activite> activites = experiences.getActivites();
	    for (Activite activite : activites) {
	        activite.setExperience(savedExperiences);

	        activiteRepository.save(activite);
	    }

			return true;
		}
		
		return false;
	}
	
	@DeleteMapping("deleteExperiences/{idExperience}")
	public boolean deleteExperiences(@PathVariable("idExperience") int idExperience) {
	    try {
	        Experiences experience = ier.findById(idExperience).get();
	        
	        for (Activite activite : experience.getActivites()) {
	            activiteRepository.delete(activite);
	        }

	        ier.delete(experience);
	        
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	@GetMapping("getUsernameById/{idExperience}")
	public String getUsernameById(@PathVariable("idExperience") int idExperience)
	{
		
		return iur.getUsernameById(ier.getIdUtilisateurByIdExp(idExperience));
		
	}
	
	
	
	

}
