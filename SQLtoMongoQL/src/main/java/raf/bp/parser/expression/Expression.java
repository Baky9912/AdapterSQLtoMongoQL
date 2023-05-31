package raf.bp.parser.expression;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Expression {
    abstract public String toString();
    abstract public boolean isNestedQuery();
}
