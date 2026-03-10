package a.slelin.work.task.management.util.filter;

import a.slelin.work.task.management.exception.EnumParseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@Getter
@RequiredArgsConstructor
@JsonSerialize(using = OperationSerializer.class)
@JsonDeserialize(using = OperationDeserializer.class)
public enum Operation {
    EQ("equals", "eq"),
    NEQ("not equals", "neq"),

    GT("greater", "gt"),
    GE("greater or equals", "ge"),
    LT("less", "ls"),
    LE("less or equals", "le"),

    LIKE("like", "lk"),
    NOT_LIKE("not like", "nlk"),
    STARTS_WITH("starts with", "sw"),
    NOT_STARTS_WITH("not starts with", "nsw"),
    ENDS_WITH("ends with", "ew"),
    NOT_ENDS_WITH("not ends with", "new"),

    IS_NULL("is null", "null"),
    IS_NOT_NULL("is not null", "nnull"),
    IS_EMPTY("is empty", "emp"),
    IS_NOT_EMPTY("is not empty", "nemp"),

    IS_TRUE("is true", "tr"),
    IS_FALSE("is false", "fls"),

    IN("in", "in"),
    NOT_IN("not in", "nin"),
    BETWEEN("between", "btwn"),
    NOT_BETWEEN("not between", "nbtwn"),

    BEFORE("before", "bfr"),
    AFTER("after", "ftr");

    private final String displayName;

    private final String shortName;

    private Operation opposite;

    public static Operation of(String key) {
        if (key == null) {
            return null;
        }

        key = key.trim();

        for (Operation value : Operation.values()) {
            if (key.equalsIgnoreCase(value.name()) ||
                    key.equalsIgnoreCase(value.displayName) ||
                    key.equalsIgnoreCase(value.shortName)) {
                return value;
            }
        }

        throw new EnumParseException(Operation.class, key);
    }

    static {
        EQ.opposite = NEQ;
        NEQ.opposite = EQ;

        GT.opposite = LT;
        GE.opposite = LE;
        LT.opposite = GT;
        LE.opposite = GE;

        LIKE.opposite = NOT_LIKE;
        NOT_LIKE.opposite = LIKE;
        STARTS_WITH.opposite = NOT_STARTS_WITH;
        NOT_STARTS_WITH.opposite = STARTS_WITH;
        ENDS_WITH.opposite = NOT_ENDS_WITH;
        NOT_ENDS_WITH.opposite = ENDS_WITH;

        IS_NULL.opposite = IS_NOT_NULL;
        IS_NOT_NULL.opposite = IS_NULL;
        IS_EMPTY.opposite = IS_NOT_EMPTY;
        IS_NOT_EMPTY.opposite = IS_EMPTY;

        IS_TRUE.opposite = IS_FALSE;
        IS_FALSE.opposite = IS_TRUE;

        IN.opposite = NOT_IN;
        NOT_IN.opposite = IN;
        BETWEEN.opposite = NOT_BETWEEN;
        NOT_BETWEEN.opposite = BETWEEN;

        BEFORE.opposite = AFTER;
        AFTER.opposite = BEFORE;
    }
}
