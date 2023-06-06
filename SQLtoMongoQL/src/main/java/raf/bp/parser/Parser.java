package raf.bp.parser;

public interface Parser<R, I> {
    R parse(I input);
}
