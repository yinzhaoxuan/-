package semantic;

import gui.UI;
import parser.ExprNode;
import parser.Parser;
import scanner.Scanner;


public class Semantic {
    public static double Start, End, Step,x = 0,y = 0;							//  绘图起点，终点，步长
    public static ExprNode start_ptr, end_ptr, step_ptr, x_ptr, y_ptr;			//  各表达式的语法树节点指针

    public static double GetExprValue (ExprNode root){
        if (root == null)
            return 0.0;
        switch (root.OpCode) {
            case PLUS:
                return GetExprValue(root.co.Left) + GetExprValue(root.co.Right);
            case MINUS:
                return GetExprValue(root.co.Left) - GetExprValue(root.co.Right);
            case MUL:
                return GetExprValue(root.co.Left) * GetExprValue(root.co.Right);
            case DIV:
                return GetExprValue(root.co.Left) / GetExprValue(root.co.Right);
            case POWER:
                return Math.pow(GetExprValue(root.co.Left), GetExprValue(root.co.Right));
            case FUNC:
                return FunUtils.matchFun(root.cf.FuncPtr, GetExprValue(root.cf.Child));
            case CONST_ID:
                return root.CaseConst;
            case T:
                return Scanner.T;
            default:
                return 0.0;
        }
    }

    public static void DelExprTree (ExprNode root){
        if (root == null)
            return;
        switch (root.OpCode) {
            case PLUS:
            case MINUS:
            case MUL:
            case DIV:
            case POWER:
                DelExprTree(root.co.Left);
                DelExprTree(root.co.Right);
                break;
            case FUNC:
                DelExprTree(root.cf.Child);
                break;
            default:
                break;
        }
        root = null;
    }

    public static void CalPara(ExprNode Hor_Ptr, ExprNode Ver_Ptr){

        double para;

        Semantic.x = GetExprValue(Hor_Ptr);
        Semantic.y = GetExprValue(Ver_Ptr);

        Semantic.x *= Parser.Scale_x;
        Semantic.y *= Parser.Scale_y;

        para = Semantic.x * Math.cos(Parser.Rot_angle) + Semantic.y * Math.sin(Parser.Rot_angle);
        Semantic.y = Semantic.y * Math.cos(Parser.Rot_angle) + Semantic.x * Math.sin(Parser.Rot_angle);
        Semantic.x = para;

        Semantic.x += Parser.Origin_x;
        Semantic.y += Parser.Origin_y;
    }

    public static void DrawLoop(double Start, double End, double Step, ExprNode x_ptr, ExprNode y_ptr) {
        for(Scanner.T = Start; Scanner.T <= End; Scanner.T += Step) {
            Semantic.CalPara(Semantic.x_ptr, Semantic.y_ptr);
            //System.out.println(""+Semantic.x + Semantic.y);
            UI.pan1.repaint();
        }
    }
}

