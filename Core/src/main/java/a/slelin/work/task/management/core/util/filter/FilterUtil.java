package a.slelin.work.task.management.core.util.filter;

import a.slelin.work.task.management.core.exception.FilterParseException;
import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static a.slelin.work.task.management.core.util.DateTimeUtil.*;

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
            case EQ, NEQ -> {
                Class<?> fieldType = path.getJavaType();

                if (Collection.class.isAssignableFrom(fieldType)) {
                    throw new FilterParseException("Operation %s is not supported for field of type %s"
                            .formatted(operation.getDisplayName(), fieldType.getSimpleName()));
                }

                yield switch (operation) {
                    case EQ -> cb.equal(path, value);
                    case NEQ -> cb.notEqual(path, value);
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case IS_NULL, IS_NOT_NULL -> {
                Class<?> fieldType = path.getJavaType();

                if (Collection.class.isAssignableFrom(fieldType)
                        || fieldType.isEnum()) {
                    throw new FilterParseException("Operation %s is not supported for field of type %s"
                            .formatted(operation.getDisplayName(), fieldType.getSimpleName()));
                }

                yield switch (operation) {
                    case IS_NULL -> cb.isNull(path);
                    case IS_NOT_NULL -> cb.isNotNull(path);
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case GT, GE, LT, LE -> {
                Class<?> fieldType = path.getJavaType();

                if (!Number.class.isAssignableFrom(fieldType)) {
                    throw new FilterParseException("Operation %s is not supported for field of type %s"
                            .formatted(operation.getDisplayName(), fieldType.getSimpleName()));
                }

                if (fieldType == Double.class) {
                    Expression<Double> expression = path.as(Double.class);
                    Double number = valueToType(value, Double.class);

                    yield switch (operation) {
                        case GT -> cb.gt(expression, number);
                        case GE -> cb.ge(expression, number);
                        case LT -> cb.lt(expression, number);
                        case LE -> cb.le(expression, number);
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Float.class) {
                    Expression<Float> expression = path.as(Float.class);
                    Float number = valueToType(value, Float.class);

                    yield switch (operation) {
                        case GT -> cb.gt(expression, number);
                        case GE -> cb.ge(expression, number);
                        case LT -> cb.lt(expression, number);
                        case LE -> cb.le(expression, number);
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Long.class) {
                    Expression<Long> expression = path.as(Long.class);
                    Long number = valueToType(value, Long.class);

                    yield switch (operation) {
                        case GT -> cb.gt(expression, number);
                        case GE -> cb.ge(expression, number);
                        case LT -> cb.lt(expression, number);
                        case LE -> cb.le(expression, number);
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Integer.class) {
                    Expression<Integer> expression = path.as(Integer.class);
                    Integer number = valueToType(value, Integer.class);

                    yield switch (operation) {
                        case GT -> cb.gt(expression, number);
                        case GE -> cb.ge(expression, number);
                        case LT -> cb.lt(expression, number);
                        case LE -> cb.le(expression, number);
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Short.class) {
                    Expression<Short> expression = path.as(Short.class);
                    Short number = valueToType(value, Short.class);

                    yield switch (operation) {
                        case GT -> cb.gt(expression, number);
                        case GE -> cb.ge(expression, number);
                        case LT -> cb.lt(expression, number);
                        case LE -> cb.le(expression, number);
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Byte.class) {
                    Expression<Byte> expression = path.as(Byte.class);
                    Byte number = valueToType(value, Byte.class);

                    yield switch (operation) {
                        case GT -> cb.gt(expression, number);
                        case GE -> cb.ge(expression, number);
                        case LT -> cb.lt(expression, number);
                        case LE -> cb.le(expression, number);
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else {
                    throw new IllegalArgumentException("Unexpected field type: " + fieldType.getSimpleName());
                }
            }

            case LIKE, NOT_LIKE, STARTS_WITH, NOT_STARTS_WITH, ENDS_WITH, NOT_ENDS_WITH -> {
                Class<?> fieldType = path.getJavaType();

                if (Collection.class.isAssignableFrom(fieldType)
                        || fieldType.isEnum()) {
                    throw new FilterParseException("Operation %s is not supported for field of type %s"
                            .formatted(operation.getDisplayName(), fieldType.getSimpleName()));
                }

                Expression<String> expression = path.as(String.class);

                String str = valueToType(value, String.class).toLowerCase();

                yield switch (operation) {
                    case LIKE -> cb.like(cb.lower(expression), "%" + str + "%");
                    case NOT_LIKE -> cb.notLike(cb.lower(expression), "%" + str + "%");
                    case STARTS_WITH -> cb.like(cb.lower(expression), str + "%");
                    case NOT_STARTS_WITH -> cb.notLike(cb.lower(expression), str + "%");
                    case ENDS_WITH -> cb.like(cb.lower(expression), "%" + str);
                    case NOT_ENDS_WITH -> cb.notLike(cb.lower(expression), "%" + str);
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case IS_EMPTY, IS_NOT_EMPTY -> {
                Class<?> fieldType = path.getJavaType();

                if (!Collection.class.isAssignableFrom(fieldType)) {
                    throw new FilterParseException("Operation %s is not supported for field of type %s"
                            .formatted(operation.getDisplayName(), fieldType.getSimpleName()));
                }

                @SuppressWarnings("unchecked")
                Expression<Collection<?>> expression = (Expression<Collection<?>>) path;

                yield switch (operation) {
                    case IS_EMPTY -> cb.isEmpty(expression);
                    case IS_NOT_EMPTY -> cb.isNotEmpty(expression);
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case IS_TRUE, IS_FALSE -> {
                Class<?> actualType = path.getJavaType();

                if (!actualType.equals(Boolean.class) && !actualType.equals(boolean.class)) {
                    throw new FilterParseException("Operation %s is not supported for field of type %s."
                            .formatted(operation.getDisplayName(), actualType.getSimpleName())
                    );
                }

                Expression<Boolean> expression = path.as(Boolean.class);

                yield switch (operation) {
                    case IS_TRUE -> cb.isTrue(expression);
                    case IS_FALSE -> cb.isFalse(expression);
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case IN, NOT_IN -> {
                Class<?> fieldType = path.getJavaType();

                if (Collection.class.isAssignableFrom(fieldType)
                        || fieldType.isEnum()) {
                    throw new FilterParseException("Operation %s is not supported for field of type %s"
                            .formatted(operation.getDisplayName(), fieldType.getSimpleName()));
                }

                List<?> list = valueToType(value, List.class);

                yield switch (operation) {
                    case IN -> path.in(list);
                    case NOT_IN -> cb.not(path.in(list));
                    default -> throw new IllegalArgumentException("Unexpected value");
                };
            }

            case BETWEEN, NOT_BETWEEN -> {
                Class<?> fieldType = path.getJavaType();

                if (!Number.class.isAssignableFrom(fieldType)) {
                    throw new FilterParseException("Operation %s is not supported for field of type %s"
                            .formatted(operation.getDisplayName(), fieldType.getSimpleName()));
                }

                if (fieldType == Double.class) {
                    Expression<Double> expression = path.as(Double.class);
                    Double from = valueToType(value, Double.class);
                    Double to = valueToType(value2, Double.class);

                    yield switch (operation) {
                        case BETWEEN -> cb.between(expression, from, to);
                        case NOT_BETWEEN -> cb.not(cb.between(expression, from, to));
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Float.class) {
                    Expression<Float> expression = path.as(Float.class);
                    Float from = valueToType(value, Float.class);
                    Float to = valueToType(value2, Float.class);

                    yield switch (operation) {
                        case BETWEEN -> cb.between(expression, from, to);
                        case NOT_BETWEEN -> cb.not(cb.between(expression, from, to));
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Long.class) {
                    Expression<Long> expression = path.as(Long.class);
                    Long from = valueToType(value, Long.class);
                    Long to = valueToType(value2, Long.class);

                    yield switch (operation) {
                        case BETWEEN -> cb.between(expression, from, to);
                        case NOT_BETWEEN -> cb.not(cb.between(expression, from, to));
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Integer.class) {
                    Expression<Integer> expression = path.as(Integer.class);
                    Integer from = valueToType(value, Integer.class);
                    Integer to = valueToType(value2, Integer.class);

                    yield switch (operation) {
                        case BETWEEN -> cb.between(expression, from, to);
                        case NOT_BETWEEN -> cb.not(cb.between(expression, from, to));
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Short.class) {
                    Expression<Short> expression = path.as(Short.class);
                    Short from = valueToType(value, Short.class);
                    Short to = valueToType(value2, Short.class);

                    yield switch (operation) {
                        case BETWEEN -> cb.between(expression, from, to);
                        case NOT_BETWEEN -> cb.not(cb.between(expression, from, to));
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == Byte.class) {
                    Expression<Byte> expression = path.as(Byte.class);
                    Byte from = valueToType(value, Byte.class);
                    Byte to = valueToType(value2, Byte.class);

                    yield switch (operation) {
                        case BETWEEN -> cb.between(expression, from, to);
                        case NOT_BETWEEN -> cb.not(cb.between(expression, from, to));
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else {
                    throw new IllegalArgumentException("Unexpected field type: " + fieldType.getSimpleName());
                }
            }

            case BEFORE, AFTER -> {
                Class<?> fieldType = path.getJavaType();

                if (!LocalDate.class.isAssignableFrom(fieldType) ||
                        !LocalTime.class.isAssignableFrom(fieldType) ||
                        !LocalDateTime.class.isAssignableFrom(fieldType)) {
                    throw new FilterParseException("Operation %s is not supported for field of type %s"
                            .formatted(operation.getDisplayName(), fieldType.getSimpleName()));
                }

                if (fieldType == LocalDate.class) {
                    Expression<LocalDate> expression = path.as(LocalDate.class);
                    LocalDate dateValue = valueToType(value, LocalDate.class);

                    yield switch (operation) {
                        case BEFORE -> cb.lessThan(expression, dateValue);
                        case AFTER -> cb.greaterThan(expression, dateValue);
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == LocalTime.class) {
                    Expression<LocalTime> expression = path.as(LocalTime.class);
                    LocalTime dateValue = valueToType(value, LocalTime.class);

                    yield switch (operation) {
                        case BEFORE -> cb.lessThan(expression, dateValue);
                        case AFTER -> cb.greaterThan(expression, dateValue);
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else if (fieldType == LocalDateTime.class) {
                    Expression<LocalDateTime> expression = path.as(LocalDateTime.class);
                    LocalDateTime dateValue = valueToType(value, LocalDateTime.class);

                    yield switch (operation) {
                        case BEFORE -> cb.lessThan(expression, dateValue);
                        case AFTER -> cb.greaterThan(expression, dateValue);
                        default -> throw new IllegalArgumentException("Unexpected value");
                    };
                } else {
                    throw new IllegalArgumentException("Unexpected field type: " + fieldType.getSimpleName());
                }
            }
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

    public static <T> T valueToType(Object value, Class<T> type) {
        if (value == null) {
            throw new FilterParseException("Value should be not null.");
        }

        try {
            if (type.isInstance(value)) {
                return type.cast(value);
            }

            if (Number.class.isAssignableFrom(type)) {
                if (value instanceof Number number) {
                    if (type == Double.class) return type.cast(number.doubleValue());
                    if (type == Float.class) return type.cast(number.floatValue());
                    if (type == Long.class) return type.cast(number.longValue());
                    if (type == Integer.class) return type.cast(number.intValue());
                    if (type == Short.class) return type.cast(number.shortValue());
                    if (type == Byte.class) return type.cast(number.byteValue());
                } else if (value instanceof CharSequence) {
                    if (type == Double.class) return type.cast(Double.parseDouble(value.toString()));
                    if (type == Float.class) return type.cast(Float.parseFloat(value.toString()));
                    if (type == Long.class) return type.cast(Long.parseLong(value.toString()));
                    if (type == Integer.class) return type.cast(Integer.parseInt(value.toString()));
                    if (type == Short.class) return type.cast(Short.parseShort(value.toString()));
                    if (type == Byte.class) return type.cast(Byte.parseByte(value.toString()));
                }
            }

            if (CharSequence.class.isAssignableFrom(type)) return type.cast(value.toString());

            if (type == LocalDate.class
                    || type == LocalTime.class
                    || type == LocalDateTime.class) {
                if (value instanceof CharSequence str) {
                    if (type == LocalDate.class) return type.cast(LocalDate.parse(str, UNIVERSE_DATE_FORMATTER));
                    if (type == LocalTime.class) return type.cast(LocalTime.parse(str, UNIVERSE_TIME_FORMATTER));
                    return type.cast(LocalDateTime.parse(str, UNIVERSE_DATETIME_FORMATTER));
                }
            }

            throw new RuntimeException("Unsupported type : " + type);
        } catch (Exception e) {
            throw new FilterParseException("Cannot convert value " + value + " to type " + type, e);
        }
    }
}
