package scanner;

/**
 * Created by think on 2016/12/4.
 */
public class Token {
    public Token_type token_type;
    public String sign_name;                                    //  记号的具体名字
    public double value;                                        //  记号的值
    public String func_name;                                    //  对应函数

    public Token(Token_type type, String sign_name, double value, String func_name) {
        this.token_type = type;
        this.sign_name = sign_name;
        this.value = value;
        this.func_name = func_name;
    }
}
