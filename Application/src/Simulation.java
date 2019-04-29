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
public class Simulation {

    String[][] InstructionBuffer;
    RegisterMemory reg;
    //reg.recordRegisterValue("x01", "26");

    public Simulation(String[][] InstructionBuffer) {
        reg = new RegisterMemory();
        this.InstructionBuffer = InstructionBuffer;
        executeAll();
    }

    public void executeAll() {
        for (int i = 0; i < 30; i++) {
            InstructionBuffer[i] = checkDTI(InstructionBuffer[i]);//check data transfer imm;
            for (int j = 0; j < InstructionBuffer[i].length; j++) {
                System.out.print(InstructionBuffer[i][j] + "~");
                //System.out.println("this tamaño -> " + InstructionBuffer[i].length);

            }
            executeLine(InstructionBuffer[i]);
            System.out.println("");
        }
    }

    private void instructionExecution(String x1, String x2, String x3, int instr_id, int imm_flag) {
        int x_02, x_03 = 0;
        x_02 = Integer.valueOf(reg.readRegisterValue(x2, 10));//valor x2
        if (imm_flag == 1) {
            x_03 = Integer.valueOf(x3);//valor x3
        } else {
            x_03 = Integer.valueOf(reg.readRegisterValue(x3, 10));//valor x3
        }
        switch (instr_id) {
            case 0://add
                reg.recordRegisterValue(x1, String.valueOf(x_02 + x_03));
                break;
            case 1://sub
                reg.recordRegisterValue(x1, String.valueOf(x_02 - x_03));
                break;
            case 2://xor
                reg.recordRegisterValue(x1, String.valueOf(x_02 ^ x_03));
                break;
            case 3://or
                reg.recordRegisterValue(x1, String.valueOf(x_02 | x_03));
                break;
            case 4://and
                reg.recordRegisterValue(x1, String.valueOf(x_02 & x_03));
                break;
            case 5://slt
                int set = (x_02 < x_03) ? 1 : 0;
                reg.recordRegisterValue(x1, String.valueOf(set));
                break;
            case 6://sltu
                int x02 = toUnsignedInt(x_02);
                int x03 = toUnsignedInt(x_03);
                int set_u = (x02 < x03) ? 1 : 0;
                reg.recordRegisterValue(x1, String.valueOf(set_u));
                break;
            case 7://sll
                reg.recordRegisterValue(x1, String.valueOf(x_02 << x_03));
                break;
            case 8://srl
                reg.recordRegisterValue(x1, String.valueOf(x_02 >>> x_03));
                break;
            case 9://sra
                reg.recordRegisterValue(x1, String.valueOf(x_02 >> x_03));
                break;

        }

    }

    private void executeLine(String[] buffLine) {
        boolean flag_label = isLabel(buffLine[1]);
        String instruction = getInstruction(buffLine, flag_label);
        int index = (flag_label == true) ? 3 : 2;

        switch (instruction.toLowerCase()) {
            case "add":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 0, 0);
                break;
            case "sub":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 1, 0);
                break;
            case "addi":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 0, 1);
                break;
            case "lw":
                break;
            case "lwu":
                break;
            case "sw":
                break;
            case "sb":
                break;
            case "lh":
                break;
            case "lhu":
                break;
            case "sh":
                break;
            case "lb":
                break;
            case "lbu":
                break;
            case "lui":
                break;
            case "and":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 4, 0);
                break;
            case "or":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 3, 0);
                break;
            case "xor":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 2, 0);
                break;
            case "andi":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 4, 1);
                break;
            case "ori":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 3, 1);
                break;
            case "xori":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 2, 1);
                break;
            case "sll":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 7, 0);
                break;
            case "srl":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 8, 0);
                break;
            case "sra":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 9, 0);
                break;
            case "slli":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 7, 1);
                break;
            case "srli":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 8, 1);
                break;
            case "srai":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 9, 1);
                break;
            case "beq":
                break;
            case "bne":
                break;
            case "blt":
                break;
            case "bge":
                break;
            case "bltu":
                break;
            case "bgeu":
                break;
            case "jal":
                break;
            case "jalr":
                break;
            default:
                System.out.println("defaulklt");
        }
    }

    /**
     * Convert the signed decimal input to unsigned decimal
     *
     * @param int integer_value
     * @return unsigned int value
     */
    private int toUnsignedInt(int signed_integer_value) {
        String unsignedIntString = Integer.toUnsignedString(signed_integer_value);
        int unsignedInt = Integer.valueOf(unsignedIntString);
        return unsignedInt;
    }

    private String getInstruction(String[] buffLine, boolean flag_lbl) {
        String instr = "";
        if (flag_lbl) {
            instr = buffLine[2];
        } else {
            instr = buffLine[1];
        }
        return instr;
    }

    private boolean isLabel(String label) {
        boolean isLabel = (label.charAt(label.length() - 1) == '*');
        return isLabel;
    }

    private String[] checkDTI(String[] buffLine) {
        int fix = 0, index = 1;
        String[] buffLineOut = new String[buffLine.length + 1];
        String ImmVal = "";
        String RegVal = "";
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
        return buffLineOut;
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

}
