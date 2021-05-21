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
package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mtfn.MetaphonePtBr;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
@RequestMapping(value="/owners")
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final OwnerRepository owners;

	private VisitRepository visits;
	
	private final PhoneticOwnerRepository phoneticOwners;

	public OwnerController(OwnerRepository clinicService, VisitRepository visits, PhoneticOwnerRepository phoneticOwners) {
		this.owners = clinicService;
		this.visits = visits;
		this.phoneticOwners = phoneticOwners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@GetMapping("/new")
	public String initCreationForm(Map<String, Object> model) {
		Owner owner = new Owner();
		model.put("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		else {
			PhoneticOwner phoneticOwner = new PhoneticOwner();
			phoneticOwner.setFirstName(new MetaphonePtBr(owner.getFirstName()).toString());
			phoneticOwner.setLastName(new MetaphonePtBr(owner.getLastName()).toString());
			phoneticOwner.setOwner(owner);
			List<PhoneticOwner> phoneticOwners = new ArrayList<PhoneticOwner>();
			phoneticOwners.add(phoneticOwner);
			owner.setPhoneticOwner(phoneticOwners);
			this.owners.save(owner);
			return "redirect:/owners/" + owner.getId();
		}
	}
	
	
	@RequestMapping(value="/{ownerId}/delete", method=RequestMethod.POST)
	public String processOwnerDelete(@PathVariable("ownerId") int ownerId, RedirectAttributes redirectAttributes) {
		Owner owner = this.owners.findById(ownerId).get();
		if(!owner.getPets().isEmpty()) {
			redirectAttributes.addFlashAttribute("error","Owner cannot be deleted because it has pets!");
			return "redirect:/owners/{ownerId}";
		}
		this.owners.deleteById(ownerId);
		redirectAttributes.addFlashAttribute("success","Owner deleted!");
		return "redirect:/owners/find";
	}

	@GetMapping("/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("owner", new Owner());
		return "owners/findOwners";
	}

	@GetMapping("")
	public String processFindForm(Owner owner, BindingResult result, Map<String, Object> model) {

		// allow parameterless GET request for /owners to return all records
		if (owner.getLastName() == null) {
			owner.setLastName(""); // empty string signifies broadest possible search
		}
		Collection<Owner> results = new ArrayList<Owner>();
		String phoneticName = new MetaphonePtBr(owner.getLastName()).toString();
		List<PhoneticOwner> phoneticOwners = this.phoneticOwners.findByLastName(phoneticName);
		for(PhoneticOwner phoneticOwner : phoneticOwners) {
			results.add(this.owners.findById(phoneticOwner.getId()).get());
		}
		if (results.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}
		else if (results.size() == 1) {
			// 1 owner found
			owner = results.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}
		else {
			// multiple owners found
			model.put("selections", results);
			return "owners/ownersList";
		}
	}

	@GetMapping("/{ownerId}/edit")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = this.owners.findById(ownerId).get();
		model.addAttribute(owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
			@PathVariable("ownerId") int ownerId) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		else {
			owner.setId(ownerId);
			this.owners.save(owner);
			return "redirect:/owners/{ownerId}";
		}
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Owner owner = this.owners.findById(ownerId).get();
		for (Pet pet : owner.getPets()) {
			pet.setVisitsInternal(visits.findByPet(pet));
		}
		mav.addObject(owner);
		return mav;
	}

}
