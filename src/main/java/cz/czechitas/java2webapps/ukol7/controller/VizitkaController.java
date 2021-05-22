package cz.czechitas.java2webapps.ukol7.controller;

import cz.czechitas.java2webapps.ukol7.entity.Vizitka;
import cz.czechitas.java2webapps.ukol7.repository.VizitkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class VizitkaController {

    private final VizitkaRepository vizitkaRepository;

    @Autowired
    public VizitkaController(VizitkaRepository vizitkaRepository) {
        this.vizitkaRepository = vizitkaRepository;
    }

    @InitBinder
    public void nullStringBinding(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/")
    public Object seznam() {
        return new ModelAndView("seznam")
                .addObject("vizitky", vizitkaRepository.findAll());
    }

    @GetMapping("/{id:[0-9]+}")
    public Object detail(@PathVariable Integer id) {
        Optional<Vizitka> vizitka = vizitkaRepository.findById(id);
        if(vizitka.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ModelAndView result = new ModelAndView("vizitka");
        result.addObject("vizitka", vizitka.get());
        return result;
    }

    @GetMapping("/nova")
    public ModelAndView nova() {
        return new ModelAndView("formular")
                .addObject("vizitka", new Vizitka());
    }

    @GetMapping("/upravit")
    public Object upravit(Integer id) {
        if(vizitkaRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return new ModelAndView("upravit")
                .addObject("vizitka", vizitkaRepository.findById(id).get())
                .addObject("id", id);
    }

    @PostMapping("/upravit")
    public Object update(@ModelAttribute("vizitka") @Valid Vizitka vizitka, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "upravit";
        }
        vizitkaRepository.save(vizitka);
        return "redirect:/";
    }

    @PostMapping("/nova")
    public Object add(@ModelAttribute("vizitka") @Valid Vizitka vizitka, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "formular";
        }
        vizitka.setId(null);
        vizitkaRepository.save(vizitka);
        return "redirect:/";
    }

    @PostMapping(value = "/detail", params = {"id"})
    public String delete(Integer id) {
        vizitkaRepository.delete(vizitkaRepository.findById(id).get());
        return "redirect:/";
    }
}
