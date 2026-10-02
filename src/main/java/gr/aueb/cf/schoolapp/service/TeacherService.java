package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.schoolapp.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.schoolapp.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.schoolapp.dto.TeacherEditDTO;
import gr.aueb.cf.schoolapp.dto.TeacherEditReadOnlyDTO;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.schoolapp.mapper.Mapper;
import gr.aueb.cf.schoolapp.model.Region;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.repository.RegionRepository;
import gr.aueb.cf.schoolapp.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService implements ITeacherService {

    private final TeacherRepository teacherRepository;
    private final RegionRepository regionRepository;
    private final Mapper mapper;

//    @Autowired
//    public TeacherService(TeacherRepository teacherRepository, RegionRepository regionRepository, Mapper mapper) {
//        this.teacherRepository = teacherRepository;
//        this.regionRepository = regionRepository;
//        this.mapper = mapper;
//    }

    @Override
    @PreAuthorize("hasAuthority('INSERT_TEACHER')")
    @Transactional(rollbackFor = { EntityAlreadyExistsException.class, EntityInvalidArgumentException.class })
    public TeacherReadOnlyDTO saveTeacher(TeacherInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {

        try {
//            if (dto.vat() != null && teacherRepository.findByVat(dto.vat()).isPresent()) {
            if (dto.vat() != null && isTeacherExistsByVat(dto.vat())) {
                throw new EntityAlreadyExistsException("Teacher with VAT= " + dto.vat() + " already exists");
            }

            Region region = regionRepository.findById(dto.regionId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("Region id= " + dto.regionId() + " not found"));

            Teacher teacher = mapper.mapToTeacherEntity(dto);
            region.addTeacher(teacher);
            teacherRepository.save(teacher);        // pre-persist - saved teacher
            log.info("Teacher with vat={} save successfully ", dto.vat());  // Structured Logging -- parameterized placeholder pattern
            return  mapper.mapToTeacherReadOnlyDTO(teacher);
        } catch (EntityAlreadyExistsException  e) {
            log.warn("Save failed for teacher with VAT={}. Teacher already exists", dto.vat());
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.warn("Save failed for teacher with VAT={}. Region with id={} invalid", dto.vat(), dto.regionId());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.warn("Save failed for teacher with VAT={}. Teacher exists", dto.vat());
            throw new EntityAlreadyExistsException("Save failed for teacher with VAT= " + dto.vat() + " already exists");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('EDIT_TEACHER')")
    @Transactional(rollbackFor = { EntityNotFoundException.class, EntityAlreadyExistsException.class, EntityInvalidArgumentException.class })
    public TeacherReadOnlyDTO updateTeacher(TeacherEditDTO dto)
            throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidArgumentException {

        try {
            Teacher teacher = teacherRepository.findByUuidAndDeletedFalse(dto.uuid())
                    .orElseThrow(() -> new  EntityNotFoundException("Teacher with uuid= " + dto.uuid() + " not found"));

            if (!teacher.getVat().equals(dto.vat())) {  // TODO - use Objects utility class for null safety
                if (teacherRepository.findByVatAndDeletedFalse(dto.vat()).isPresent()) {
                    throw new EntityAlreadyExistsException("Teacher with VAT= " + dto.vat() + " already exists");
                }
                teacher.setVat(dto.vat());
            }

            teacher.setFirstname(dto.firstname());
            teacher.setLastname(dto.lastname());

            if (!Objects.equals(teacher.getRegion().getId(), dto.regionId())) {
                Region region = regionRepository.findById(dto.regionId())
                        .orElseThrow(() -> new EntityInvalidArgumentException("Region id= " + dto.regionId() + " not found"));
                Region oldRegion = teacher.getRegion();

                if (oldRegion != null) {
                    oldRegion.removeTeacher(teacher);
                }
                region.addTeacher(teacher);
            }

            teacherRepository.save(teacher);        // προαιρετικό  dirty check
            log.info("Teacher with VAT={} update successfully ", dto.vat());
            return  mapper.mapToTeacherReadOnlyDTO(teacher);
        } catch (EntityNotFoundException e) {
            log.warn("Update failed for teacher with uuid={}. Teacher not found", dto.uuid());
            throw e;
        } catch (EntityAlreadyExistsException e) {
            log.warn("Update failed for teacher with uuid={}. Teacher already exists", dto.uuid());
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.warn("Update failed for teacher with uuid={}. Region with id={} invalid", dto.uuid(), dto.regionId());
            throw e;
        }
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_TEACHERS')")
    @Transactional(readOnly = true)
    public Page<TeacherReadOnlyDTO> getPaginatedTeachersDeletedFalse(Pageable pageable) {
        Page<Teacher> teachersPage = teacherRepository.findAllByDeletedFalse(pageable);
        log.debug("Get paginated teachers not deleted returned successfully page={}, size={}",
                teachersPage.getNumber(), teachersPage.getSize());
        return teachersPage.map(mapper::mapToTeacherReadOnlyDTO);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<TeacherReadOnlyDTO> getPaginatedTeachers(Pageable pageable) {
        Page<Teacher> teachersPage = teacherRepository.findAll(pageable);
        log.debug("Get paginated teachers returned successfully page={}, size={}",
                teachersPage.getNumber(), teachersPage.getSize());
        return teachersPage.map(mapper::mapToTeacherReadOnlyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherEditDTO getTeacherByUUIDDeletedFalse(UUID uuid) throws EntityNotFoundException {

        try {
            Teacher teacher = teacherRepository.findByUuidAndDeletedFalse(uuid)
                    .orElseThrow(() -> new  EntityNotFoundException("Teacher with uuid= " + uuid+ " not found"));
            log.debug("Teacher with uuid={} returned successfully.", uuid);
            return mapper.mapToTeacherEditDTO(teacher);
        } catch (EntityNotFoundException e) {
            log.warn("Get teacher with uuid={} not found", uuid);
            throw e;
        }
    }

    @Override
    @PreAuthorize("hasAuthority('DELETE_TEACHER')")
    @Transactional(rollbackFor = EntityNotFoundException.class)
    public TeacherReadOnlyDTO deleteTeacherByUUID(UUID uuid) throws EntityNotFoundException {

        try {
            Teacher teacher = teacherRepository.findByUuidAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Teacher with uuid= " + uuid+ " not found"));

            teacher.softDelete();
            // no explicit save needed due to dirty checking
            // teacherRepository.save(teacher);
            log.info("Teacher with uuid={} deleted successfully.", uuid);
            return mapper.mapToTeacherReadOnlyDTO(teacher);

        } catch (EntityNotFoundException e) {
            log.warn("Delete failed. Teacher with uuid={} not found", uuid);
            throw e;
        }
    }


    @Override
    @Transactional(readOnly = true)
    public boolean isTeacherExistsByVat(String vat) {
        return teacherRepository.findByVat(vat).isPresent();
    }


}
