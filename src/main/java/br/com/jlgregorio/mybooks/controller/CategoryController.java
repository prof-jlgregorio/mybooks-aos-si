package br.com.jlgregorio.mybooks.controller;

import br.com.jlgregorio.mybooks.model.CategoryModel;
import br.com.jlgregorio.mybooks.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category/v1")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping(value = "/{id}", produces = {"application/json", "application/xml"})
    public CategoryModel findById(@PathVariable long id){
        CategoryModel model = service.findById(id);
        buildEntityLink(model);
        return  model;
    }

    @GetMapping(value = "/", produces = {"application/json", "application/xml"})
    public CollectionModel<CategoryModel> findAll(){
        //create a CollectionModel from the ArrayList of Category
        CollectionModel<CategoryModel> collection = CollectionModel.of(service.findAll());
        //iterates over the collection building the self links for each model
        for(final CategoryModel model : collection){
            buildEntityLink(model);
        }
        //build the link to the collection
        buildCollectionLink(collection);
        //return the collection
        return collection;
    }

    @PostMapping(produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
    public CategoryModel save(@RequestBody CategoryModel model){
        return service.save(model);
    }

    @PutMapping(produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
    public CategoryModel update(@RequestBody CategoryModel model){
        return service.update(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }


    private void buildEntityLink(CategoryModel model){
        //link to self
        model.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(
                                CategoryController.class).findById(model.getId())
                ).withSelfRel()
        );
        //..link to delete
        model.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(CategoryController.class).delete(model.getId())
                ).withRel("delete")
        );
    }

    private void buildCollectionLink(CollectionModel<CategoryModel> collection){
        collection.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(CategoryController.class).findAll()
                ).withRel(IanaLinkRelations.COLLECTION)
        );
    }

}
