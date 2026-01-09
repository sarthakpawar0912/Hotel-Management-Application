package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByIsActiveTrue();

    Optional<Property> findByName(String name);

    List<Property> findByCity(String city);

    List<Property> findByState(String state);

    List<Property> findByCountry(String country);

    List<Property> findByStarRatingGreaterThanEqual(Integer rating);
}
