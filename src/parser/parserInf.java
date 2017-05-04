package parser;

/**
 * Created by think on 2016/12/7.
 */
public interface parserInf {
    void enter(String string);
    void back(String string);
    void call_match(String string);
    void Tree_trace(ExprNode x);
}
