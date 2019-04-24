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
public class RegisterMemory {

    private final RegisterWord x00;
    private final RegisterWord x01;
    private final RegisterWord x02;
    private final RegisterWord x03;
    private final RegisterWord x04;
    private final RegisterWord x05;
    private final RegisterWord x06;
    private final RegisterWord x07;
    private final RegisterWord x08;
    private final RegisterWord x09;
    private final RegisterWord x10;
    private final RegisterWord x11;
    private final RegisterWord x12;
    private final RegisterWord x13;
    private final RegisterWord x14;
    private final RegisterWord x15;
    private final RegisterWord x16;
    private final RegisterWord x17;
    private final RegisterWord x18;
    private final RegisterWord x19;
    private final RegisterWord x20;
    private final RegisterWord x21;
    private final RegisterWord x22;
    private final RegisterWord x23;
    private final RegisterWord x24;
    private final RegisterWord x25;
    private final RegisterWord x26;
    private final RegisterWord x27;
    private final RegisterWord x28;
    private final RegisterWord x29;
    private final RegisterWord x30;
    private final RegisterWord x31;
   

    public RegisterMemory() {
        x00 = new RegisterWord("x00", "00");
        x01 = new RegisterWord("x01", "04");
        x02 = new RegisterWord("x02", "08");
        x03 = new RegisterWord("x03", "12");
        x04 = new RegisterWord("x04", "16");
        x05 = new RegisterWord("x05", "20");
        x06 = new RegisterWord("x06", "24");
        x07 = new RegisterWord("x07", "28");
        x08 = new RegisterWord("x08", "32");
        x09 = new RegisterWord("x09", "36");
        x10 = new RegisterWord("x10", "40");
        x11 = new RegisterWord("x11", "44");
        x12 = new RegisterWord("x12", "48");
        x13 = new RegisterWord("x13", "52");
        x14 = new RegisterWord("x14", "56");
        x15 = new RegisterWord("x15", "60");
        x16 = new RegisterWord("x16", "64");
        x17 = new RegisterWord("x17", "68");
        x18 = new RegisterWord("x18", "72");
        x19 = new RegisterWord("x19", "76");
        x20 = new RegisterWord("x20", "80");
        x21 = new RegisterWord("x21", "84");
        x22 = new RegisterWord("x22", "88");
        x23 = new RegisterWord("x23", "92");
        x24 = new RegisterWord("x24", "96");
        x25 = new RegisterWord("x25", "100");
        x26 = new RegisterWord("x26", "104");
        x27 = new RegisterWord("x27", "108");
        x28 = new RegisterWord("x28", "112");
        x29 = new RegisterWord("x29", "116");
        x30 = new RegisterWord("x30", "120");
        x31 = new RegisterWord("x31", "124");
        initRegisters();

    }

    public String readRegisterValue(String registerName, int dataBase) {//data base: 2, 10, 16

        String result = "";
        RegisterWord Xn = null;
        switch (registerName) {
            case "x00":
                Xn = x00;
                break;
            case "x01":
                Xn = x01;
                break;
            case "x02":
                Xn = x02;
                break;
            case "x03":
                Xn = x03;
                break;
            case "x04":
                Xn = x04;
                break;
            case "x05":
                Xn = x05;
                break;
            case "x06":
                Xn = x06;
                break;
            case "x07":
                Xn = x07;
                break;
            case "x08":
                Xn = x08;
                break;
            case "x09":
                Xn = x09;
                break;
            case "x10":
                Xn = x10;
                break;
            case "x11":
                Xn = x11;
                break;
            case "x12":
                Xn = x12;
                break;
            case "x13":
                Xn = x13;
                break;
            case "x14":
                Xn = x14;
                break;
            case "x15":
                Xn = x15;
                break;
            case "x16":
                Xn = x16;
                break;
            case "x17":
                Xn = x17;
                break;
            case "x18":
                Xn = x18;
                break;
            case "x19":
                Xn = x19;
                break;
            case "x20":
                Xn = x20;
                break;
            case "x21":
                Xn = x21;
                break;
            case "x22":
                Xn = x22;
                break;
            case "x23":
                Xn = x23;
                break;
            case "x24":
                Xn = x24;
                break;
            case "x25":
                Xn = x25;
                break;
            case "x26":
                Xn = x26;
                break;
            case "x27":
                Xn = x27;
                break;
            case "x28":
                Xn = x28;
                break;
            case "x29":
                Xn = x29;
                break;
            case "x30":
                Xn = x30;
                break;
            case "x31":
                Xn = x31;
                break;
        }

        switch (dataBase) {

            case 2:
                result += Xn.getData_bin();
                break;
            case 10:
                result += Xn.getData_dec();
                break;
            case 16:
                result += Xn.getData_hex();
                break;    
            default:
                result += Xn.getData_dec();
        }

        return result;
    }

    public void recordRegisterValue(String registerName, String decValue) {
        String bin_val = Integer.toBinaryString(Integer.valueOf(decValue));
        String hex_val = Integer.toHexString(Integer.valueOf(decValue));
        RegisterWord Xn = null;

        switch (registerName) {
            case "x00":
                Xn = x00;
                break;
            case "x01":
                Xn = x01;
                break;
            case "x02":
                Xn = x02;
                break;
            case "x03":
                Xn = x03;
                break;
            case "x04":
                Xn = x04;
                break;
            case "x05":
                Xn = x05;
                break;
            case "x06":
                Xn = x06;
                break;
            case "x07":
                Xn = x07;
                break;
            case "x08":
                Xn = x08;
                break;
            case "x09":
                Xn = x09;
                break;
            case "x10":
                Xn = x10;
                break;
            case "x11":
                Xn = x11;
                break;
            case "x12":
                Xn = x12;
                break;
            case "x13":
                Xn = x13;
                break;
            case "x14":
                Xn = x14;
                break;
            case "x15":
                Xn = x15;
                break;
            case "x16":
                Xn = x16;
                break;
            case "x17":
                Xn = x17;
                break;
            case "x18":
                Xn = x18;
                break;
            case "x19":
                Xn = x19;
                break;
            case "x20":
                Xn = x20;
                break;
            case "x21":
                Xn = x21;
                break;
            case "x22":
                Xn = x22;
                break;
            case "x23":
                Xn = x23;
                break;
            case "x24":
                Xn = x24;
                break;
            case "x25":
                Xn = x25;
                break;
            case "x26":
                Xn = x26;
                break;
            case "x27":
                Xn = x27;
                break;
            case "x28":
                Xn = x28;
                break;
            case "x29":
                Xn = x29;
                break;
            case "x30":
                Xn = x30;
                break;
            case "x31":
                Xn = x31;
                break;
        }
        Xn.setData_bin(bin_val);
        Xn.setData_dec(decValue);
        Xn.setData_hex(hex_val);
    }

    public void initRegisters() {

        x00.setData_bin("0");
        x01.setData_bin("0");
        x02.setData_bin("0");
        x03.setData_bin("0");
        x04.setData_bin("0");
        x05.setData_bin("0");
        x06.setData_bin("0");
        x07.setData_bin("0");
        x08.setData_bin("0");
        x09.setData_bin("0");
        x10.setData_bin("0");
        x11.setData_bin("0");
        x12.setData_bin("0");
        x13.setData_bin("0");
        x14.setData_bin("0");
        x15.setData_bin("0");
        x16.setData_bin("0");
        x17.setData_bin("0");
        x18.setData_bin("0");
        x19.setData_bin("0");
        x20.setData_bin("0");
        x21.setData_bin("0");
        x22.setData_bin("0");
        x23.setData_bin("0");
        x24.setData_bin("0");
        x25.setData_bin("0");
        x26.setData_bin("0");
        x27.setData_bin("0");
        x28.setData_bin("0");
        x29.setData_bin("0");
        x30.setData_bin("0");
        x31.setData_bin("0");

        x00.setData_dec("0");
        x01.setData_dec("0");
        x02.setData_dec("0");
        x03.setData_dec("0");
        x04.setData_dec("0");
        x05.setData_dec("0");
        x06.setData_dec("0");
        x07.setData_dec("0");
        x08.setData_dec("0");
        x09.setData_dec("0");
        x10.setData_dec("0");
        x11.setData_dec("0");
        x12.setData_dec("0");
        x13.setData_dec("0");
        x14.setData_dec("0");
        x15.setData_dec("0");
        x16.setData_dec("0");
        x17.setData_dec("0");
        x18.setData_dec("0");
        x19.setData_dec("0");
        x20.setData_dec("0");
        x21.setData_dec("0");
        x22.setData_dec("0");
        x23.setData_dec("0");
        x24.setData_dec("0");
        x25.setData_dec("0");
        x26.setData_dec("0");
        x27.setData_dec("0");
        x28.setData_dec("0");
        x29.setData_dec("0");
        x30.setData_dec("0");
        x31.setData_dec("0");

        x00.setData_hex("0x0");
        x01.setData_hex("0x0");
        x02.setData_hex("0x0");
        x03.setData_hex("0x0");
        x04.setData_hex("0x0");
        x05.setData_hex("0x0");
        x06.setData_hex("0x0");
        x07.setData_hex("0x0");
        x08.setData_hex("0x0");
        x09.setData_hex("0x0");
        x10.setData_hex("0x0");
        x11.setData_hex("0x0");
        x12.setData_hex("0x0");
        x13.setData_hex("0x0");
        x14.setData_hex("0x0");
        x15.setData_hex("0x0");
        x16.setData_hex("0x0");
        x17.setData_hex("0x0");
        x18.setData_hex("0x0");
        x19.setData_hex("0x0");
        x20.setData_hex("0x0");
        x21.setData_hex("0x0");
        x22.setData_hex("0x0");
        x23.setData_hex("0x0");
        x24.setData_hex("0x0");
        x25.setData_hex("0x0");
        x26.setData_hex("0x0");
        x27.setData_hex("0x0");
        x28.setData_hex("0x0");
        x29.setData_hex("0x0");
        x30.setData_hex("0x0");
        x31.setData_hex("0x0");

    }
}
