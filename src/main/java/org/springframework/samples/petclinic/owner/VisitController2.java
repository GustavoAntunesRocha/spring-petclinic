package org.springframework.samples.petclinic.owner;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VisitController2 {

	@Autowired
	private VisitRepository visits;

	@PostMapping("/visit/delete/{visitId}/{ownerId}")
	public String processDeleteVisit(@PathVariable("visitId") int visitId, @PathVariable("ownerId") int ownerId, HttpServletResponse httpResponse) throws IOException {
		//Visit visit = this.visits.findById(visitId).get();
		this.visits.delete(visitId);
		httpResponse.sendRedirect("/owners/"+ownerId);
		return null;
	}
}
