package shootingstar.stellaide.service.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ContainerTreeResDto {
    String name;
    String type;
    List<ContainerTreeResDto> children;

    public ContainerTreeResDto(String name, String type) {
        this.name = name;
        this.type = type;
        this.children = new ArrayList<>();
    }

    public void addChild(ContainerTreeResDto child) {
        this.children.add(child);
    }
}
