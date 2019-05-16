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
    Memory mem;
    int lineCounter;
    int PC;//program counter
    int simulation_flag;// EXECUTE ALL = 1, EXECUTE_STEP = 2;
    boolean end_flag = false;

    public Simulation() {
        reg = new RegisterMemory();
        mem = new Memory();
        simulation_flag = 0;
    }

    public void startSimulation(String[][] InstructionBuffer, int lineCounter, int simulation_flag) {
        reg.initRegisters();
        //limpiar_memoria implementar
        this.InstructionBuffer = InstructionBuffer;
        PC = 0;
        this.lineCounter = lineCounter;
        this.simulation_flag = simulation_flag;
        execute();
    }

    public void setExecFlag(int flag) {
        this.simulation_flag = flag;

    }

    public void execute() {//fix PC=PC+4 siempre es corrido apesar de que una instruccion branch lo haya alterado

        if (simulation_flag == 1) {
            executeAll();
        } else if (simulation_flag == 2) {
            step_exec();
        }

    }

    private void executeAll() {

        for (PC = 0; PC < lineCounter * 4; PC = PC + 4) {//for (int i = 0; i < lineCounter; i++) 
            nextStep();
        }
    }

    public void step_exec() {
        if (PC < lineCounter * 4) {
            nextStep();
            PC = PC+4;
        }
        else if(PC == lineCounter*4){
            boolean end_flag = true;
        }
    }

    private void nextStep() {
        InstructionBuffer[PC / 4] = checkDTI(InstructionBuffer[PC / 4]);//check data transfer imm;
        for (int j = 0; j < InstructionBuffer[PC / 4].length; j++) {
            System.out.print(InstructionBuffer[PC / 4][j] + "~");
            //System.out.println("this tamaño -> " + InstructionBuffer[i].length);

        }
        System.out.println("end");
        if (InstructionBuffer[PC / 4].length == 2) {
            System.out.println("nothing to do...");
        } else {
            executeLine(InstructionBuffer[PC / 4]);
        }
    }

    private void instructionExecution(String x1, String x2, String x3, int instr_id, int imm_flag) {
        int uint, integer, label_index, x_01, x_02, x_03 = 0;
        int imm_b_type = 3;//branches x3 operand--->  1-> imm, 2-> register 3->label
        String value;
        x_02 = Integer.valueOf(reg.readRegisterValue(x2, 10));//valor x2

        if (imm_flag == 1 && isNumber(x3)) {
            x_03 = Integer.valueOf(x3);//valor x3//fix this for branch only not working
            imm_b_type = 1;
        } else if (isRegister(x3)) {
            x_03 = Integer.valueOf(reg.readRegisterValue(x3, 10));//valor x3
            imm_b_type = 2;
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
            case 10://lw
                value = mem.getFromMemory(x_02 + x_03, 10);
                reg.recordRegisterValue(x1, value);
                break;
            case 11://lwu
                value = mem.getFromMemory(x_02 + x_03, 10);
                reg.recordRegisterValue(x1, Integer.toUnsignedString(Integer.valueOf(value)));
                break;
            case 12:
                break;
            case 13:
                break;
            case 14://lh
                value = mem.getFromMemory(x_02 + x_03, 10);
                integer = Integer.valueOf(value) & 65535;//apply erase half word
                reg.recordRegisterValue(x1, String.valueOf(integer));
                break;
            case 15://lhu
                value = mem.getFromMemory(x_02 + x_03, 10);
                uint = toUnsignedInt(Integer.valueOf(value));
                integer = uint & 65535;//
                reg.recordRegisterValue(x1, String.valueOf(integer));
                break;
            case 16://lb
                value = mem.getFromMemory(x_02 + x_03, 10);
                integer = Integer.valueOf(value) & 255;//apply erase half word
                reg.recordRegisterValue(x1, String.valueOf(integer));
                break;
            case 17://lbu
                value = mem.getFromMemory(x_02 + x_03, 10);
                uint = toUnsignedInt(Integer.valueOf(value));
                integer = uint & 255;//
                reg.recordRegisterValue(x1, String.valueOf(integer));
                break;
            case 18://sw
                mem.storeWord(x_02 + x_03, reg.readRegisterValue(x1, 10));//int address/String value
                break;
            case 19://sb
                integer = Integer.valueOf(reg.readRegisterValue(x1, 10)) & 255;//String
                mem.storeWord(x_02 + x_03, String.valueOf(integer));//int address/String value
                break;
            case 20://sh
                integer = Integer.valueOf(reg.readRegisterValue(x1, 10)) & 65535;//String
                mem.storeWord(x_02 + x_03, String.valueOf(integer));//int address/String value
                break;
            case 21://beq
                x_01 = Integer.valueOf(reg.readRegisterValue(x1, 10));//valor x1
                if (x_01 == x_02) {
                    setPC(x3, x_03, imm_b_type);
                    System.out.print("setting PC beq, x3: " + x_03 + " imm_flag: " + imm_b_type + " ");
                }
                break;
            case 22:////bne
                x_01 = Integer.valueOf(reg.readRegisterValue(x1, 10));//valor x1
                if (x_01 != x_02) {
                    setPC(x3, x_03, imm_b_type);
                    //System.out.print("setting PC bne");
                }
                break;
            case 23://blt
                x_01 = Integer.valueOf(reg.readRegisterValue(x1, 10));//valor x1
                if (x_01 < x_02) {
                    setPC(x3, x_03, imm_b_type);
                    //System.out.print("setting PC blt");
                }
                break;
            case 24://bge
                x_01 = Integer.valueOf(reg.readRegisterValue(x1, 10));//valor x1
                if (x_01 >= x_02) {
                    setPC(x3, x_03, imm_b_type);
                    //System.out.print("setting PC bge");
                }
                break;
            case 25://bltu  unsigned not implemented yet
                x_01 = Integer.valueOf(reg.readRegisterValue(x1, 10));//valor x1
                if (x_01 == x_02) {
                    //setPC(x3,imm_flag);
                    //System.out.print("setting PC bltu");
                }
                break;
            case 26://bgeu unsigned not implemented yet
                x_01 = Integer.valueOf(reg.readRegisterValue(x1, 10));//valor x1
                if (x_01 == x_02) {
                    //setPC(x3,imm_flag);
                    //System.out.print("setting PC bgeu");
                }
                break;
        }

    }

    private void setPC(String label, int imm_val, int imm_type) {
        if (imm_type == 1 || imm_type == 2) {
            PC = PC + imm_val - 4;
        } else {
            setPCtoLabel(label);
        }

    }

    private void setPCtoLabel(String label) {
        int temp_pc = PC;
        String label_ = label + "*";
        boolean flag_pc_found = false;
        for (PC = 0; PC < lineCounter * 4; PC = PC + 4) {
            if (label_.equals(InstructionBuffer[PC / 4][1])) {
                flag_pc_found = true;
                PC -= 4;
                break;
            }
        }
        if (!flag_pc_found) {
            PC = temp_pc;
            System.out.println("LABEL <<" + label + ">> NOT FOUND");
        }
    }

    private boolean isNumber(String num) {
        boolean result = true;//true
        for (int i = 0; i < num.length(); i++) {
            switch (num.charAt(i)) {
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
                default:
                    result = false;
                    break;
            }
            if (result == false) {
                break;
            }
        }
        return result;

    }

    private boolean isRegister(String num) {
        boolean result = true;//true

        switch (num) {
            case "x00":
                break;
            case "x01":
                break;
            case "x02":
                break;
            case "x03":
                break;
            case "x04":
                break;
            case "x05":
                break;
            case "x06":
                break;
            case "x07":
                break;
            case "x08":
                break;
            case "x09":
                break;
            case "x0":
                break;
            case "x1":
                break;
            case "x2":
                break;
            case "x3":
                break;
            case "x4":
                break;
            case "x5":
                break;
            case "x6":
                break;
            case "x7":
                break;
            case "x8":
                break;
            case "x9":
                break;
            case "x10":
                break;
            case "x11":
                break;
            case "x12":
                break;
            case "x13":
                break;
            case "x14":
                break;
            case "x15":
                break;
            case "x16":
                break;
            case "x17":
                break;
            case "x18":
                break;
            case "x19":
                break;
            case "x20":
                break;
            case "x21":
                break;
            case "x22":
                break;
            case "x23":
                break;
            case "x24":
                break;
            case "x25":
                break;
            case "x26":
                break;
            case "x27":
                break;
            case "x28":
                break;
            case "x29":
                break;
            case "x30":
                break;
            case "x31":
                break;
            default:
                result = false;
                break;
        }
        return result;

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
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 10, 1);
                break;
            case "lwu":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 11, 1);
                break;
            case "sw":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 18, 1);
                break;
            case "sb":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 19, 1);
                break;
            case "lh":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 14, 1);
                break;
            case "lhu":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 15, 1);
                break;
            case "sh":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 20, 1);
                break;
            case "lb":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 16, 1);
                break;
            case "lbu":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 17, 1);
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
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 21, 1);
                break;
            case "bne":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 22, 1);
                break;
            case "blt":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 23, 1);
                break;
            case "bge":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 24, 1);
                break;
            case "bltu":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 25, 1);
                break;
            case "bgeu":
                instructionExecution(buffLine[index], buffLine[index + 1], buffLine[index + 2], 26, 1);
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
        ///System.out.println("");
        boolean isLabel = (label.charAt(label.length() - 1) == '*');
        return isLabel;
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
