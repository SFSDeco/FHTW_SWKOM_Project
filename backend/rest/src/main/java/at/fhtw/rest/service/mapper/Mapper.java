package at.fhtw.rest.service.mapper;

public interface Mapper<S, T> {
    T mapToDto(S source);
}
