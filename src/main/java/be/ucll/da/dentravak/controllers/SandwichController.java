package be.ucll.da.dentravak.controllers;

import be.ucll.da.dentravak.model.Sandwich;
import be.ucll.da.dentravak.model.SandwichPreferences;
import be.ucll.da.dentravak.repositories.SandwichRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.naming.ServiceUnavailableException;
import java.net.URISyntaxException;
import java.util.*;


import javax.naming.ServiceUnavailableException;
import java.net.URI;

@RestController
public class SandwichController {


    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private SandwichRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/sandwiches")
    public Iterable<Sandwich> sandwiches() {

        try {
            SandwichPreferences preferences = getPreferences("nummer");
            Iterable<Sandwich> allSandwiches = repository.findAll();
            List<Sandwich> sandwichList = (List<Sandwich>) allSandwiches;
            List<Sandwich> sandwichesSorted = sortSandwiches(preferences, sandwichList);
            return sandwichesSorted;
        } catch (ServiceUnavailableException e) {
            return repository.findAll();
        } catch (NullPointerException e) {
            return repository.findAll();
        }

    }


    List<Sandwich> sortSandwiches(SandwichPreferences preferences, List<Sandwich> sandwiches) {
        Collections.sort(sandwiches, new Comparator<Sandwich>() {
            @Override
            public int compare(Sandwich o1, Sandwich o2) {
                if (preferences.getRatingForSandwich(o1.getId()) == null && preferences.getRatingForSandwich(o2.getId()) == null) {
                    return 0;
                }
                if (preferences.getRatingForSandwich(o1.getId()) == null) {
                    return 1;
                }
                if (preferences.getRatingForSandwich(o2.getId()) == null) {
                    return -1;
                }
                return preferences.getRatingForSandwich(o2.getId()).compareTo(preferences.getRatingForSandwich(o1.getId()));
            }});
        return sandwiches;
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

    // why comment: for testing
    @GetMapping("/getpreferences/{emailAddress}")
    public SandwichPreferences getPreferences(@PathVariable String emailAddress) throws RestClientException, ServiceUnavailableException {
        URI service = recommendationServiceUrl()
                .map(s -> s.resolve("/recommendation/recommend/" + emailAddress))                .orElseThrow(ServiceUnavailableException::new);
        return restTemplate
                .getForEntity(service, SandwichPreferences.class)
                .getBody();
    }

//    public Optional<URI> recommendationServiceUrl() {
//        return discoveryClient.getInstances("recommendation")
//                .stream()
//                .map(si -> si.getUri())
//                .findFirst();
//    }

    public Optional<URI> recommendationServiceUrl() {
        return discoveryClient.getInstances("recommendation")
                .stream()
                .map(si -> si.getUri())
                .findFirst();
    }
}