package gr.aueb.cf.schoolapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;

import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "teachers")
public class Teacher extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Column(unique = true, nullable = false)
    private String vat;

    private String firstname;
    private String lastname;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    public Teacher(String firstname, String lastname, String vat) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.vat = vat;
    }

    @PrePersist
    public void initializeUUID() {
        if (uuid == null) uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Teacher teacher)) return false;
        return Objects.equals(getVat(), teacher.getVat());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getVat());
    }
}


