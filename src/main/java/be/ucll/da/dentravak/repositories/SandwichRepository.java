package be.ucll.da.dentravak.repositories;

import be.ucll.da.dentravak.model.Sandwich;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SandwichRepository extends CrudRepository<Sandwich, UUID> {
    List<Sandwich> findByName(String name);
}
