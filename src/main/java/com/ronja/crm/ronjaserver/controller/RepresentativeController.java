package com.ronja.crm.ronjaserver.controller;

import com.ronja.crm.ronjaserver.dto.RepresentativeDto;
import com.ronja.crm.ronjaserver.dto.RepresentativeMapper;
import com.ronja.crm.ronjaserver.entity.Customer;
import com.ronja.crm.ronjaserver.entity.Representative;
import com.ronja.crm.ronjaserver.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/representatives")
public class RepresentativeController {

  private final RepresentativeMapper mapper;
  private final EntityService<Customer> customerService;
  private final EntityService<Representative> representativeService;

  public RepresentativeController(@Autowired EntityService<Representative> representativeService,
                                  @Autowired EntityService<Customer> customerService,
                                  @Autowired RepresentativeMapper mapper) {
    this.representativeService = representativeService;
    this.customerService = customerService;
    this.mapper = mapper;
  }

  @GetMapping("/list")
  public List<Representative> list() {
    return representativeService.findAll();
  }

  @PostMapping("/add")
  public ResponseEntity<RepresentativeDto> add(@Valid @RequestBody RepresentativeDto dto) {
    Representative representative = provideRepresentative(dto);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(representative.getId())
        .toUri();

    return ResponseEntity.created(uri).body(mapper.toDto(representative));
  }

  @PutMapping("/update")
  public ResponseEntity<RepresentativeDto> update(@Valid @RequestBody RepresentativeDto dto) {
    if (representativeService.existsById(dto.id())) {
      Representative representative = provideRepresentative(dto);
      return ResponseEntity.ok(mapper.toDto(representative));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Object> delete(@PathVariable int id) {
    if (representativeService.existsById(id)) {
      representativeService.deleteById(id);
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  private Representative provideRepresentative(RepresentativeDto dto) {
    Customer customer = customerService.findById(dto.customerId());
    return representativeService.save(mapper.toEntity(dto, customer));
  }
}
