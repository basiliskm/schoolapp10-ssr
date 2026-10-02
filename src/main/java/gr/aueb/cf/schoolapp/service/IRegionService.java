package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.dto.RegionReadOnlyDTO;

import java.util.List;

public interface IRegionService {
    List<RegionReadOnlyDTO> findAllRegionsSortedByName();
}
