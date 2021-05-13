package org.springframework.samples.petclinic.owner;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.samples.petclinic.model.Person;

/**
 * Simple JavaBean object representing the phonetic names of an owner.
 *
 * @author Gustavo Rocha
 * 
 */

@Entity
@Table(name = "phoneticOwners")
public class PhoneticOwner extends Person{

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Owner owner;

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}
}
