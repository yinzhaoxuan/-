package parser;

import scanner.Scanner;
import scanner.Token;
import scanner.Token_type;
import semantic.Semantic;

import java.io.File;

import static java.lang.System.exit;
import static scanner.Token_type.*;

/**
 * Created by think on 2016/12/7.
 */
public class Parser implements parserInf{
    public static  double Origin_x = 0,// 横平移距离
                          Origin_y = 0,//纵平移距离
                          Scale_x = 0,//纵比例因子
                          Scale_y = 0,//纵比例因子
                          Rot_angle = 0;// 旋转角度
    public Token token; // 记号
    public static Scanner scanner = new Scanner();

    public void enter(String s){
        System.out.println("Enter in" + s);
    }

    public void back(String s){
        System.out.println("back in" + s);
    }

    public void call_match(String s){
        System.out.println("match in" + s);
    }

    /**
     * 通过词法分析器接口GetToken获取一个记号
     * */

    public void FetchToken(){
        token = scanner.getToken();
        if(ERRTOKEN == token.token_type) {
            SyntaxError(1);
        }
    }

    /**
     *匹配记号
     * @param  the_token 记号表
     *
     * */

    public void MatchToken(Token_type the_token){
        if(the_token != token.token_type){
            SyntaxError(2);
        }
        FetchToken();
    }

    /**
     * 语法错误处理
     * @param case_of 错误类型
     * line_num 行数
     * */

    public void SyntaxError(int case_of) {
        switch(case_of) {
            case 1:
                ErrMsg(scanner.Line_num,"错误记号",token.sign_name);
                break;
            case 2:
                ErrMsg(scanner.Line_num,"不是预期记号",token.sign_name);
                break;
        }
    }

    /**
     * 打印错误信息
     * @param Line_num
     * @param descrip
     * @param string
     * */

    public void ErrMsg(int Line_num,String descrip,String string){
        System.out.println("LineNo:"+Line_num+descrip+string);
        scanner.CloseScanner(); //关闭
        exit(1);
    }

    /**
     * 先序遍历并打印表达式的语法树
     * @param root
     * @param indent
     * */
    public void PrintSyntaxTree(ExprNode root,int indent)
    {
        int temp;
        for(temp=1;temp<=indent;temp++) // 缩进
        {
            System.out.print("\t");
        }
        switch(root.OpCode)// 打印根节点
        {
            case PLUS :
                System.out.println("+");
                break;
            case MINUS :
                System.out.println("-");
                break;
            case MUL :
                System.out.println("*");
                break;
            case DIV :
                System.out.println("/");
                break;
            case POWER :
                System.out.println("**");
                break;
            case FUNC :
                System.out.println(root.cf.FuncPtr);
                break;
            case CONST_ID :
                System.out.println(root.CaseConst);
                break;
            case T :
                System.out.println("T");
                break;
            default :
                System.out.println("Error Tree Node !");
                exit(0);
        }
        if(CONST_ID == root.OpCode || T == root.OpCode)// 叶子节点返回
        {
            return ;
        }
        if(FUNC == root.OpCode)// 递归打印一个孩子的节点
        {
            PrintSyntaxTree(root.cf.Child,indent+1);
        } else { // 递归打印两个孩子的节点
            PrintSyntaxTree(root.co.Left,indent+1);
            PrintSyntaxTree(root.co.Right,indent+1);
        }
    }

    /**
     * Program的递归子程序
     * */
    public void Program()
    {
        enter("Program");
        while(NONTOKEN != token.token_type)
        {
            Statement();
            MatchToken(SEMICO);
        }
        back("Program");
    }

    /**
     * Statement的递归子程序
     * */

    public void Statement()
    {
        enter("Statement");
        switch(token.token_type)
        {
            case ORIGIN :
                OriginStatement();
                break;
            case SCALE :
                ScaleStatement();
                break;
            case ROT :
                RotStatement();
                break;
            case FOR :
                ForStatement();
                break;
            default :
                SyntaxError(2);
        }
        back("Statement");
    }

    /**
     * OriginStatement的递归子程序
     *
     * */
    public void OriginStatement()
    {
        ExprNode tmp;
        enter("OriginStatement");
        MatchToken(ORIGIN);
        MatchToken(IS);
        MatchToken(L_BRACKET);
        tmp = Expression();
        Origin_x = Semantic.GetExprValue(tmp); // 获取横坐标的平移距离
        Semantic.DelExprTree(tmp);
        MatchToken(COMMA);
        tmp = Expression();
        Origin_y = Semantic.GetExprValue(tmp); // 获取纵坐标的平移距离
        Semantic.DelExprTree(tmp);
        MatchToken(R_BRACKET);
        back("OriginStatement");
        System.out.println(Origin_x+Origin_y);
    }

    /**
     * ScaleStatement的递归子程序
     * */

    public void ScaleStatement()
    {
        ExprNode tmp;
        enter("ScaleStatement");
        MatchToken(SCALE);
        MatchToken(IS);
        MatchToken(L_BRACKET);
        tmp = Expression();
        Scale_x = Semantic.GetExprValue(tmp); // 获取横坐标的比例因子
        Semantic.DelExprTree(tmp);
        MatchToken(COMMA);
        tmp = Expression();
        Scale_y = Semantic.GetExprValue(tmp); // 获取纵坐标的比例因子
        Semantic.DelExprTree(tmp);
        MatchToken(R_BRACKET);
        back("ScaleStatement");
    }

    /**
     *RotStatement的递归子程序
     * */

    public void RotStatement()
    {
        ExprNode tmp;
        enter("RotStatement");
        MatchToken(ROT);
        MatchToken(IS);
        tmp = Expression();
        Rot_angle = Semantic.GetExprValue(tmp); // 获取旋转角度
        Semantic.DelExprTree(tmp);
        back("RotStatement");
    }

    /**
     * ForStatement 的递归子程序
     *
     * */

    public  void ForStatement() {

        enter("ForStatement");
        MatchToken(FOR);
        call_match("FOR");
        MatchToken(T);
        call_match("T");
        MatchToken(FROM);
        call_match("FROM");

        Semantic.start_ptr = Expression();// 构造参数起始表达式语法树
        Semantic.Start = Semantic.GetExprValue(Semantic.start_ptr); // 计算参数起始表达式的值
        Semantic.DelExprTree(Semantic.start_ptr); // 释放参数起始语法树所占空间
        MatchToken(TO);
        call_match("TO");
        Semantic.end_ptr = Expression(); // 构造参数终结表达式语法树
        Semantic.End = Semantic.GetExprValue(Semantic.end_ptr); // 计算参数结束表达式的值
        Semantic.DelExprTree(Semantic.end_ptr); // 释放参数结束语法树所占空间
        MatchToken(STEP);
        call_match("STEP");
        Semantic.step_ptr = Expression();// 构造参数步长表达式语法树
        Semantic.Step = Semantic.GetExprValue(Semantic.step_ptr);// 计算参数步长表达式的值
        Semantic.DelExprTree(Semantic.step_ptr);// 释放参数步长语法树所占空间
        MatchToken(DRAW);
        call_match("DRAW");
        MatchToken(L_BRACKET);
        call_match("(");
        Semantic.x_ptr = Expression();// 构造横坐标表达式语法树
        MatchToken(COMMA);
        call_match(",");
        Semantic.y_ptr = Expression();// 构造纵坐标表达式语法树
        MatchToken(R_BRACKET);
        call_match(")");
        System.out.println(Semantic.Start+" "+Semantic.End+" "+Semantic.Step);
        Semantic.DrawLoop(Semantic.Start,Semantic.End,Semantic.Step,Semantic.x_ptr,Semantic.y_ptr); // 绘制图形
        Semantic.DelExprTree(Semantic.x_ptr);// 释放横坐标语法树所占空间
        Semantic.DelExprTree(Semantic.y_ptr);// 释放纵坐标语法树所占空间
        back("ForStatement");
    }

    /**
     * Expression 的递归子程序
     *
     * */

    public ExprNode Expression()
    {
        ExprNode  left, right; // 左右子树节点的指针
        Token_type token_tmp; // 当前记号

        enter("Expression");
        left = Term(); // 分析左操作数且得到其语法树
        while(PLUS == token.token_type || MINUS == token.token_type)
        {
            token_tmp = token.token_type;
            MatchToken(token_tmp);
            right = Term(); // 分析右操作数且得到其语法树
            left = MakeExprNode(token_tmp,left,right); // 构造运算的语法树，结果为左子树
        }
        Tree_trace(left); // 打印表达式的语法树
        back("Expression");
        return left; // 返回最终表达式的语法树
    }

    /**
     *Term 的递归子程序
     * */

    public ExprNode Term()
    {
        ExprNode left, right;
        Token_type token_tmp;

        left = Factor();
        while(MUL == token.token_type || DIV == token.token_type)
        {
            token_tmp = token.token_type;
            MatchToken(token_tmp);
            right = Factor();
            left = MakeExprNode(token_tmp,left,right);
        }
        return left;
    }

    /**
     *Factor的递归子程序
     * */

    public ExprNode Factor()
    {
         ExprNode  left, right;

        if(PLUS == token.token_type) // 匹配一元加运算
        {
            MatchToken(PLUS);
            right = Factor(); //  表达式退化为仅有右操作数的表达式
        } else if(MINUS == token.token_type) { // 匹配一元减运算
            MatchToken(MINUS); // 表达式转化为二元减运算的表达式
            right = Factor();
            left = new ExprNode();
            left.OpCode = CONST_ID;
            left.CaseConst = 0.0;
            right = MakeExprNode(MINUS,left,right);
        } else { // 匹配非终结符Component
            right = Component();
        }
        return right;
    }

    /**
     * Component的递归子程序
     *
     * */

    public ExprNode Component()
    {
         ExprNode  left, right;

        left = Atom();
        if(POWER == token.token_type)
        {
            MatchToken(POWER);
            right = Component(); // 递归调用Component以实现POWER的右结合
            left = MakeExprNode(POWER,left,right);
        }
        return left;
    }

    /**
     * Atom的递归子程序
     *
     * */

    public ExprNode Atom()
    {
        Token t = token;

        ExprNode address = null, tmp;

        switch(token.token_type)
        {
            case CONST_ID :
                MatchToken(CONST_ID);
                address = MakeExprNode(CONST_ID,t.value);
                break;
            case T :
                MatchToken(T);
                address = MakeExprNode(T);
                break;
            case FUNC :
                MatchToken(FUNC);
                MatchToken(L_BRACKET);
                tmp = Expression();
                address = MakeExprNode(FUNC,t.func_name,tmp);
                MatchToken(R_BRACKET);
                break;
            case L_BRACKET :
                MatchToken(L_BRACKET);
                address = Expression();
                MatchToken(R_BRACKET);
                break;
            default :
                SyntaxError(2);
        }
        return address;
    }

    /**
     * 生成语法树的一个节点
     */

    static ExprNode MakeExprNode( Token_type opcode,Object...obj)
    {
        ExprNode  ExprPtr = new ExprNode();
        ExprPtr.OpCode = opcode; // 接收记号的类别
        switch(opcode) // 根据记号的类别构造不同的节点
        {
            case CONST_ID : // 常数节点
                ExprPtr.CaseConst = (double)obj[0];
                break;
            case T : // 参数节点
                break;
            case FUNC : // 函数调用节点
                ExprPtr.cf = new CaseFunc();
                ExprPtr.cf.FuncPtr = (String) obj[0];
                ExprPtr.cf.Child = (ExprNode)obj[1];
                break;
            default : // 二元运算节点
                ExprPtr.co = new CaseOperator();
                ExprPtr.co.Left = (ExprNode) obj[0];
                ExprPtr.co.Right = (ExprNode) obj[1];
                break;
        }
        return ExprPtr;
    }

    public void Tree_trace(ExprNode x) {
        PrintSyntaxTree(x,1);
        return;
    }

    public void start_parser(File SrcFilePtr)
    {	enter("Parser");
        if(!scanner.InitScanner(SrcFilePtr))	// 初始化词法分析器
        {
            System.out.println("Open Source File Error !");
            return;
        }
        FetchToken();					// 获取第一个记号
        Program();						// 递归下降分析
        scanner.CloseScanner();					// 关闭词法分析器
        back("Parser");
        return;
    }
}
