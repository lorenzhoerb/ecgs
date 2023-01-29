package at.ac.tuwien.sepm.groupphase.backend.service.helprecords;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.FlagsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Flags;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.FlagsRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class FlagUtils {
    FlagsMapper flagsMapper;
    FlagsRepository flagsRepository;

    public FlagUtils(FlagsMapper flagsMapper, FlagsRepository flagsRepository) {
        this.flagsMapper = flagsMapper;
        this.flagsRepository = flagsRepository;
    }

    public Flags verify(SimpleFlagDto flagDto, Set<Long> myFlagIds) {
        Flags flag = flagsMapper.simpleFlagDtoToFlags(flagDto);

        if (!myFlagIds.contains(flag.getId())) {
            throw new ValidationListException("Unmanaged flag was passed",
                List.of("Unmanaged flag was passed"));
        }

        Optional<Flags> found = flagsRepository.findById(flag.getId());

        if (found.isEmpty()) {
            throw new ValidationListException("Invalid flag was passed",
                List.of("Invalid flag was passed"));
        }

        flag = found.get();

        return flag;
    }

    public Flags verifyOrCreate(SimpleFlagDto flagDto, Set<Long> myFlagIds) {
        if (flagDto.id() < 0) {
            Flags flag = flagsMapper.simpleFlagDtoToFlags(flagDto);
            flag.setClubs(new HashSet<>());
            flag.setRegistrations(new HashSet<>());
            return flagsRepository.save(flag);
        }

        return verify(flagDto, myFlagIds);
    }
}
