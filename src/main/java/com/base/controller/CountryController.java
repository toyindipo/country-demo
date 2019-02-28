package com.base.controller;

import com.base.model.Country;
import com.base.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Created by Toyin on 2/28/19.
 */
@RestController
@RequestMapping("/countries")
public class CountryController {
    private CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    /**
     *
     * @param country country instance deserialized from the request body
     * @param bindingResult bindingResult contains all validation errors
     * @return ResponseEntity instance containing the newly created country record
     * for successful method call, else 400 if there is validation error, else
     * 409 if there is already a country with the same name in database
     */
    @PostMapping
    public ResponseEntity<Country> saveCountry(@RequestBody @Valid Country country, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else if (countryService.findByName(country.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(countryService.save(country));
    }

    /**
     * Returns all country records in database
     * @return a List of Country, containing all country records in database
     */
    @GetMapping
    public ResponseEntity<List<Country>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAll());
    }

    /**
     * Edit a country record in database
     * @param id of the country record in database to edit
     * country instance containing update data, which is deserialized from the request body,
     * @param bindingResult bindingResult contains all validation errors
     * @return
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<Country> editCountry(@PathVariable("id") Long id,
                           @RequestBody @Valid Country country, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Optional<Country> old = countryService.findById(id);
            if (old.isPresent()) {
                return ResponseEntity.ok(countryService.edit(old.get(), country));
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    /**
     * Deletes a country with the given id in database
     * @param id id of the country to be deleted
     * @return ResponseEntity with OK status
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCountry(@PathVariable("id") Long id) {
        countryService.delete(id);
        return ResponseEntity.ok().build();
    }

}
