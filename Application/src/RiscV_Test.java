/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvsomecode;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author daniel
 */
public class RiscV_Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        /*Memory mem = new Memory();
        mem.storeWord(0, "230");
        mem.storeWord(1, "1");
        mem.storeWord(5, "5");
        mem.storeWord(4, "1");
        mem.storeWord(-1, "10");
        mem.storeWord(-2, "11");
//        System.out.println("readed value "+mem.getFromMemory(2));
        mem.printMemoryValues();
        //System.out.println("from.."+mem.getFromMemory(1000000000));*/
        int i = 0;
        String e = Integer.toBinaryString(i);
        System.out.println("binary -> "+e);
        if(i==0){
            e = "00000000000000000000000000000000";
        }
        else if(i>0){
            int lenght = e.length();
            int offset = 32-lenght;
            String part = "";
            for (int j = 0; j < offset; j++) {
                part += "0";
            }
            part  += e;
            e = part;
        }
        String data = String.copyValueOf(e.toCharArray(), 20, 12);
        System.out.println("this fixed -> "+data);
        
        
    }

}
