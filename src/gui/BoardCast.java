package gui;

import parser.Parser;
import scanner.Scanner;
import scanner.Token;
import scanner.Token_type;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by think on 2016/12/9.
 */
public class BoardCast implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        String getbutton = e.getActionCommand();
        switch (getbutton) {
            case "Scanner":
                Scanner x = new Scanner();
                if(!x.InitScanner(UI.file)) {
                    System.out.println("Error");
                    return;
                }
                while (true) {
                    Token y = x.getToken();
                    if( y.token_type != Token_type.NONTOKEN ) {
                        System.out.printf("%12s %12s %12s %12s\n",y.token_type,y.sign_name,y.value,y.func_name);
                    } else break;
                }
                x.CloseScanner();
                break;
            case "Draw":
                Parser y = new Parser();
                y.start_parser(UI.file);
                break;
            default:
                break;
        }
    }
}
