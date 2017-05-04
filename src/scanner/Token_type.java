package scanner;

/**
 * Created by think on 2016/12/4.
 */
public enum Token_type {
    ORIGIN, SCALE, ROT, IS,	TO, STEP, DRAW, FOR, FROM, 	// 保留字
    T,				  									// 参数
    SEMICO, L_BRACKET, R_BRACKET, COMMA,				// 分隔符
    PLUS, MINUS, MUL, DIV, POWER,						// 运算符
    FUNC,				  								// 函数（调用）
    CONST_ID,			  								// 常数
    NONTOKEN,			  								// 空记号（源程序结束）
    ERRTOKEN;			  								// 出错记号（非法输入）
}
