package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record PageableDto<T>(
    T filters,
    Integer page,
    Integer size
) {
}
