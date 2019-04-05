/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvsomecode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 */
public class RiscV_Parser_Errors_Summary {

    static char TAG;//l for label, i for instruvtion
    static String[] Ops = {"LW", "LH", "LHU", "SH", "LB", "LBU", "SB", "LUI", "SW", "SLL", "SRL", "SRA", "ADD", "SUB", "XOR","XORI", "OR", "AND", "SLT",
        "SLTU", "SLLI", "SRLI", "SRAI", "LUI", "AUIPC", "JAL", "ADDI", "ORI", "ANDI", "SLTI", "SLTIU",
        "BEQ", "BNE", "BLT", "BGE", "BLTU", "BGEU", "JALR", "FENCE", "FENCE.I", "SCALL", "SBRAKE",
        "lw", "lh", "lhu", "sh", "lb", "lbu", "sb", "sw", "sll", "srl", "sra", "add", "sub", "xor", "or", "and", "slt", "sltu", "slli", "srli", "srai", "lui",
        "auipc", "jal", "addi", "ori","xori","andi", "slti", "sltiu", "beq", "bne", "blt", "bge", "bltu", "bgeu", "jalr",
        "fence", "fence.i", "scall", "sbrake"};
    
    static String[] LoadOP = {"LW", "LH", "LHU", "SH","SW", "LB", "LBU", "SB", "JALR",
        "lw", "lh", "lhu", "sh","sw", "lb", "lbu", "sb", "jalr"};

    static char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ':', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' ', ';', '.', '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', ',', '(', ')','-'};
    
    static String deleteWS_errMSG = "";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {

        String file = "/home/daniel/NetBeansProjects/RVSomeCode/src/rvsomecode/code.s";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line_first;
            int i = 1;
            while ((line_first = br.readLine()) != null) {
                String[] line_second = line_first.split(";");
                String line = line_second[0];
                if(!"".equals(line)){
                //System.out.println("line-" + i + ">" + line);
                String LineToReview = deleteWS(line);
                System.out.println("line-" + i + ">" + LineToReview);
                System.out.println(deleteWS_errMSG);
                deleteWS_errMSG = "";
                parseLine(LineToReview);
                i++;
                }
                //else{System.out.println("blank space");}
            }
        } catch (IOException ex) {
            Logger.getLogger(RiscV_Parser_Errors_Summary.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static void parseLine(String newLine) {
        checkFormat(newLine);
        checkRegisters(split(newLine));
    }

    static void checkRegisters(String[] newLine) {
        //System.out.println("registers");
        boolean label = isLabel(newLine[0]);
        int tamano = newLine.length;
        if(tamano == 3 && !label){
            if(containChain(newLine[0])){
                checkReg(newLine[1]);
                checkLoadReg(newLine[2]);
            }
            else if(newLine[0].equals("jal")||newLine[0].equals("JAL")){
                checkReg(newLine[1]);
                checkImm(newLine[2]);
            }
            else{
                System.out.println("Error: Incomplete number of parameters for "+newLine[0]);
            }
        }
        else if(tamano == 4 && label){//--
            if(containChain(newLine[1])){
                checkReg(newLine[2]);
                checkLoadReg(newLine[3]);
            }
            else if(newLine[1].equals("jal")||newLine[1].equals("JAL")){
                checkReg(newLine[2]);
                checkImm(newLine[3]);
            }
            else {
                System.out.println("ERROR: Bad parameters especification for "+newLine[1]);
            }
        }
        else if (label && tamano==5) {
            checkReg(newLine[2]);
            checkReg(newLine[3]);
            if(newLine[1].contains("i")||newLine[1].charAt(0) == 'b'||newLine[1].charAt(0) == 'B'){
                
                checkImm(newLine[4]);
                
            }else{
                checkReg(newLine[4]);
            }
        } 
        else if (label==false && tamano==4 && newLine[0].contains("i") ) {
            checkReg(newLine[1]);
            checkReg(newLine[2]);
            checkImm(newLine[3]);
            
        }
        else if (label && tamano==4) {
            String instr = newLine[1];
            if(instr.equals("jal")||instr.equals("JAL")||instr.equals("lui")||instr.equals("LUI")){
                checkReg(newLine[2]);
                checkImm(newLine[3]);
            }else{
                System.out.println("Error: Incomplete number of parameters for "+instr);
            }
        } 
        else if(tamano==4 && !label){
            
             checkReg(newLine[1]);
            checkReg(newLine[2]);
            if(newLine[0].contains("i")||newLine[0].charAt(0) == 'b'||newLine[0].charAt(0) == 'B'){//------------------------
                checkImm(newLine[3]);
            }else{
                checkReg(newLine[3]);
            }
        }
        
        else if(tamano==3){
            String instr = newLine[0];
            if(instr.equals("jal")||instr.equals("JAL")||instr.equals("lui")||instr.equals("LUI")){
                checkReg(newLine[1]);
                checkImm(newLine[2]);
            }else{
                System.out.println("Error: Incomplete number of parameters for "+instr);
            }
        
        }
        else{
            System.out.println("Error: unknown parameters format");
        }

    }
    //static void checkThreeOP(String Op1, String Op2, String Op3){
    
    //}
    static boolean containChain(String str){
        boolean contained = false;
        for (int i = 0; i < LoadOP.length; i++) {
            if(str.equals(LoadOP[i])){
                contained = true;
                break;
            }
        }
        return contained;
    }

    static String[] split(String line) {
        String Chain = "";
        for (int i = 0; i < line.length(); i++) {
            char str = line.charAt(i);
            if (str == ' ') {
                Chain += ',';
            } else if (str == ':') {
                Chain += "*,";
            } else {
                Chain += line.charAt(i);
            }
        }
        return Chain.split(",");
    }

    static void checkFormat(String newLine) {
        String firstPart = "";
        //String line = lineNew.replace(' ', '-');
        char CHAR_;
        for (int i = 0; i < newLine.length(); i++) {
            CHAR_ = newLine.charAt(i);
            if (!isCharOk(CHAR_)) {
                System.out.println("ERROR CHAR NOT ALLOWED: >>> " + CHAR_ + " <<<");
                break;
            }
            if (CHAR_ == ':' && TAG == 'l') {//(CHAR_ == ' ' ||CHAR_ == ':')
                if (isOP(firstPart)) {
                    System.out.println("Label can not have instruction name");
                }
                TAG = 'i';
                firstPart = "";
                continue;
            }
            if (CHAR_ == ',' && TAG == 'l') {//(CHAR_ == ' ' ||CHAR_ == ':')
                System.out.println("This label is bad!!!!->" + firstPart);
                break;
            }
            if (CHAR_ == ' ' && TAG == 'i') {
                if (!isOP(firstPart)) {
                    System.out.println("ERROR: bad instruction");
                } else {
                    //System.out.println("Info: good instruction");
                    TAG = 'x';
                    firstPart = "";
                    continue;
                }
            }
            if (CHAR_ == ',' && TAG == 'i') {
                if (!isOP(firstPart)) {
                    System.out.println("ERROR: bad instruction");
                } else {
                    System.out.println("ERROR: good instruction but register spected: ',' found");
                    TAG = 'x';
                    firstPart = "";
                    continue;
                }
            }
            firstPart += CHAR_;

        }
    }

    static void checkImm(String Imm) {
        try {
            int result = Integer.parseInt(Imm);
            if(!(result < 2048 && result >= -2048)){//−(2 exp 11) to (2 exp 11)−1.
                System.out.println("Immediate must be in limit −(2 exp 11) to (2 exp 11)−1.");
            }
        } catch (Exception e) {
            System.out.println("Error: Bad Immediate format.");

        }
    }
    
    static void checkRegNumber(String number) {
        try {
            int result = Integer.parseInt(number);
            if(!(result < 32 && result >= 0)){//−(2 exp 11) to (2 exp 11)−1.
                System.out.println("Error: Register must be in 0-31 range.");
            }
        } catch (Exception e) {
            System.out.println("Error: Bad Register format.");

        }
    }
    
    static void checkReg(String xReg){
        int tamano = xReg.length();
        if(tamano==2){
            char reg = xReg.charAt(0);
            if(reg=='x'|| reg=='X'){
                if(!isNumberCharOk(xReg.charAt(1))){
                    System.out.println("Error: Bad Register format.");
                }
            }else{
                System.out.println("Error: Bad input register waiting for 'x' found-> "+reg);
            }
        }
        else if(tamano==3){
            System.out.println(xReg.charAt(0));
            System.out.println(xReg.charAt(1));
            System.out.println(xReg.charAt(2));
            char reg = xReg.charAt(0);
            if(reg=='x'|| reg=='X'){
                String regNumber = "";
                regNumber += xReg.charAt(1);
                regNumber += xReg.charAt(2);
                checkRegNumber(regNumber);
            }
            else{
                System.out.println("Error: Bad Register format found 3-> "+reg);
            }
        }
        else{
            System.out.println("Error: Bad Register format.");
        }
    }

    static boolean isLabel(String label) {
        boolean isLabel = (label.charAt(label.length() - 1) == '*');
        return isLabel;
    }

    static boolean isCharOk(char i) {
        boolean isOk = false;
        for (int j = 0; j < alphabet.length; j++) {
            if (i == alphabet[j]) {
                isOk = true;
                break;
            }
        }
        return isOk;

    }
    
    static void checkLoadReg(String reg){
        
        if(reg.length()<4){
            System.out.println("Error: Bad expresion format at ->"+reg);
        }
        else if(reg.length() < 5 && reg.charAt(0)!= '('){
            System.out.println("Error: Bad expresion format at ->"+reg);
        }
        else if(reg.charAt(reg.length()-4)=='('&& reg.charAt(reg.length()-1)==')'){
            String xReg = "";
            xReg += reg.charAt(reg.length()-3);
            xReg += reg.charAt(reg.length()-2);
            checkReg(xReg);
            int range = reg.length()-4;
            String suma = "";
            char number;
            for (int i = 0; i < range; i++) {
                suma += reg.charAt(i);
                if(!isNumberCharOk(reg.charAt(i))){
                    System.out.println("Error: Bad number Imm");//implementar revision del tamano; maximo 4095
                }
            }
            
        }
        else if(reg.charAt(reg.length()-5)=='('&& reg.charAt(reg.length()-1)==')'){
            String xReg = "";
            xReg += reg.charAt(reg.length()-4);
            xReg += reg.charAt(reg.length()-3);
            xReg += reg.charAt(reg.length()-2);
            checkReg(xReg);
            int range = reg.length()-5;
            for (int i = 0; i < range; i++) {
                if(!isNumberCharOk(reg.charAt(i))){
                    System.out.println("Error: Bad number Imm");//implementar revision del tamano; maximo 4095
                }
            }
        }
        else{
            System.out.println("Error: Can't manage this expression -> "+reg);
        }
    }

    static boolean isNumberCharOk(char i) {
        boolean isOk = false;
        switch (i) {
            case '0':
                isOk = true;
                break;
            case '1':
                isOk = true;
                break;
            case '2':
                isOk = true;
                break;
            case '3':
                isOk = true;
                break;
            case '4':
                isOk = true;
                break;
            case '5':
                isOk = true;
                break;
            case '6':
                isOk = true;
                break;
            case '7':
                isOk = true;
                break;
            case '8':
                isOk = true;
                break;
            case '9':
                isOk = true;
                break;
            default:
                isOk = false;

        }
        return isOk;

    }

    static boolean isOP(String label) {
        boolean isOk = false;
        for (int j = 0; j < Ops.length; j++) {
            if (Ops[j].equals(label)) {
                isOk = true;
                break;
            }
        }
        return isOk;

    }

    static String deleteWS(String line) {
        String newLine = "";
        char CHAR;
        int i;
        int status = 0;
        boolean label = false;
        for (int y = 0; y < line.length(); y++) {
            CHAR = line.charAt(y);
            if (CHAR == ':') {
                label = true;
                break;
            }
        }
        i = 0;
        if (label) {
            TAG = 'l';
            int badLBL = 0;
            for (i = 0; i < line.length(); i++) {
                CHAR = line.charAt(i);
                if (CHAR == ':') {
                    if (badLBL != 0) {
                        deleteWS_errMSG += "Error: Bad Label format";
                        
                    }
                    newLine += CHAR;
                    i++;
                    break;
                }
                if (CHAR == ' ' && status == 0) {

                    continue;
                }
                if (CHAR == ' ' && status == 1) {
                    badLBL += 1;
                    continue;
                }
                status = 1;
                newLine += CHAR;
            }
        } else {
            TAG = 'i';
        }
        int j;
        for (j = i; j < line.length(); j++) {
            CHAR = line.charAt(j);
            if (CHAR != ' ') {
                newLine += CHAR;
                break;
            }
            //newLine += CHAR;
        }
        int k;
        for (k = j + 1; k < line.length(); k++) {
            CHAR = line.charAt(k);
            if (CHAR == ' ') {
                newLine += ' ';
                break;
            }
            newLine += CHAR;
        }

        for (int l = k + 1; l < line.length(); l++) {
            CHAR = line.charAt(l);
            if (CHAR == ' ') {
                continue;
            }
            newLine += CHAR;
        }

        return newLine;
    }
}
