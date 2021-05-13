package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PhoneticOwnerRepository extends CrudRepository<PhoneticOwner, Integer>{

	@Transactional(readOnly = true)
	List<PhoneticOwner> findByLastName(String lastName);
}
