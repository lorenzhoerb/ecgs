package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Flags;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface FlagsMapper {
    List<ImportFlag> flagsToImportFlag(List<Flags> flags);
}
