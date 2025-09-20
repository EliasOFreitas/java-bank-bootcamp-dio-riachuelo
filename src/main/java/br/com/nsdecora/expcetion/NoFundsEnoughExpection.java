package br.com.nsdecora.expcetion;

public class NoFundsEnoughExpection extends RuntimeException {

    public NoFundsEnoughExpection(String message) {
        super(message);
    }


}
