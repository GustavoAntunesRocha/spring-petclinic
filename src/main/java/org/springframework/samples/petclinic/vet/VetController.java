/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@RequestMapping("/vets")
class VetController {

	private static final String VIEWS_VETS_CREATE_OR_UPDATE_FORM = "vets/createOrUpdateVetForm";
	
	private final VetRepository vets;
	private final SpecialtyRepository specialties;

	public VetController(VetRepository clinicService, SpecialtyRepository specialties) {
		this.vets = clinicService;
		this.specialties = specialties;
	}
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@GetMapping("")
	public String showVetList(Map<String, Object> model) {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for Object-Xml mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vets.findAll());
		model.put("vets", vets);
		return "vets/vetList";
	}
	
	/**
	 * Here we add the atribute vet of type Vet
	 * @param model
	 * @return
	 */
	@ModelAttribute("vet")
	public Vet loadVetWithSpecialty(Map<String, Object> model) {
		List <Specialty> specialties = (List<Specialty>) this.specialties.findAll();
		
		model.put("specialties", specialties);
		Vet vet = new Vet();
		return vet;
	}
	
	@GetMapping("/new")
	public String initCreationForm(Map<String, Object> model) {
		return VIEWS_VETS_CREATE_OR_UPDATE_FORM;
	}
	
	@PostMapping("")
	public String save(@Valid Vet vet, BindingResult result, Map<String, Object> model, @RequestParam("specialties") List<Specialty> specialties) {
		if (result.hasErrors()) {
			model.put("vet", vet);
			return VIEWS_VETS_CREATE_OR_UPDATE_FORM;
		}
		else {
			for(Specialty specialty: specialties) {
				vet.addSpecialty(specialty);
			}
			this.vets.save(vet);
			return "redirect:/vets";
		}
	}
}
