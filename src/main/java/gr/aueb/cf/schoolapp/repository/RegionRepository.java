package gr.aueb.cf.schoolapp.repository;

import gr.aueb.cf.schoolapp.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    List<Region> findAllByOrderByNameAsc();
}
