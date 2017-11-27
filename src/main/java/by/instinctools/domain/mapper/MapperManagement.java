package by.instinctools.domain.mapper;

public interface MapperManagement<T, R> {

    R transform(T source);
}
