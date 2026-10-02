package gr.aueb.cf.schoolapp.mapper;

import gr.aueb.cf.schoolapp.dto.*;
import gr.aueb.cf.schoolapp.model.Region;
import gr.aueb.cf.schoolapp.model.Role;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public Teacher mapToTeacherEntity(TeacherInsertDTO teacherInsertDTO) {
        return new Teacher(teacherInsertDTO.firstname(), teacherInsertDTO.lastname(), teacherInsertDTO.vat());
    }

    public TeacherReadOnlyDTO mapToTeacherReadOnlyDTO(Teacher teacher) {
        return new TeacherReadOnlyDTO(teacher.getUuid().toString(), teacher.getFirstname(), teacher.getLastname(),
                teacher.getVat(), teacher.getRegion().getName());
    }

    public RegionReadOnlyDTO mapToRegionReadOnlyDTO(Region region) {
        return new RegionReadOnlyDTO(region.getId(), region.getName());
    }

    public TeacherEditDTO mapToTeacherEditDTO(Teacher teacher) {
        return new TeacherEditDTO(teacher.getUuid(), teacher.getFirstname(),
                teacher.getLastname(), teacher.getVat(), teacher.getRegion().getId());
    }
    public User mapToUserEntity(UserInsertDTO userInsertDTO) {
        return new User(userInsertDTO.username(), userInsertDTO.password());
    }

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(user.getUuid().toString(), user.getUsername(), user.getRole().getName());
    }

    public RoleReadOnlyDTO mapToRoleReadOnlyDTO(Role role) {
        return new RoleReadOnlyDTO(role.getId(), role.getName());
    }

}
