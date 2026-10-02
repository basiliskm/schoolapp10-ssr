package gr.aueb.cf.schoolapp.service;


import gr.aueb.cf.schoolapp.dto.RegionReadOnlyDTO;
import gr.aueb.cf.schoolapp.mapper.Mapper;
import gr.aueb.cf.schoolapp.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionService implements IRegionService {

    private final RegionRepository regionRepository;
    private final Mapper mapper;

//    public RegionService(RegionRepository regionRepository, Mapper mapper) {
//        this.regionRepository = regionRepository;
//        this.mapper = mapper;
//    }

    @Override
    public List<RegionReadOnlyDTO> findAllRegionsSortedByName() {
        return regionRepository.findAllByOrderByNameAsc()
                .stream()
                .map(mapper::mapToRegionReadOnlyDTO)
                .toList();
    }
}
