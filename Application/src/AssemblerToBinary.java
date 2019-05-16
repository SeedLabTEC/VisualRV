/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvsomecode;

/**
 *
 * @author daniel
 */
public class AssemblerToBinary {

    String[][] InstructionBuffer;
    String[] R_inst = {"SLL", "SRL", "SRA", "ADD", "SUB", "XOR", "OR", "AND", "SLT", "SLTU", "sll", "srl", "sra", "add", "sub", "xor", "or", "and",
        "slt", "sltu"};
    String[] I_inst = {"LB", "LH", "LW", "LBU", "LHU", "SLLI", "SRLI", "SRAI", "ADDI", "XORI", "ORI", "ANDI", "SLTI", "SLTIU", "lb", "lh", "lw", "lbu", "lhu",
        "slli", "srli", "srai", "addi", "xori", "ori", "andi", "slti", "sltiu"};
    String[] S_inst = {"SB", "SH", "SW", "sb", "sh", "sw"};
    String[] SB_inst = {"BEQ", "BNE", "BLT", "BGE", "BLTU", "BGEU", "beq", "bne", "blt", "bge", "bltu", "bgeu"};
    String[] U_inst = {"LUI", "AUIPC", "lui", "auipc"};
    String[] UJ_inst = {"JAL", "JALR", "jal", "jalr"};

    ;

    public AssemblerToBinary(String[][] InstructionBuffer) {
        this.InstructionBuffer = InstructionBuffer;
        //printInstructionBuffer();
        //convertAll();
        //convertLine(1);//index

    }
    
    public void convertAll(){
        int bufferLenght = InstructionBuffer.length;
        for (int i = 1; i < bufferLenght; i++) {
            convertLine(i);
        }
    }

    public void convertLine(int index_) {
        int index = index_-1;
        String type = "";
        
        int i = (isLabel(InstructionBuffer[index][1])) ? 2 : 1;
        type = verifyInstrType(InstructionBuffer[index][i]);

        switch (type) {
            case "R":
                System.out.println("the line "+index_+" binary code is "+createR_Type(i, index));
                break;
            case "I":
                System.out.println("the line "+index_+" binary code is "+createI_Type(i, index));
                break;
            case "S":
                //createS_Type(line,i);
                break;
            case "SB":
                //createSB_Type(line,i);
                break;

            case "U":
                //createU_Type(line,i);
                break;

            case "UJ":
                //createUJ_Type(line,i);
                break;
            case "-":
                System.out.println("Instruction not found<<" + InstructionBuffer[index][i] + ">>");

        }
        System.out.println("This is type " + type);

    }

    public String createR_Type(int index, int lineNumber) {
        String opCode = "0110011";
        String func701 = "0000000";
        String func702 = "0100000";
        String binary = "";
        String instr = InstructionBuffer[lineNumber][index];
        String rd = convertRegister(InstructionBuffer[lineNumber][index + 1]);
        String r2 = convertRegister(InstructionBuffer[lineNumber][index + 3]);
        String r1 = convertRegister(InstructionBuffer[lineNumber][index + 2]);
        if (instr.equals("sll") || instr.equals("SLL")) {
            binary += func701 + r2 + r1 + "001" + rd + opCode;
        } else if (instr.equals("srl") || instr.equals("SRL")) {
            binary += func701 + r2 + r1 + "101" + rd + opCode;
        } else if (instr.equals("sra") || instr.equals("SRA")) {
            binary += func702 + r2 + r1 + "101" + rd + opCode;
        } else if (instr.equals("add") || instr.equals("ADD")) {
            binary += func701 + r2 + r1 + "000" + rd + opCode;
        } else if (instr.equals("sub") || instr.equals("SUB")) {
            binary += func702 + r2 + r1 + "000" + rd + opCode;
        } else if (instr.equals("xor") || instr.equals("XOR")) {
            binary += func701 + r2 + r1 + "100" + rd + opCode;
        } else if (instr.equals("or") || instr.equals("OR")) {
            binary += func701 + r2 + r1 + "110" + rd + opCode;
        } else if (instr.equals("and") || instr.equals("AND")) {
            binary += func701 + r2 + r1 + "111" + rd + opCode;
        } else if (instr.equals("slt") || instr.equals("SLT")) {
            binary += func701 + r2 + r1 + "010" + rd + opCode;
        } else if (instr.equals("sltu") || instr.equals("SLTU")) {
            binary += func701 + r2 + r1 + "011" + rd + opCode;
        }
        return binary;
    }

    public String createI_Type(int index, int lineNumber) {
        String opCode = "0010011";
        String opCodeL = "0000011";
        String binary = "";
        String instr = InstructionBuffer[lineNumber][index];
        String rd = convertRegister(InstructionBuffer[lineNumber][index + 1]);
        String imm = convertRegister(InstructionBuffer[lineNumber][index + 3]);//pasar de decimal a binario
        String r1 = convertRegister(InstructionBuffer[lineNumber][index + 2]);
        if (instr.equals("lb") || instr.equals("LB")) {
            binary += imm + r1 + "000" + rd + opCodeL;
        } 
        else if (instr.equals("lh") || instr.equals("LH")) {
            binary += imm + r1 + "001" + rd + opCodeL;
        }
        else if (instr.equals("lw") || instr.equals("LW")) {
            binary += imm + r1 + "010" + rd + opCodeL;
        }
        else if (instr.equals("lbu") || instr.equals("LBU")) {
            binary += imm + r1 + "100" + rd + opCodeL;
        }
        else if (instr.equals("lhu") || instr.equals("LHU")) {
            binary += imm + r1 + "101" + rd + opCodeL;
        }
        else if (instr.equals("addi") || instr.equals("ADDI")) {
            binary += imm + r1 + "000" + rd + opCode;
        }
        else if (instr.equals("slti") || instr.equals("SLTI")) {
            binary += imm + r1 + "010" + rd + opCode;
        }
        else if (instr.equals("sltiu") || instr.equals("SLTIU")) {
            binary += imm + r1 + "011" + rd + opCode;
        }
        else if (instr.equals("xori") || instr.equals("XORI")) {
            binary += imm + r1 + "100" + rd + opCode;
        }
        else if (instr.equals("ori") || instr.equals("ORI")) {
            binary += imm + r1 + "110" + rd + opCode;
        }
        else if (instr.equals("andi") || instr.equals("ANDI")) {
            binary += imm + r1 + "111" + rd + opCode;
        }
        else if (instr.equals("slli") || instr.equals("SLLI")) {
            binary += "0000000"+imm + r1 + "001" + rd + opCode;
        }
        else if (instr.equals("srli") || instr.equals("SRLI")) {
            binary += "0000000"+imm + r1 + "101" + rd + opCode;
        }
        else if (instr.equals("srai") || instr.equals("SRAI")) {
            binary += "0100000"+imm + r1 + "101" + rd + opCode;
        }
        return binary;
    }

    private String convertRegister(String reg) {
        String binary = "";
        if (reg.equals("x0") || reg.equals("X0")) {
            binary += "00000";
        } else if (reg.equals("x1") || reg.equals("X1")) {
            binary += "00001";
        } else if (reg.equals("x2") || reg.equals("X2")) {
            binary += "00010";
        } else if (reg.equals("x3") || reg.equals("X3")) {
            binary += "00011";
        } else if (reg.equals("x4") || reg.equals("X4")) {
            binary += "00100";
        } else if (reg.equals("x5") || reg.equals("X5")) {
            binary += "00101";
        } else if (reg.equals("x6") || reg.equals("X6")) {
            binary += "00110";
        } else if (reg.equals("x7") || reg.equals("X7")) {
            binary += "00111";
        } else if (reg.equals("x8") || reg.equals("X8")) {
            binary += "01000";
        } else if (reg.equals("x9") || reg.equals("X9")) {
            binary += "01001";
        } else if (reg.equals("x10") || reg.equals("X10")) {
            binary += "01010";
        } else if (reg.equals("x11") || reg.equals("X11")) {
            binary += "01011";
        } else if (reg.equals("x12") || reg.equals("X12")) {
            binary += "01100";
        } else if (reg.equals("x13") || reg.equals("X13")) {
            binary += "01101";
        } else if (reg.equals("x14") || reg.equals("X14")) {
            binary += "01110";
        } else if (reg.equals("x15") || reg.equals("X15")) {
            binary += "01111";
        } else if (reg.equals("x16") || reg.equals("X16")) {
            binary += "10000";
        } else if (reg.equals("x17") || reg.equals("X17")) {
            binary += "10001";
        } else if (reg.equals("x18") || reg.equals("X18")) {
            binary += "10010";
        } else if (reg.equals("x19") || reg.equals("X19")) {
            binary += "10011";
        } else if (reg.equals("x20") || reg.equals("X20")) {
            binary += "10100";
        } else if (reg.equals("x21") || reg.equals("X21")) {
            binary += "10101";
        } else if (reg.equals("x22") || reg.equals("X22")) {
            binary += "10110";
        } else if (reg.equals("x23") || reg.equals("X23")) {
            binary += "10111";
        } else if (reg.equals("x24") || reg.equals("X24")) {
            binary += "11000";
        } else if (reg.equals("x25") || reg.equals("X25")) {
            binary += "11001";
        } else if (reg.equals("x26") || reg.equals("X26")) {
            binary += "11010";
        } else if (reg.equals("x27") || reg.equals("X27")) {
            binary += "11011";
        } else if (reg.equals("x28") || reg.equals("X28")) {
            binary += "11100";
        } else if (reg.equals("x29") || reg.equals("X29")) {
            binary += "11101";
        } else if (reg.equals("x30") || reg.equals("X30")) {
            binary += "11110";
        } else if (reg.equals("x31") || reg.equals("X31")) {
            binary += "11111";
        } else {
            binary += "<immediate value>";
        }

        return binary;

    }

    public void printInstructionBuffer() {
        for (int y = 0; y < InstructionBuffer.length - 3; y++) {
            if (InstructionBuffer[y][0] == null) {
                break;
            } else {
                for (int k = 0; k < InstructionBuffer[y].length; k++) {

                    System.out.print(InstructionBuffer[y][k] + "|");

                }
            }
            System.out.println("");
        }

    }

    public boolean isLabel(String label) {
        boolean isLabel = (label.charAt(label.length() - 1) == '*');
        return isLabel;
    }

    public String verifyInstrType(String instr) {
        String instrType = "-";
        for (int i = 0; i < R_inst.length; i++) {
            if (R_inst[i].equals(instr)) {
                //instrType = "R";
                return "R";
            }
        }
        for (int j = 0; j < I_inst.length; j++) {
            if (I_inst[j].equals(instr)) {
                //instrType = "R";
                return "I";
            }
        }
        for (int k = 0; k < S_inst.length; k++) {
            if (S_inst[k].equals(instr)) {
                //instrType = "R";
                return "S";
            }
        }
        for (int l = 0; l < S_inst.length; l++) {
            if (SB_inst[l].equals(instr)) {
                //instrType = "R";
                return "SB";
            }
        }
        for (int m = 0; m < S_inst.length; m++) {
            if (U_inst[m].equals(instr)) {
                //instrType = "R";
                return "U";
            }
        }
        for (int n = 0; n < S_inst.length; n++) {
            if (UJ_inst[n].equals(instr)) {
                //instrType = "R";
                return "UJ";
            }
        }
        return instrType;

    }

    public int getInstrCount() {
        int count = 0;
        for (int i = 0; i < InstructionBuffer.length; i++) {
            if (InstructionBuffer[i][0] == null) {
                break;
            } else {
                count++;
            }

        }
        return count;
    }
}
