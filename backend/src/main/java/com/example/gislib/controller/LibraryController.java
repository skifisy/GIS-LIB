package com.example.gislib.controller;

import com.example.gislib.entity.Library;
import com.example.gislib.repository.LibraryRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/libraries")
    @CrossOrigin(origins = "*") public class LibraryController {
 private
  final LibraryRepository libraryRepository;
 private
  final GeometryFactory geometryFactory = new GeometryFactory();

 public
  LibraryController(LibraryRepository libraryRepository) {
    this.libraryRepository = libraryRepository;
  }

  @GetMapping public List<LibraryDto> list() {
    List<Library> libs = libraryRepository.findAll();
    return libs.stream()
        .map(l->{
          LibraryDto d = new LibraryDto();
          d.setId(l.getId());
          d.setName(l.getName());
          d.setCollege(l.getCollege());
          d.setNumberOfBooks(l.getNumberOfBooks());
          if (l.getLocation() != null) {
            d.setLon(l.getLocation().getX());
            d.setLat(l.getLocation().getY());
          }
          return d;
        })
        .collect(Collectors.toList());
  }

  @PostMapping public ResponseEntity <
      ? > create(@RequestBody CreateLibraryReq req) {
    Library lib = new Library();
    lib.setName(req.getName());
    lib.setCollege(req.getCollege());
    lib.setNumberOfBooks(req.getNumberOfBooks());
    // build point (注意：前端传入 lon/lat，此处构造 Point(x=lon, y=lat))
    Point p =
        geometryFactory.createPoint(new Coordinate(req.getLon(), req.getLat()));
    p.setSRID(4523);
    lib.setLocation(p);
    Library saved = libraryRepository.save(lib);
    // return DTO
    LibraryDto d = new LibraryDto();
    d.setId(saved.getId());
    d.setName(saved.getName());
    d.setCollege(saved.getCollege());
    d.setNumberOfBooks(saved.getNumberOfBooks());
    if (saved.getLocation() != null) {
      d.setLon(saved.getLocation().getX());
      d.setLat(saved.getLocation().getY());
    }
    return ResponseEntity.ok(d);
  }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
      try {
        libraryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
      } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
        return ResponseEntity.notFound().build();
      }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateLibraryReq req) {
      return libraryRepository.findById(id).map(lib -> {
        lib.setName(req.getName());
        lib.setCollege(req.getCollege());
        lib.setNumberOfBooks(req.getNumberOfBooks());
        Point p = geometryFactory.createPoint(new Coordinate(req.getLon(), req.getLat()));
        p.setSRID(4523);
        lib.setLocation(p);
        Library saved = libraryRepository.save(lib);
        LibraryDto d = new LibraryDto();
        d.setId(saved.getId());
        d.setName(saved.getName());
        d.setCollege(saved.getCollege());
        d.setNumberOfBooks(saved.getNumberOfBooks());
        if (saved.getLocation() != null) { d.setLon(saved.getLocation().getX()); d.setLat(saved.getLocation().getY()); }
        return ResponseEntity.ok(d);
      }).orElseGet(() -> ResponseEntity.notFound().build());
    }

 public
  static class CreateLibraryReq {
   private
    String name;
   private
    String college;
   private
    Long numberOfBooks;
   private
    double lon;
   private
    double lat;

   public
    String getName() { return name; }
   public
    void setName(String name) { this.name = name; }
   public
    String getCollege() { return college; }
   public
    void setCollege(String college) { this.college = college; }
   public
    Long getNumberOfBooks() { return numberOfBooks; }
   public
    void setNumberOfBooks(Long numberOfBooks) {
      this.numberOfBooks = numberOfBooks;
    }
   public
    double getLon() { return lon; }
   public
    void setLon(double lon) { this.lon = lon; }
   public
    double getLat() { return lat; }
   public
    void setLat(double lat) { this.lat = lat; }
  }

  public static class LibraryDto {
   private
    Long id;
   private
    String name;
   private
    String college;
   private
    Long numberOfBooks;
   private
    double lon;
   private
    double lat;

   public
    Long getId() { return id; }
   public
    void setId(Long id) { this.id = id; }
   public
    String getName() { return name; }
   public
    void setName(String name) { this.name = name; }
   public
    String getCollege() { return college; }
   public
    void setCollege(String college) { this.college = college; }
   public
    Long getNumberOfBooks() { return numberOfBooks; }
   public
    void setNumberOfBooks(Long numberOfBooks) {
      this.numberOfBooks = numberOfBooks;
    }
   public
    double getLon() { return lon; }
   public
    void setLon(double lon) { this.lon = lon; }
   public
    double getLat() { return lat; }
   public
    void setLat(double lat) { this.lat = lat; }
  }
}
