package be.ucll.da.dentravak.controllers;

import be.ucll.da.dentravak.model.Sandwich;
import be.ucll.da.dentravak.model.SandwichPreferences;
import be.ucll.da.dentravak.repositories.SandwichRepository;
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


    @Inject
    private DiscoveryClient discoveryClient;
    @Inject
    private SandwichRepository repository;

    @Inject
    private RestTemplate restTemplate;

    @RequestMapping("/sandwiches")
    public Iterable<Sandwich> sandwiches() {

        try {
            SandwichPreferences preferences = getPreferences("nummer");
            Iterable<Sandwich> allSandwiches = repository.findAll();
            List<Sandwich> sandwiches = new ArrayList<>();
            for (Sandwich s : allSandwiches) {
                sandwiches.add(s);
            };

            Collections.sort(sandwiches, new Comparator<Sandwich>() {
                @Override
                public int compare(Sandwich s2, Sandwich s1)
                {
                    Float rating1 = preferences.getRatingForSandwich(s1.getId());
                    Float rating2 = preferences.getRatingForSandwich(s2.getId());
                    if(rating1 == null){
                        rating1 = new Float(0.00);
                    }
                    if(rating2 == null){
                        rating2 = new Float(0.00);
                    }
                    return  rating1.compareTo(rating2);
                }
            });
            Collections.reverse(sandwiches);

            return sandwiches;
            /*
            Map<Float, Sandwich> sortedSandwiches = new TreeMap<>();

            for (Sandwich s : allSandwiches) {
                Float rating = preferences.getRatingForSandwich(s.getId());
                if(rating == null){
                    rating = new Float(0.00);
                    sortedSandwiches.put(rating, s);
                }else{
                    sortedSandwiches.put(rating, s);
                }
            }

            List sortedList = new ArrayList(sortedSandwiches.values());
                        Collections.reverse(sortedList);

            return sortedList;
            */

        } catch (ServiceUnavailableException e) {
            return repository.findAll();
        }

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