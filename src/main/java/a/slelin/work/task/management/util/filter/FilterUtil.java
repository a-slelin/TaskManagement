package a.slelin.work.task.management.util.filter;

import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilterUtil {

    public static <T> Specification<T> toSpecification(FilterChain filterChain) {
        return (root, _, criteriaBuilder) -> {
            if (filterChain == null || filterChain.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Predicate[] predicates = filterChain.filters().stream()
                    .filter(Objects::nonNull)
                    .map(filter -> toPredicate(filter, root, criteriaBuilder))
                    .toArray(Predicate[]::new);

            return criteriaBuilder.and(predicates);
        };
    }

    public static <T> Predicate toPredicate(Filter filter, Root<T> root, CriteriaBuilder cb) {
        String field = filter.field();
        Operation operation = filter.operation();
        Object value = filter.value();
        Object value2 = filter.value2();

        if (field.toLowerCase().contains("password")) {
            throw new FilterParseException("Filtering by field password is denied.");
        }

        Path<T> path = getNestedPath(root, field);

        return switch (operation) {
            case EQ -> {
                if (path.getJavaType() == UUID.class) {
                    value = UUID.fromString((String) value);
                }
                yield cb.equal(path, value);
            }
            case NEQ -> {
                if (path.getJavaType() == UUID.class) {
                    value = UUID.fromString((String) value);
                }
                yield cb.notEqual(path, value);
            }

            case IS_NULL -> cb.isNull(path);
            case IS_NOT_NULL -> cb.isNotNull(path);

            case GT, GE, LT, LE -> {
                Expression<Long> expression = pathToType(path, Long.class);

                Long number = valueToType(value, Long.class);

                yield switch (operation) {
                    case GT -> cb.gt(expression, number);
                    case GE -> cb.ge(expression, number);
                    case LT -> cb.lt(expression, number);
                    case LE -> cb.le(expression, number);
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case LIKE, NOT_LIKE, STARTS_WITH, NOT_STARTS_WITH, ENDS_WITH, NOT_ENDS_WITH -> {
                Expression<String> expression = pathToType(path, String.class);

                String str = valueToType(value, String.class);

                yield switch (operation) {
                    case LIKE -> cb.like(cb.lower(expression), "%" + str.toLowerCase() + "%");
                    case NOT_LIKE -> cb.notLike(cb.lower(expression), "%" + str.toLowerCase() + "%");
                    case STARTS_WITH -> cb.like(cb.lower(expression), str.toLowerCase() + "%");
                    case NOT_STARTS_WITH -> cb.notLike(cb.lower(expression), str.toLowerCase() + "%");
                    case ENDS_WITH -> cb.like(cb.lower(expression), "%" + str.toLowerCase());
                    case NOT_ENDS_WITH -> cb.notLike(cb.lower(expression), "%" + str.toLowerCase());
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case IS_EMPTY, IS_NOT_EMPTY -> {
                @SuppressWarnings("rawtypes")
                Expression<Collection> expression = pathToType(path, Collection.class);

                yield switch (operation) {
                    case IS_EMPTY -> cb.isEmpty(expression);
                    case IS_NOT_EMPTY -> cb.isNotEmpty(expression);
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case IS_TRUE, IS_FALSE -> {
                Expression<Boolean> expression = pathToType(path, Boolean.class);

                yield switch (operation) {
                    case IS_TRUE -> cb.isTrue(expression);
                    case IS_FALSE -> cb.isFalse(expression);
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case IN, NOT_IN -> {
                List<?> list = valueToType(value, List.class);

                yield switch (operation) {
                    case IN -> path.in(list);
                    case NOT_IN -> cb.not(path.in(list));
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case BETWEEN, NOT_BETWEEN -> throw new UnsupportedOperationException("Not supported yet.");

            case BEFORE, AFTER -> //noinspection DuplicateBranchesInSwitch
                    throw new UnsupportedOperationException("Not supported yet.");
        };
    }

    public static <T> Path<T> getNestedPath(Root<T> root, String field) {
        if (field == null) {
            throw new FilterParseException("Field should be not null.");
        }

        String[] parts = field.split("\\.");
        Path<T> currentPath = root;

        for (String part : parts) {
            if (part == null || part.isBlank()) {
                throw new FilterParseException("Invalid field path format : empty part.");
            }

            try {
                currentPath = currentPath.get(part);
            } catch (Exception e) {
                throw new FilterParseException("Invalid part path format : " + part);
            }
        }

        return currentPath;
    }

    public static <T> Expression<T> pathToType(Path<?> path, Class<T> type) {
        if (path == null) {
            throw new FilterParseException("Filter path should be not null.");
        }

        try {
            return path.as(type);
        } catch (Exception e) {
            String field = path.toString();

            Class<?> realType = path.getJavaType();
            if (realType == null) {
                realType = path.getModel().getBindableJavaType();
            }

            throw new FilterParseException("Cannot convert path " + field + " to type "
                    + type + ". Real type is " + realType.getSimpleName());
        }
    }

    public static <T> T valueToType(Object value, Class<T> type) {
        if (value == null) {
            throw new FilterParseException("Value should be not null.");
        }

        try {
            return type.cast(value);
        } catch (Exception e) {
            throw new FilterParseException("Cannot convert value " + value + " to type " + type);
        }
    }
}
