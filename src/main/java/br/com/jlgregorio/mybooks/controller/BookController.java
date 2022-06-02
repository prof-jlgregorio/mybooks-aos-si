package br.com.jlgregorio.mybooks.controller;

import br.com.jlgregorio.mybooks.model.BookModel;
import br.com.jlgregorio.mybooks.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/book/v1")
public class BookController {

    @Autowired
    private BookService service;

    @GetMapping(value = "/{id}", produces = {"application/json", "application/xml"})
    public BookModel findById(@PathVariable long id){
        BookModel model = service.findById(id);
        buildEntityLink(model);
        return model;
    }

    @GetMapping(value = "/", produces = {"application/json", "application/xml"})
    public CollectionModel<BookModel> findAll(){
        CollectionModel<BookModel> collectionModel =
                CollectionModel.of(service.findAll());
        buildCollectionLink(collectionModel);
        return collectionModel;
    }

    @GetMapping(value = "/find", produces = {"application/json", "application/xml"})
    public List<BookModel> findByTitle(@RequestParam Optional<String> title,
                                       @RequestParam Optional<String> authorName){
        List<BookModel> result = new ArrayList<>();
        if(title.isPresent()){
            result = service.findByTitle(title.get());
        }
        if(authorName.isPresent()){
            result = service.findByAuthor(authorName.get());
        }
        return result;
    }

    @PostMapping(produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
    public BookModel save(@RequestBody BookModel model){
        return service.save(model);
    }

    @PutMapping(produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
    public BookModel update(@RequestBody BookModel model){
        return service.update(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    public void buildEntityLink(BookModel model){
        model.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(BookController.class).findById(model.getId())
                ).withRel(IanaLinkRelations.SELF)
        );

        //..build links to relationships
        if(!model.getCategory().hasLinks()) {
            model.getCategory().add(
                    WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder
                                    .methodOn(CategoryController.class)
                                    .findById(model.getCategory().getId())
                    ).withRel(IanaLinkRelations.SELF)
            );
        }

        if(!model.getAuthor().hasLinks()) {
            model.getAuthor().add(
                    WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder
                                    .methodOn(AuthorController.class)
                                    .findById(model.getAuthor().getId())
                    ).withRel(IanaLinkRelations.SELF)
            );
        }

    }

    public void buildCollectionLink(
            CollectionModel<BookModel> collectionModel){

        for (BookModel book : collectionModel){
           buildEntityLink(book);
        }

        collectionModel.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(
                                BookController.class
                        ).findAll())
                        .withRel(IanaLinkRelations.COLLECTION)
        );

    }




}
