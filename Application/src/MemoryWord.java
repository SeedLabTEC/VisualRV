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
public class MemoryWord {
    private final int address;
    private  String data_bin;
    private  String data_dec;
    private  String data_hex;
    
    public MemoryWord(int address, String dec_val){
   
        this.address = address;
        this.data_dec = dec_val;
        this.data_bin = Integer.toBinaryString(Integer.valueOf(dec_val));
        this.data_hex = Integer.toHexString(Integer.valueOf(dec_val));
    }

    public int getAddress() {
        return address;
    }

    public String getData_bin() {
        return data_bin;
    }

    public String getData_dec() {
        return data_dec;
    }

    public String getData_hex() {
        return data_hex;
    }

    public void setData_bin(String data_bin) {
        this.data_bin = data_bin;
    }

    public void setData_dec(String data_dec) {
        this.data_dec = data_dec;
    }

    public void setData_hex(String data_hex) {
        this.data_hex = data_hex;
    }
    
}
