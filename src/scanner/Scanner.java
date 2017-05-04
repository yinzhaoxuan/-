package scanner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by think on 2016/12/4.
 */
public class Scanner {

    public static double T = 0.0;

    static Token Token_Tab[] = new Token[18];
    {
        Token_Tab[0] = new Token(Token_type.CONST_ID, "PI", 3.1415926 , null);
        Token_Tab[1] = new Token(Token_type.CONST_ID, "e", 2.71828 , null);
        Token_Tab[2] = new Token(Token_type.FUNC, "SIN", 0.0 ,"sin");
        Token_Tab[3] = new Token(Token_type.FUNC, "COS", 0.0 , "cos");
        Token_Tab[4] = new Token(Token_type.FUNC, "TAN", 0.0 , "tan");
        Token_Tab[5] = new Token(Token_type.FUNC, "LOG", 0.0 , "log");
        Token_Tab[6] = new Token(Token_type.FUNC, "EXP", 0.0 , "exp");
        Token_Tab[7] = new Token(Token_type.FUNC, "SQRT", 0.0 , "sqrt");
        //Token_Tab[8] = new Token(Token_type.POWER,"POW",0.0,Math.pow(T,T));
        Token_Tab[8] = new Token(Token_type.ORIGIN, "ORIGIN", 0.0 , null);
        Token_Tab[9] = new Token(Token_type.SCALE, "SCALE", 0.0 , null);
        Token_Tab[10] = new Token(Token_type.ROT, "ROT", 0.0 , null);
        Token_Tab[11] = new Token(Token_type.IS, "IS", 0.0 , null);
        Token_Tab[12] = new Token(Token_type.TO, "TO", 0.0 , null);
        Token_Tab[13] = new Token(Token_type.STEP, "STEP", 0.0 , null);
        Token_Tab[14] = new Token(Token_type.DRAW, "DRAW", 0.0 , null);
        Token_Tab[15] = new Token(Token_type.FOR, "FOR", 0.0 , null);
        Token_Tab[16] = new Token(Token_type.FROM, "FROM", 0.0 , null);
        Token_Tab[17] = new Token(Token_type.T, "T", T , null);
    }


    static RandomAccessFile reader = null;					//  定义一个random文件流
    static char[] SignBuffer = new char[100];				 		//  定义记号缓冲区
    static public int Line_num = 1;						 		//  用于跟踪读取文件的行号

    /*
	 * 初始化词法分析器
	 */
    public boolean InitScanner(File file) {
        try {
            reader = new RandomAccessFile(file, "rw");   	//  设置reader为可读写
            return true;
        } catch (Exception e) {
            System.out.println("Can not find this file");
            return false;
        }
    }

    /*
     * 关闭词法分析器
     */
    public void CloseScanner() {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error");
            System.exit(-1);
        }
    }

    /*
     * 从文件流中读出一个字符
     */
    public int getChar() {
        try {
            int Char = reader.read();
            return Char;
        } catch (IOException e) {
            System.out.println("Read error");
            e.printStackTrace();
            System.exit(-1);
        }
        return -1;
    }

    /*
     * 把预读的字符暂时文件流中
     */
    public void backChar(int Char) {
        if(Char == -1) return;
        try {
            long pointer;
            pointer = reader.getFilePointer();
            reader.seek(pointer - 1);
        } catch (IOException e) {
            System.out.println("I/O error");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /*
     * 将读取的字符加入到记号缓冲区
     */
    public void Add_char_buffer(char Char) {
        int index = 0;
        while (SignBuffer[index] != '\0' && index < SignBuffer.length) {
            index ++;
        }			// 取得当前字符串的长度
        if (index < SignBuffer.length) { 					// 字符缓冲区只有100个字符的大小
            SignBuffer[index] = Char;
            SignBuffer[index + 1] = '\0';
        }
        return;
    }

    /*
     * 清空记号缓冲区
     */
    public void Empty_buffer() {
        SignBuffer = new char[100]; 					// 将字符缓冲指向一个新的100个字符大小的空的数组
    }

    /*
     * 判断读取的记号是否出现在记号表中
     */
    public Token judgeSign(String ID) {
        int f;
        for (f = 0; f < 18; f++) {
            if (Token_Tab[f].sign_name.compareToIgnoreCase(ID) == 0) 	// 不区分大小写的识别
                return Token_Tab[f];
        }
        Token new_token = new Token(Token_type.ERRTOKEN, null, 0.0, null);
                        // 不与符号表中的任何记录符合则返回错误记号的信息
        return new_token;
    }

    /*
     * 读取一个记号
     */
    public Token getToken() {
        Token get_token = new Token(null, null, 0.0, null); 				// 初始化一个记号
        int Char;														// Char接收getchar的返回值
        Empty_buffer(); 												// 清空缓存区
        while (true) {
            Char = getChar();
            if (Char == -1) { 											// 返回-1代表文件流读取完，所以nontoken记号
                get_token.token_type = Token_type.NONTOKEN;
                CloseScanner(); 										// 文件流读完所以关闭词法分析器
                return get_token;
            }
            else if (Char == '\n')
                Line_num++; 											// 回车使行数增加
            else if (Char == ' ' || Char == '\t' || Char == '\r');		// 遇到空格或制表符跳过不予回应
            else
                break; 												// 若不是tab，空格，回车，文件流结束 则将字符存入缓冲区
        }
        Add_char_buffer((char) Char);
        if (Character.isAlphabetic(Char)) { 							// 判断Char是字母，那么有可能是保留字或者是函数，参数T、常量PI,E
            while (true) {
                Char = getChar();
                if (Character.isAlphabetic(Char) || Character.isDigit(Char))
                    Add_char_buffer((char) Char);						// 如果是字母或者数字则继续向后读
                else if (Char == '\n') {
                    Line_num++;
                    break;
                }
                else
                    break; 										        // 不是字母或数字都应该结束，但是考虑换行符需要增加行数所以单独列出一个分支
            }
            backChar(Char);
            String buffer = new String(SignBuffer); 					// 将buffer的字符数组换为字符串
            buffer = buffer.trim();										// 去除首尾的空格，此处主要是尾部的空格
            get_token = judgeSign(buffer); 							    // 使用judge函数对从缓冲区读出的字符串进行识别
            get_token.sign_name = buffer; 							    // 记号的名字自然为buffer里的内容
            return get_token;
        }
        else if (Character.isDigit(Char)) { 							// 不是字母，则可能是数字
            while (true) {
                Char = getChar();
                if (Character.isDigit(Char))
                    Add_char_buffer((char) Char); 						// 如果一直是数字则不断向后读
                else
                    break;
            }
            if (Char == '.') { 											// 可能读到小数点，跳过，继续享受读，并检查还是不是数字
                Add_char_buffer((char) Char);
                while (true) {
                    Char = getChar();
                    if (Character.isDigit(Char))
                        Add_char_buffer((char) Char);
                    else
                        break;
                }
            }
            backChar(Char);
            String buffer = new String(SignBuffer); 					// 将缓冲区的内容变为字符串
            get_token.token_type = Token_type.CONST_ID; 						// 记号类型为常量
            get_token.value = Double.parseDouble(buffer); 				// 将字符串转化为double类型浮点数赋值给记号值
            return get_token;
        }
        else {  														// 既然既不是数字也不是字母，那就是符号类型的了
            switch (Char) { 											// switch对各种符号讨论各种情况
                case ';': 													// 分号
                    get_token.token_type = Token_type.SEMICO;
                    get_token.sign_name = ";";
                    break;
                case '(': 													// 左括号
                    get_token.token_type = Token_type.L_BRACKET;
                    get_token.sign_name = "(";
                    break;
                case ')': 													// 右括号
                    get_token.token_type = Token_type.R_BRACKET;
                    get_token.sign_name = ")";
                    break;
                case ',': 													// 逗号
                    get_token.token_type = Token_type.COMMA;
                    get_token.sign_name = ",";
                    break;
                case '+': 													// 加法运算符
                    get_token.token_type = Token_type.PLUS;
                    get_token.sign_name = "+";
                    break;
                case '-': 													// 读出 - 符号，但是不确定它是减号或者可能是注释符 -- 的一部分
                    Char = getChar();
                    if (Char == '-') { 										// 分情况，若紧跟其后的符号还是 - 则是注释符
                        while (Char != '\n' && Char != -1) {
                            Char = getChar();								// 忽略其后的内容直到遇到换行符或文件流读取结束
                        }
                        backChar(Char);
                        return getToken(); 									// 重新去识别记号
                    } else { 												// 后面不是 - ，则确定为减号
                        backChar(Char);
                        get_token.token_type = Token_type.MINUS;
                        get_token.sign_name = "-";
                        break;
                    }
                case '/': 													// 情况同 上
                    Char = getChar();
                    if (Char == '/') {
                        while (Char != '\n' && Char != -1) {
                            Char = getChar();
                        }
                        backChar(Char);
                        return getToken();
                    } else {												// 当做除号
                        backChar(Char);
                        get_token.token_type = Token_type.DIV;
                        //get_token.sign_name = "/";
                        break;
                    }
                case '*':
                    Char = getChar();// 乘号
                    if('*' == Char){
                        get_token.token_type = Token_type.POWER;
                    } else {
                        backChar(Char);
                        get_token.token_type = Token_type.MUL;
                        break;
                    }
                    //get_token.sign_name = "*";
                    //break;
                default:
                    get_token.token_type = Token_type.ERRTOKEN; 					// 上面的情况皆未出现，则识别错误，返回错误类型
                    break;
            }
            return get_token;
        }
    }
}
