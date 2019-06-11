/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvsomecode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 */
public class RiscvAnalyzer {

    boolean isThereErrors = false;
    int line_counter = 0;
    int syntax_line_counter = 1;
    String[][] InstructionBuffer;

    char TAG;//l for label, i for instruvtion
    String[] Ops = {"LW", "LH", "LHU", "SH", "LB", "LBU", "SB", "LUI", "SW", "SLL", "SRL", "SRA", "ADD", "SUB", "XOR", "XORI", "OR", "AND", "SLT",
        "SLTU", "SLLI", "SRLI", "SRAI", "LUI", "AUIPC", "JAL", "ADDI", "ORI", "ANDI", "SLTI", "SLTIU",
        "BEQ", "BNE", "BLT", "BGE", "BLTU", "BGEU", "JALR", "FENCE", "FENCE.I", "SCALL", "SBRAKE",
        "lw", "lh", "lhu", "sh", "lb", "lbu", "sb", "sw", "sll", "srl", "sra", "add", "sub", "xor", "or", "and", "slt", "sltu", "slli", "srli", "srai", "lui",
        "auipc", "jal", "addi", "ori", "xori", "andi", "slti", "sltiu", "beq", "bne", "blt", "bge", "bltu", "bgeu", "jalr",
        "fence", "fence.i", "scall", "sbrake"};

    String[] LoadOP = {"LW", "LH", "LHU", "SH", "SW", "LB", "LBU", "SB", "JALR",
        "lw", "lh", "lhu", "sh", "sw", "lb", "lbu", "sb", "jalr"};

    char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ':', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' ', ';', '.', '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', ',', '(', ')', '-'};

    String deleteWS_errMSG = "";
    String Parser_errMSG = "";
    int emptyFile_Flag;

    public RiscvAnalyzer() {
    }

    public void makeAnalysis(String file_name) throws IOException {
        clean();
        InstructionBuffer = new String[200][10];
        emptyFile_Flag = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
            String line_first;
            int i = 0;
            while ((line_first = br.readLine()) != null) {
                emptyFile_Flag = 0;
                String[] line_second = line_first.split(";");
                if (line_second.length == 0) {
                    continue;
                }
                String line = line_second[0];
                if (!"".equals(line)) {

                    String LineToReview = deleteWS(line);
                    //System.out.println("line-" + i + ">" + LineToReview); //print after formating
                    //System.out.print(deleteWS_errMSG);
                    errorLog(deleteWS_errMSG, syntax_line_counter);
                    deleteWS_errMSG = "";
                    parseLine(LineToReview, i);
                    //System.out.println(Parser_errMSG);
                    errorLog(Parser_errMSG, syntax_line_counter);
                    Parser_errMSG = "";
                    i++;
                    syntax_line_counter++;
                } else {
                    syntax_line_counter++;
                }
                //else{System.out.println("blank space");}
            }
            if (emptyFile_Flag == 1) {
                isThereErrors = true;
                errorLog("EMPTY FILE", 0);
                System.out.println("ERRORS >> " + isThereErrors);

            } else {
                System.out.println("ERRORS >> " + isThereErrors);
            }
            System.out.println("Number of lines: " + line_counter);
        } catch (IOException ex) {
            Logger.getLogger(RiscV_Test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void clean() throws IOException {
        //sim.reg.initRegisters();
        syntax_line_counter = 1;
        line_counter = 0;
        isThereErrors = false;
        new FileWriter("ERRORS.log", false).close();

    }

    private void errorLog(String msg, int line_counter_) {

        if (!msg.equals("")) {
            String data = "";
            data += msg + " At Line --> " + line_counter_ + "\n";
            BufferedWriter writer = null;
            try {
                // TODO add your handling code here:
                writer = new BufferedWriter(new FileWriter("ERRORS.log", true));
                writer.append(data);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private void parseLine(String newLine, int lineNumber) {
        checkFormat(newLine);
        String[] line = split(newLine);
        checkRegisters(line);
        String[] line2 = insertLineNumber(line, lineNumber);
        InstructionBuffer[lineNumber] = checkDTI(line2);
        line_counter++;

    }

    private String[] insertLineNumber(String[] line, int number) {
        String[] out = new String[line.length + 1];
        out[0] = String.valueOf(4 * number);
        for (int i = 0; i < line.length; i++) {
            out[i + 1] = line[i];
        }
        return out;

    }

    private void checkRegisters(String[] newLine) {
        //System.out.println("registers");
        boolean label = isLabel(newLine[0]);
        int tamano = newLine.length;
        if (tamano == 3 && !label) {
            if (containChain(newLine[0])) {
                checkReg(newLine[1]);
                checkLoadReg(newLine[2]);
            } else if (newLine[0].equals("jal") || newLine[0].equals("JAL")) {
                checkReg(newLine[1]);
                checkImm(newLine[2]);
            } else {
                Parser_errMSG += "Error: Incomplete number of parameters for " + newLine[0];
                System.out.println("Error: Incomplete number of parameters for " + newLine[0]);
                isThereErrors = true;
            }
        } else if (tamano == 4 && label) {//--
            if (containChain(newLine[1])) {
                checkReg(newLine[2]);
                checkLoadReg(newLine[3]);
            } else if (newLine[1].equals("jal") || newLine[1].equals("JAL")) {
                checkReg(newLine[2]);
                checkImm(newLine[3]);
            } else {
                Parser_errMSG += "ERROR: Bad parameters especification for " + newLine[1];
                System.out.println("ERROR: Bad parameters especification for " + newLine[1]);
                isThereErrors = true;
            }
        } else if (label && tamano == 5) {
            checkReg(newLine[2]);
            checkReg(newLine[3]);
            if (newLine[1].contains("i") || newLine[1].charAt(0) == 'b' || newLine[1].charAt(0) == 'B' || isDataTransOP(newLine[1])) {//uy
                checkImm(newLine[4]);

            } else {
                checkReg(newLine[4]);
            }
        } else if (label == false && tamano == 4 && (newLine[0].contains("i") || newLine[0].contains("I"))) {
            checkReg(newLine[1]);
            checkReg(newLine[2]);
            checkImm(newLine[3]);

        } else if (label && tamano == 4) {
            String instr = newLine[1];
            if (instr.equals("jal") || instr.equals("JAL") || instr.equals("lui") || instr.equals("LUI")) {
                checkReg(newLine[2]);
                checkImm(newLine[3]);
            } else {
                Parser_errMSG += "Error: Incomplete number of parameters for " + instr;
                System.out.println("Error: Incomplete number of parameters for " + instr);
                isThereErrors = true;
            }
        } else if (tamano == 4 && !label) {

            checkReg(newLine[1]);
            checkReg(newLine[2]);
            if (newLine[0].contains("i")) {//------------------------
                checkImm(newLine[3]);
            } else if (newLine[0].charAt(0) == 'b' || newLine[0].charAt(0) == 'B') {
                checkBranchOperandSource(newLine[3]);
            } else {
                checkReg(newLine[3]);
            }
        } else if (tamano == 3) {
            String instr = newLine[0];
            if (instr.equals("jal") || instr.equals("JAL") || instr.equals("lui") || instr.equals("LUI")) {
                checkReg(newLine[1]);
                checkImm(newLine[2]);
            } else {
                Parser_errMSG += "Error: Incomplete number of parameters for " + instr;
                System.out.println("Error: Incomplete number of parameters for " + instr);
                isThereErrors = true;
            }

        } else {
            if (newLine.length == 1 && newLine[0].charAt(newLine[0].length() - 1) == '*') {

                checkLabel(newLine[0]);

            } else {
                Parser_errMSG += "Error: unknown >> " + newLine[0] + " <<";
                System.out.println("Error: unknown >> " + newLine[0] + " <<");
                isThereErrors = true;
            }

        }

    }

    private void checkBranchOperandSource(String op_value) {
        if (isaNumber(op_value)) {
            checkImm(op_value);
        } else {
            checkLabel(op_value);
        }
    }

    private boolean isaNumber(String num) {
        boolean result = true;//true if it is a number
        char _char;
        for (int i = 0; i < num.length(); i++) {
            _char = num.charAt(i);
            if (!isNumberCharOk(_char)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private int checkLabel(String label) {
        int goodchar = 0;
        for (int i = 0; i < label.length(); i++) {
            switch (label.toLowerCase().charAt(i)) {
                case 'a':
                    break;
                case 'b':
                    break;
                case 'c':
                    break;
                case 'd':
                    break;
                case 'e':
                    break;
                case 'f':
                    break;
                case 'g':
                    break;
                case 'h':
                    break;
                case 'i':
                    break;
                case 'j':
                    break;
                case 'k':
                    break;
                case 'l':
                    break;
                case 'm':
                    break;
                case 'n':
                    break;
                case 'o':
                    break;
                case 'p':
                    break;
                case 'q':
                    break;
                case 'r':
                    break;
                case 's':
                    break;
                case 't':
                    break;
                case 'u':
                    break;
                case 'v':
                    break;
                case 'w':
                    break;
                case 'x':
                    break;
                case 'y':
                    break;
                case 'z':
                    break;
                case '0':
                    break;
                case '1':
                    break;
                case '2':
                    break;
                case '3':
                    break;
                case '4':
                    break;
                case '5':
                    break;
                case '6':
                    break;
                case '7':
                    break;
                case '8':
                    break;
                case '9':
                    break;
                case '*':
                    break;
                default:
                    goodchar = 1;
            }
            if (goodchar == 1) {
                isThereErrors = true;
                Parser_errMSG += "Error in label: invalid character->> " + label.charAt(i);
                System.out.println("Error in label: invalid character->> " + label.charAt(i));
                break;
            }
        }
        return goodchar;
    }

    /**
     *
     * @param instr
     * @return
     */
    private boolean isDataTransOP(String instr) {
        boolean result = false;

        switch (instr.toLowerCase()) {
            case "lw":
                result = true;
                break;
            case "sw":
                result = true;
                break;
            case "lh":
                result = true;
                break;
            case "lhu":
                result = true;
                break;
            case "sh":
                result = true;
                break;
            case "lb":
                result = true;
                break;
            case "lbu":
                result = true;
                break;
            case "sb":
                result = true;
                break;

        }
        return result;
    }

    private boolean containChain(String str) {
        boolean contained = false;
        for (int i = 0; i < LoadOP.length; i++) {
            if (str.equals(LoadOP[i])) {
                contained = true;
                break;
            }
        }
        return contained;
    }

    private String[] split(String line) {
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

    private void checkFormat(String newLine) {
        String firstPart = "";
        char CHAR_;
        for (int i = 0; i < newLine.length(); i++) {
            CHAR_ = newLine.charAt(i);
            if (!isCharOk(CHAR_)) {
                Parser_errMSG += "ERROR CHAR NOT ALLOWED: >>> " + CHAR_ + " <<<";
                System.out.println("ERROR CHAR NOT ALLOWED: >>> " + CHAR_ + " <<<");
                isThereErrors = true;
                break;
            }
            if (CHAR_ == ':' && TAG == 'l') {//(CHAR_ == ' ' ||CHAR_ == ':')
                if (isOP(firstPart)) {
                    Parser_errMSG += "Label can not have instruction name";
                    System.out.println("Label can not have instruction name");
                    isThereErrors = true;
                }
                TAG = 'i';
                firstPart = "";
                continue;
            }
            if (CHAR_ == ',' && TAG == 'l') {//(CHAR_ == ' ' ||CHAR_ == ':')
                Parser_errMSG += "This label is bad!!!!->" + firstPart;
                System.out.println("This label is bad!!!!->" + firstPart);
                isThereErrors = true;
                break;
            }
            if (CHAR_ == ' ' && TAG == 'i') {
                if (!isOP(firstPart)) {
                    Parser_errMSG += "ERROR: bad instruction";
                    System.out.println("ERROR: bad instruction");
                    isThereErrors = true;
                } else {
                    TAG = 'x';
                    firstPart = "";
                    continue;
                }
            }
            if (CHAR_ == ',' && TAG == 'i') {
                if (!isOP(firstPart)) {
                    Parser_errMSG += "ERROR: bad instruction";
                    System.out.println("ERROR: bad instruction");
                    isThereErrors = true;
                } else {
                    Parser_errMSG += "ERROR: good instruction but register spected: ',' found";
                    System.out.println("ERROR: good instruction but register spected: ',' found");
                    isThereErrors = true;
                    TAG = 'x';
                    firstPart = "";
                    continue;
                }
            }
            firstPart += CHAR_;

        }
    }

    private void checkImm(String Imm) {
        try {
            int result = Integer.parseInt(Imm);
            if (!(result < 2048 && result >= -2048)) {//−(2 exp 11) to (2 exp 11)−1.
                Parser_errMSG += "Error: Immediate must be in limit −(2 exp 11) to (2 exp 11)−1.";
                System.out.println("Error: Immediate must be in limit −(2 exp 11) to (2 exp 11)−1.");
                isThereErrors = true;
            }
        } catch (Exception e) {
            Parser_errMSG += "Error: Bad Immediate format.";
            System.out.println("Error: Bad Immediate format.");
            isThereErrors = true;

        }
    }

    private void checkRegNumber(String number) {
        try {
            int result = Integer.parseInt(number);
            if (!(result < 32 && result >= 0)) {//−(2 exp 11) to (2 exp 11)−1.
                Parser_errMSG += "Error: Register must be in 0-31 range.";
                System.out.println("Error: Register must be in 0-31 range.");
                isThereErrors = true;
            }
        } catch (Exception e) {
            Parser_errMSG += "Error: Bad Register format.";
            System.out.println("Error: Bad Register format.");
            isThereErrors = true;

        }
    }

    private void checkReg(String xReg) {
        int tamano = xReg.length();
        if (tamano == 2) {
            char reg = xReg.charAt(0);
            if (reg == 'x' || reg == 'X') {
                if (!isNumberCharOk(xReg.charAt(1))) {
                    Parser_errMSG += "Error: Bad Register format.";
                    System.out.println("Error: Bad Register format.");
                    isThereErrors = true;
                }
            } else {
                Parser_errMSG += "Error: Bad input register waiting for 'x' found-> " + reg;
                System.out.println("Error: Bad input register waiting for 'x' found-> " + reg);
                isThereErrors = true;
            }
        } else if (tamano == 3) {
            char reg = xReg.charAt(0);
            if (reg == 'x' || reg == 'X') {
                String regNumber = "";
                regNumber += xReg.charAt(1);
                regNumber += xReg.charAt(2);
                checkRegNumber(regNumber);
            } else {
                Parser_errMSG += "Error: Bad Register format found 3-> " + reg;
                System.out.println("Error: Bad Register format found 3-> " + reg);
                isThereErrors = true;
            }
        } else {
            Parser_errMSG += "Error: Bad Register format.";
            System.out.println("Error: Bad Register format.");
            isThereErrors = true;
        }
    }

    private boolean isLabel(String label) {
        boolean isLabel = (label.charAt(label.length() - 1) == '*');
        return isLabel;
    }

    private boolean isCharOk(char i) {
        boolean isOk = false;
        for (int j = 0; j < alphabet.length; j++) {
            if (i == alphabet[j]) {
                isOk = true;
                break;
            }
        }
        return isOk;

    }

    private void checkLoadReg(String reg) {

        if (reg.length() < 4) {
            Parser_errMSG += "Error: Bad expresion format at ->" + reg;
            System.out.println("Error: Bad expresion format at ->" + reg);
            isThereErrors = true;
        } else if (reg.length() < 5 && reg.charAt(0) != '(') {
            Parser_errMSG += "Error: Bad expresion format at ->" + reg;
            System.out.println("Error: Bad expresion format at ->" + reg);
            isThereErrors = true;
        } else if (reg.charAt(reg.length() - 4) == '(' && reg.charAt(reg.length() - 1) == ')') {
            String xReg = "";
            xReg += reg.charAt(reg.length() - 3);
            xReg += reg.charAt(reg.length() - 2);
            checkReg(xReg);
            int range = reg.length() - 4;
            String suma = "";
            char number;
            for (int i = 0; i < range; i++) {
                suma += reg.charAt(i);
                if (!isNumberCharOk(reg.charAt(i))) {
                    Parser_errMSG += "Error: Bad number Imm";
                    System.out.println("Error: Bad number Imm");//implementar revision del tamano; maximo 4095
                    isThereErrors = true;
                }
            }

        } else if (reg.charAt(reg.length() - 5) == '(' && reg.charAt(reg.length() - 1) == ')') {
            String xReg = "";
            xReg += reg.charAt(reg.length() - 4);
            xReg += reg.charAt(reg.length() - 3);
            xReg += reg.charAt(reg.length() - 2);
            checkReg(xReg);
            int range = reg.length() - 5;
            for (int i = 0; i < range; i++) {
                if (!isNumberCharOk(reg.charAt(i))) {
                    Parser_errMSG += "Error: Bad number Imm";
                    System.out.println("Error: Bad number Imm");//implementar revision del tamano; maximo 4095
                    isThereErrors = true;
                }
            }
        } else {
            Parser_errMSG += "Error: Can't manage this expression -> " + reg;
            System.out.println("Error: Can't manage this expression -> " + reg);
            isThereErrors = true;
        }
    }

    private String[] checkDTI(String[] buffLine) {
        int fix = 0, index = 1;
        String[] buffLineOut = new String[buffLine.length + 1];
        String ImmVal = "";
        String RegVal = "";

        if (buffLine.length == 2) {
            buffLineOut = buffLine;
        } else {

            if (isLabel(buffLine[1])) {
                index = 2;
            }
            String instr = buffLine[index].toLowerCase();
            if (instr.equals("lw") || instr.equals("sw") || instr.equals("lh") || instr.equals("lhu")
                    || instr.equals("sh") || instr.equals("lb") || instr.equals("lbu") || instr.equals("sb")) {
                index += 2;
                if (containString(buffLine[index], ')')) {
                    fix = 1;
                } else {
                    buffLineOut = buffLine;
                }
            } else {
                buffLineOut = buffLine;
            }
            if (fix == 1) {
                String[] unFormat = fixImm(buffLine[index]).split("@");
                ImmVal += unFormat[0];
                RegVal += unFormat[1];
                int i = 0;
                for (i = 0; i < buffLine.length - 1; i++) {
                    buffLineOut[i] = buffLine[i];
                }
                buffLineOut[i] = RegVal;
                buffLineOut[i + 1] = ImmVal;
            }
        }
        return buffLineOut;
    }

    private boolean containString(String a, char b) {
        boolean isContained = false;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == b) {
                isContained = true;
                break;
            }
        }
        return isContained;
    }
    
        /**
     *
     * @param a
     * @return
     */
    private String fixImm(String a) {
        String result = "";
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == '(' || a.charAt(i) == ')') {
                result += "@";
            } else {
                result += a.charAt(i);
            }
        }
        return result;
    }

    private boolean isNumberCharOk(char i) {
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

    private boolean isOP(String label) {
        boolean isOk = false;
        for (int j = 0; j < Ops.length; j++) {
            if (Ops[j].equals(label)) {
                isOk = true;
                break;
            }
        }
        return isOk;

    }

    private String deleteWS(String line) {
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
                        isThereErrors = true;

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

    public boolean getErrorsFlag() {
        return isThereErrors;
    }

    public String[][] getInstructionBuffer() {
        return InstructionBuffer;
    }

    public int getLineCounter() {
        return line_counter;
    }
}
