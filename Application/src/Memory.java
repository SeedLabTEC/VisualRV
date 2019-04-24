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
public class Memory {

    private MemNode start;
    private int lenght;

    public void Memory() {
        start = null;
        lenght = 0;
    }

    /**
     * Consulta si la lista esta vacia.
     *
     * @return true si el primer MemNode (inicio), no apunta a otro MemNode.
     */
    public boolean isEmpty() {
        return start == null;
    }

    /**
     * Consulta cuantos elementos (MemNodes) tiene la lista.
     *
     * @return numero entero entre [0,n] donde n es el numero de elementos que
     * contenga la lista.
     */
    public int getTamanio() {
        return lenght;
    }

    public void storeWord(int address, String value) {
        int value_int = Integer.valueOf(value);
        MemoryWord wrd = new MemoryWord(address, value);
        MemNode newNode = new MemNode();
        newNode.setMemoryWord(wrd);

        if (isEmpty()) {
            start = newNode;
        } else {
            MemNode aux = start;

            // Recorre la lista hasta llegar al ultimo MemNode.
            int currentvalue, nextvalue = -1;
            //0,1,2    insert
            //0,2,1    real
            // tmp
            //0->1->null
            currentvalue = address;//0

            if (currentvalue > aux.getWord().getAddress()) {
                while (aux.getNext() != null) {
                    nextvalue = aux.getNext().getWord().getAddress();//1
                    if (currentvalue > nextvalue) {
                        aux = aux.getNext();
                    } else {
                        break;
                    }
                }
            }
            if(currentvalue < aux.getWord().getAddress()){
                newNode.setNextWord(aux);
                start = newNode;
                lenght++;
            }
            else if (currentvalue == aux.getWord().getAddress()) {
                aux.getWord().setData_bin(Integer.toBinaryString(Integer.valueOf(value)));
                aux.getWord().setData_dec(value);
                aux.getWord().setData_hex(Integer.toHexString(Integer.valueOf(value)));
            } else if (currentvalue == nextvalue) {
                aux.getNext().getWord().setData_bin(Integer.toBinaryString(Integer.valueOf(value)));
                aux.getNext().getWord().setData_dec(value);
                aux.getNext().getWord().setData_hex(Integer.toHexString(Integer.valueOf(value)));
            } else if (aux.getNext() != null) {
                newNode.setNextWord(aux.getNext());
                aux.setNextWord(newNode);
                lenght++;
            } else {
                aux.setNextWord(newNode);
                lenght++;
            }
        }

    }

    public void printMemoryValues() {
        MemNode aux = start;

        if (isEmpty()) {
            System.out.println("empty memory");
        } else {
            for (int i = 0; i <= lenght; i++) {
                System.out.println("Address(" + aux.getWord().getAddress() + ") : Value (" + aux.getWord().getData_dec() + ")");
                aux = aux.getNext();
            }
        }

    }
}
