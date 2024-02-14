package shootingstar.stellaide.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Container {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID containerId;

    @NotBlank
    private String type;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String path;

    @NotBlank
    private LocalDateTime createTime;

    @NotBlank
    private LocalDateTime editTime;

    @NotBlank
    private Long editUserId;

    public Container(String type, String name, String description, String path, Long editUserId) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.path = path;
        this.createTime = LocalDateTime.now();
        this.editTime = LocalDateTime.now();
        this.editUserId = editUserId;
    }
}
