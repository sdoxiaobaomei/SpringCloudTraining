package org.chai.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FileResolverGUI {

    public static void main(String[] args) {
        try {
            Desktop.getDesktop().open(new File("D:\\让菜菜更轻松"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        JFileChooser jfc=new JFileChooser();
        if(jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
            File file=jfc.getSelectedFile();
            Scanner input= null;
            try {
                input = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while(input.hasNext()){
                System.out.println(input.nextLine());
            }
            input.close();
        }
        else
            System.out.println("No file is selected!");
    }
}
