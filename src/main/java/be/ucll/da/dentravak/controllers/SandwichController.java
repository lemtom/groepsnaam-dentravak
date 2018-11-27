package be.ucll.da.dentravak.controllers;

import be.ucll.da.dentravak.model.Sandwich;
import be.ucll.da.dentravak.repositories.SandwichRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class SandwichController {

    private SandwichRepository repository;

    public SandwichController(SandwichRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/sandwiches")
    public Iterable<Sandwich> sandwiches() {
        return repository.findAll();
    }

    @RequestMapping(value = "/sandwiches", method = RequestMethod.POST)
    public Sandwich createSandwich(@RequestBody Sandwich sandwich) {
        return repository.save(sandwich);
    }


    @RequestMapping(value = "/sandwiches/{id}", method = RequestMethod.GET)
    public Sandwich getSandwich(@PathVariable UUID id){
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException(id + "not found"));
    }

    @RequestMapping(value = "/sandwiches/{id}", method = RequestMethod.PUT)
    public Sandwich updateSandwich(@RequestBody Sandwich newSandwich, @PathVariable UUID id){
        return repository.findById(id).map(sandwich -> {
            sandwich.setName(newSandwich.getName());
            sandwich.setIngredients(newSandwich.getIngredients());
            sandwich.setPrice(newSandwich.getPrice());
            return repository.save(sandwich);
        }).orElseGet(() -> {
            newSandwich.setId(id);
            return repository.save(newSandwich);
        });
    }
}
