package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by think on 2016/12/9.
 */
public class UI extends JFrame {
    public static File file = null;
    public static Draw pan1;

    public UI() {
        super("Graphic Interpreter");
        setLayout(null);
        Panel top = new Panel(null);
        top.setBackground(Color.WHITE);
        top.setBounds(0,0,800,600);
        top.setVisible(true);
        add(top);

        BoardCast boardcast = new BoardCast();

        Button button2 = new Button("Scanner");
        Button button3 = new Button("Draw");
        TextField filePath = new TextField(30);

        filePath.addActionListener(boardcast);
        button2.addActionListener(boardcast);
        button3.addActionListener(boardcast);

        filePath.setBounds(50,5,100,20);
        button2.setBounds(300,5,90,30);
        button2.setBackground(Color.WHITE);
        button3.setBounds(600,5,90,30);
        button3.setBackground(Color.WHITE);

        filePath.addActionListener(new GavinActionListener());

        top.add(filePath);
        top.add(button2);
        top.add(button3);

        pan1 = new Draw();
        pan1.setLayout(null);
        pan1.setBounds(0,50,800,550);
        pan1.setBackground(Color.WHITE);
        pan1.setVisible(true);
        top.add(pan1);


        setBounds(0,0,800,600);
        setResizable(false);
        setVisible(true);



    }
}
