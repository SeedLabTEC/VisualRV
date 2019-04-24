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
public class MemNode {
    private MemoryWord word;
    private MemNode nextWord;
    /**
     * Constructor que inicializamos el value de las variables.
     */
    public void MemNode(){
        this.word = null;
        this.nextWord = null;
    }
    
    // Métodos get y set para los atributos.
    
    public MemoryWord getWord() {
        return word;
    }

    public void setMemoryWord(MemoryWord word) {
        this.word = word;
    }

    public MemNode getNext() {
        return nextWord;
    }

    public void setNextWord(MemNode nextWord) {
        this.nextWord = nextWord;
    }   
}