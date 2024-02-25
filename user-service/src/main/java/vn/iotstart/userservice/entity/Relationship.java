package vn.iotstart.userservice.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "relationships")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {})
@Builder
public class Relationship extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "relationship_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    private User parent;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @ToString.Exclude
    private User child;

    @Column(name = "is_accepted")
    private Boolean isAccepted;

}
