package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.schoolapp.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.schoolapp.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.schoolapp.dto.TeacherEditDTO;
import gr.aueb.cf.schoolapp.dto.TeacherEditReadOnlyDTO;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ITeacherService {

    TeacherReadOnlyDTO saveTeacher(TeacherInsertDTO teacherInsertDTO)
        throws EntityAlreadyExistsException, EntityInvalidArgumentException;

    TeacherReadOnlyDTO updateTeacher(TeacherEditDTO teacherEditDTO)
        throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidArgumentException;

    boolean isTeacherExistsByVat(String vat);

    Page<TeacherReadOnlyDTO> getPaginatedTeachersDeletedFalse(Pageable pageable);
    Page<TeacherReadOnlyDTO> getPaginatedTeachers(Pageable pageable);

    TeacherEditDTO getTeacherByUUIDDeletedFalse(UUID uuid)
        throws EntityNotFoundException;

    TeacherReadOnlyDTO deleteTeacherByUUID(UUID uuid)
        throws EntityNotFoundException;
}
